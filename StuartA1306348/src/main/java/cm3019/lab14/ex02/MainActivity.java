package cm3019.lab14.ex02;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cm3019.lab14.ex02.database.DbHelper;
import cm3019.lab14.ex02.database.TaskDataSource;
import cm3019.lab14.ex02.rssreader.RssItem;
import cm3019.lab14.ex02.rssreader.RssLoader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<RssItem>> {

    private EditText mEditText;
    private Button mSwitchButton;
    private Button mDateSelect;
    private Button mDeleteDatabase;
    private Button mDisplayDatabase;

    private MainArrayAdapter mAdapter;
    private ListView mListView ;

    //URL that will be used for single feed
    private String mDataSource;
    //List that will be used in the case of multiple feeds
    private ArrayList<String> mLoadSources;
    // no search string at the moment
    private String mSearchString = "";

    //Date that will passed into parseHandler eventually for filtering articles out
    private Date mSearchDate;

    //Flag that will indicate whether there are multiple fields or not
    private boolean loadAll;

   //To be used in date picker
    private Calendar calendar;
    private int day, month, year;

    //Format the search date
    DateFormat df;

    private static final int LOADER_ID = 1;
    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<List<RssItem>> mCallbacks;
    private LoaderManager mLoaderManager;

    //List that will be bound to the adapter
    private ArrayList<RssItem> articles = new ArrayList<>();

    //Database stuff
    //Made this public static as I was struggling to pass it through via other means
    public static TaskDataSource mTaskSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditText = (EditText)findViewById(R.id.editText);
        mSwitchButton = (Button) findViewById(R.id.switchButton);
        mDateSelect = (Button) findViewById(R.id.dateButton);
        mDeleteDatabase = (Button) findViewById(R.id.clearDatabase);
        mDisplayDatabase = (Button) findViewById(R.id.displayDatabase);

        mTaskSource = new TaskDataSource(this);
        mTaskSource.open();

        //Format that will be used for the search date
        df = new SimpleDateFormat("dd MM yyyy HH:mm:ss");

        //Default value for the search date
        try {
            mSearchDate = df.parse("01 01 1950  00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar = Calendar.getInstance();
        //Set variables to today's date
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        mAdapter = new MainArrayAdapter(this, articles);
        mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        TextView emptyText = (TextView)findViewById(R.id.textView);
        mListView.setEmptyView(emptyText);
        // Set list view item click listener
        mListView.setOnItemClickListener(new ListListener(/*data,*/ this));

        // The Activity (which implements the LoaderCallbacks<Cursor>
        // interface) is the callbacks object through which we will interact
        // with the LoaderManager. The LoaderManager uses this object to
        // instantiate the Loader and to notify the client when data is made
        // available/unavailable.
        mCallbacks = this;

        mLoadSources = new ArrayList<>();

        // Initialize the Loader with id '1' and callbacks 'mCallbacks'.
        // If the loader doesn't already exist, one is created. Otherwise,
        // the already created Loader is reused. In either case, the
        // LoaderManager will manage the Loader across the Activity/Fragment
        // lifecycle, will receive any new loads once they have completed,
        // and will report this new data back to the 'mCallbacks' object.
        mLoaderManager = getLoaderManager();

        SharedPreferences appHistory = this.getSharedPreferences("RSSFeeds", 0);

        loadAll = appHistory.getBoolean("loadAll", false);

        //Check to see if the user has preferred URL(s) and use this to load data
        if (appHistory.contains("preferred_URL") && !loadAll) {
            mDataSource = appHistory.getString("preferred_URL", "");
            mLoaderManager.initLoader(LOADER_ID, null, mCallbacks);
        }
        else if(loadAll){
            Set<String> addressSet = new LinkedHashSet<>();
            addressSet.addAll(appHistory.getStringSet("URL_address", addressSet));
            Iterator iterator1 = addressSet.iterator();
            while (iterator1.hasNext()){
                mLoadSources.add((String) iterator1.next());
            }
            for(int x = 0; x < mLoadSources.size(); x++){
                mLoaderManager.initLoader(x, null, mCallbacks);
            }
        };

        //Handler for switch activity button click
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity2.class));
            }
        });

        //Listener for button that will delete all entries in the database
        mDeleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskSource.deleteAll();
            }
        });

        //Output the contents of the database to the log
        mDisplayDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<RssItem> list = mTaskSource.read();

                for(int x = 0; x < list.size(); x++){
                    Log.d("database", list.get(x).getTitle());
                    Log.d("database", list.get(x).getDesc());
                    Log.d("database", list.get(x).getDate());
                    Log.d("database", list.get(x).getLink());
                    Log.d("database", " ");
                }
            }
        });

        //Button that will reveal date picking dialogue
        mDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(999);

            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

            // arg1 = year
            // arg2 = month
            // arg3 = day

            //Month variable goes from 0 to 11 therefore must add 1 to go from 0 to 12
            arg2 = arg2 + 1;

            //Add padding if the month or day dates are single digit
            String newArg2 = "" + arg2;
            if(newArg2.length() == 1){
                newArg2 = 0 + newArg2;
            }
            String newArg1 = "" + arg1;
            if(newArg1.length() == 1){
                newArg1 = 0 + newArg1;
            }

            String tempDate = arg3 + " " + newArg2 + " " + newArg1 + " 00:00:00";
            try {
                mSearchDate = df.parse(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), tempDate, Toast.LENGTH_LONG).show();
        }
    };


    // handler for search button click
    public void onClick(View v){
        //Clear the current contents of the database
        mTaskSource.deleteAll();

        mSearchString = mEditText.getText().toString();

        if(!loadAll) {
            mLoaderManager.restartLoader(LOADER_ID, null, mCallbacks);
        }
        else {
            for(int x = 0; x < mLoadSources.size(); x++){
                mLoaderManager.restartLoader(x, null, mCallbacks);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<RssItem>> onCreateLoader(int id, Bundle args) {

        if(loadAll){
            mDataSource = mLoadSources.get(id);
        }

        RssLoader loader = new RssLoader(
                this, // context
                mDataSource, // URL of Rss feed
                mSearchString, // search loaded RssItem for match in title
                mSearchDate    // search loaded item to check date is after search date
        );
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<RssItem>> loader, List<RssItem> data) {
        articles = (ArrayList)data;
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<RssItem>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mTaskSource.close();
    }

}

package cm3019.lab14.ex02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Activity2 extends AppCompatActivity {

    private Button mBackButton;
    private Button mLoadAllButton;

    private ArrayList<RSSFeed> modules;
    private ArrayList<String> URL_Address;
    private ArrayList<String> URL_Name;
    private Set<String> addressSet;
    private Set<String> nameSet;
    private EditText code;
    private EditText desc;
    private Button add;
    private ListView list;
    private MyArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        code = (EditText)findViewById(R.id.editText1);
        desc = (EditText)findViewById(R.id.editText2);
        add = (Button)findViewById(R.id.button1);
        mBackButton = (Button)findViewById(R.id.backButton);
        mLoadAllButton = (Button)findViewById(R.id.buttonLoadAll);

        final SharedPreferences appHistory = this.getSharedPreferences("RSSFeeds", 0);

        //Return to the main activity
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        //Set the flag that will tell the main activity whether all feeds must be loaded
        mLoadAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appHistory.edit().putBoolean("loadAll", true).apply();

            }
        });

        // model
        modules = new ArrayList<>();

        URL_Address = new ArrayList<>();
        URL_Name = new ArrayList<>();
        // view
        adapter = new MyArrayAdapter(this, modules);
        list = (ListView)findViewById(R.id.listView1);

        addressSet = new LinkedHashSet<>();
        nameSet = new LinkedHashSet<>();

        //Check to see if the user already has any preferences
        if (appHistory.contains("URL_address")){

            addressSet.clear();
            nameSet.clear();

            //Populate sets with data from the shared preferences
            addressSet.addAll(appHistory.getStringSet("URL_address", addressSet));
            nameSet.addAll(appHistory.getStringSet("URL_name", nameSet));

            //Place the set data into the arrays
            Iterator iterator1 = addressSet.iterator();
            while (iterator1.hasNext()){
                URL_Address.add((String)iterator1.next());
            }

            Iterator iterator2 = nameSet.iterator();
            while (iterator2.hasNext()){
                URL_Name.add((String)iterator2.next());
            }

            //Create new module objects from the data held in the arrays
            for(int x = 0; x < URL_Address.size(); x++){
                modules.add(new RSSFeed(URL_Address.get(x), URL_Name.get(x)));
            }

        }

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Click on an item in the preferences to remove it
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Activity2.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + modules.get(position).getCode());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modules.remove(positionToRemove);
                        adapter.notifyDataSetChanged();

                        //Repopulate the arrays minus the module that was deleted
                        addressSet.clear();
                        nameSet.clear();
                        for (int x = 0; x < modules.size(); x++) {
                            addressSet.add(modules.get(x).getCode());
                            nameSet.add(modules.get(x).getDescription());
                        }
                        //Place sets into shared preferences
                        appHistory.edit().putStringSet("URL_address", addressSet).apply();
                        appHistory.edit().putStringSet("URL_name", nameSet).apply();
                    }
                });
                adb.show();
            }
        });

        list.setLongClickable(true);

        //Long click on an item in the preferences to set it as the preferred feed
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {

                AlertDialog.Builder adb2 = new AlertDialog.Builder(Activity2.this);
                adb2.setTitle("Preferred feed?");
                final int preferredPosition = position;
                adb2.setNegativeButton("Cancel", null);
                adb2.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = modules.get(preferredPosition).getCode();
                        appHistory.edit().putString("preferred_URL", url).apply();
                        appHistory.edit().putBoolean("loadAll", false).apply();
                    }
                });
                adb2.show();
                return true;
            }
        });

        //Take text from the edit texts and make add the Name/URL pair to the preferences
        add.setOnClickListener(new View.OnClickListener() {

                                   @Override
                                   public void onClick(View v) {

                                       if (modules.size() < 10) {
                                           String mcode = code.getText().toString();
                                           String mdesc = desc.getText().toString();
                                           RSSFeed module = new RSSFeed(mcode, mdesc);
                                           modules.add(module);
                                           adapter.notifyDataSetChanged();
                                           //Place data in sets
                                           addressSet.add(mcode);
                                           nameSet.add(mdesc);
                                           //Place sets into shared preferences
                                           appHistory.edit().putStringSet("URL_address", addressSet).apply();
                                           appHistory.edit().putStringSet("URL_name",nameSet).apply();
                                       } else {
                                           Toast.makeText(getApplicationContext(), "Too many feeds", Toast.LENGTH_LONG).show();
                                       }
                                   }
        });

    }

}
package cm3019.lab14.ex02.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cm3019.lab14.ex02.MainActivity;
import cm3019.lab14.ex02.rssreader.RssItem;

/**
 * Created by Alasdairp on 26/04/2016.
 */
public class TaskDataSource {

    //Columns that will be used in the table
    private String[] columns = {
            DbHelper.COLUMN_ID,
            DbHelper.COLUMN_TITLE,
            DbHelper.COLUMN_DESC,
            DbHelper.COLUMN_DATE,
            DbHelper.COLUMN_URL
    };

    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mDbHelper;

    public TaskDataSource(Context context) { mDbHelper = new DbHelper(context);}

    public void open() {
        mDatabase = mDbHelper.getWritableDatabase();
    }


    //Take in an RssItem and dissect it into individual variables which can be put into the database
    public RssItem createEntry(RssItem newEntry){
        
        ContentValues newValues = new ContentValues();

        newValues.put(DbHelper.COLUMN_TITLE, newEntry.getTitle());
        newValues.put(DbHelper.COLUMN_DESC, newEntry.getDesc());
        newValues.put(DbHelper.COLUMN_DATE, newEntry.getDate());
        newValues.put(DbHelper.COLUMN_URL, newEntry.getLink());

        long id = mDatabase.insert(DbHelper.TABLE_NAME, null, newValues);

        RssItem item = new RssItem();

        item.setId(id);
        item.setTitle(newEntry.getTitle());
        item.setDesc(newEntry.getDesc());
        item.setDate(newEntry.getDate());
        item.setLink(newEntry.getLink());

        return item;

    };

    public void close(){mDbHelper.close();}

    public void deleteAll(){mDatabase.delete(DbHelper.TABLE_NAME, null, null);}

    //Read the entirety of the database and return an ArrayList of RssItems constructed from the column values
    public ArrayList<RssItem> read() {
        Cursor cursor = mDatabase.query(DbHelper.TABLE_NAME, columns, null,
                null, null, null, null);
        ArrayList tasks = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tasks.add(generateObjectFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    //Construct an RssItem from the values in the database
    public RssItem generateObjectFromCursor(Cursor cursor) {

        if (cursor == null) {
            return null;
        }

        RssItem task = new RssItem();
        task.setId(cursor.getLong(0));
        task.setTitle(cursor.getString(1));
        task.setDesc(cursor.getString(2));
        task.setDate(cursor.getString(3));
        task.setLink(cursor.getString(4));

        return task;
    }

}
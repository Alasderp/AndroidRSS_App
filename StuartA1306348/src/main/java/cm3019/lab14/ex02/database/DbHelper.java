package cm3019.lab14.ex02.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alasdairp on 26/04/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "RSSFeed";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_URL = "URL";

    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_DATABASE = "create table " + TABLE_NAME + "( " + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TITLE + " text not null, "
    + COLUMN_DESC + " text not null, " + COLUMN_DATE + " text not null, " + COLUMN_URL + " text not null);";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

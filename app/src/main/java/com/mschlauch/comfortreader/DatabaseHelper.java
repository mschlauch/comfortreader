package com.mschlauch.comfortreader;

/**
 * Created by michael on 27.11.19.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "COUNTRIES";

    // Table columns
    public static final String _ID = "_id";
    public static final String BOOK_PATH = "bookpath";
    public static final String BOOK_TEXT = "booktext";
    public static final String BOOK_LENGTH = "booklength";//int
    public static final String BOOK_POSITION = "bookposition";//int
    public static final String BOOK_LASTREADING = "readingtimestamp";
    public static final String BOOK_WPM = "bookwpm";//int speed configuration

    // Database Information
    static final String DB_NAME = "JOURNALDEV_COUNTRIES.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BOOK_PATH + " TEXT NOT NULL, "
            + BOOK_TEXT + " TEXT," + BOOK_LENGTH + " INTEGER," + BOOK_POSITION
            + " INTEGER," + BOOK_WPM + " INTEGER," + BOOK_LASTREADING + " INTEGER" + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

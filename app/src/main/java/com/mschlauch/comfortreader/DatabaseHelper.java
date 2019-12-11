package com.mschlauch.comfortreader;

/**
 * Created by michael on 27.11.19.
 */


import android.database.sqlite.SQLiteOpenHelper;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "COUNTRIES";

    // Table columns
    public static final String _ID = "_id";
    public static final String BOOKPATH = "bookpath";
    public static final String BOOKTEXT = "booktext";
    public static final String BOOKLENGTH = "booklength";//int
    public static final String BOOKPOSITION = "bookposition";//int
    public static final String BOOKWPM = "bookwpm";//int speed configuration
    public static final String BOOKLASTREADING = "readingtimestamp";



    // Database Information
    static final String DB_NAME = "JOURNALDEV_COUNTRIES.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BOOKPATH + " TEXT NOT NULL, "
            + BOOKTEXT + " TEXT," + BOOKLENGTH + " INTEGER," + BOOKPOSITION
            + " INTEGER," + BOOKWPM + " INTEGER," + BOOKLASTREADING + " INTEGER" +  ");";

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
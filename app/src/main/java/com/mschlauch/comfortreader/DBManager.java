package com.mschlauch.comfortreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;

public class DBManager {

    protected DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String bookpath, String booktext, int booklength, int bookposition, long booklastread, int bookwpm) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.BOOK_PATH, bookpath);
        contentValue.put(DatabaseHelper.BOOK_TEXT, booktext);
        contentValue.put(DatabaseHelper.BOOK_LENGTH, booklength);
        contentValue.put(DatabaseHelper.BOOK_POSITION, bookposition);
        contentValue.put(DatabaseHelper.BOOK_LASTREADING, booklastread);
        contentValue.put(DatabaseHelper.BOOK_WPM, bookwpm);
        return database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOK_PATH, DatabaseHelper.BOOK_TEXT, DatabaseHelper.BOOK_LENGTH, DatabaseHelper.BOOK_POSITION, DatabaseHelper.BOOK_WPM};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchWithBookpath(String bookpathselected) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOK_PATH, DatabaseHelper.BOOK_TEXT, DatabaseHelper.BOOK_LENGTH, DatabaseHelper.BOOK_POSITION, DatabaseHelper.BOOK_WPM};
        String whereClause = DatabaseHelper.BOOK_PATH + "=?";
        String[] whereArgs = new String[]{
                "" + bookpathselected,
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchWithBookID(int bookid) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOK_PATH, DatabaseHelper.BOOK_TEXT, DatabaseHelper.BOOK_LENGTH, DatabaseHelper.BOOK_POSITION, DatabaseHelper.BOOK_WPM};
        String whereClause = DatabaseHelper._ID + "=?";
        String[] whereArgs = new String[]{
                "" + bookid,
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

/*
    public Cursor fetchwithPath(String path) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOK_PATH, DatabaseHelper.BOOK_TEXT, DatabaseHelper.BOOK_LENGTH, DatabaseHelper.BOOK_POSITION, DatabaseHelper.BOOK_WPM};
        String whereClause = DatabaseHelper.BOOK_PATH + "=?";
        String[] whereArgs = new String[]{
                "" + path,
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
*/

    public Cursor fetchChronological() {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOK_PATH, DatabaseHelper.BOOK_TEXT, DatabaseHelper.BOOK_LENGTH, DatabaseHelper.BOOK_POSITION, DatabaseHelper.BOOK_WPM};

        String orderclause = DatabaseHelper.BOOK_LASTREADING + " DESC LIMIT 10";
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, orderclause);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean deleteSingleRow(int rowId) {
        return database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + rowId, null) > 0;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOK_PATH, name);
        contentValues.put(DatabaseHelper.BOOK_TEXT, desc);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public int updateGlobalPosition(long _id, int position) {
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        long timevalue = tsTemp.getTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOK_POSITION, position);
        contentValues.put(DatabaseHelper.BOOK_LASTREADING, timevalue);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public int updateWPM(long _id, int wpm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOK_WPM, wpm);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public int updateText(long _id, String text) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOK_TEXT, text);
        contentValues.put(DatabaseHelper.BOOK_LENGTH, text.length());

        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        long timevalue = tsTemp.getTime();

        contentValues.put(DatabaseHelper.BOOK_LASTREADING, timevalue);
        contentValues.put(DatabaseHelper.BOOK_POSITION, 0);

        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}

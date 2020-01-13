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
        contentValue.put(DatabaseHelper.BOOKPATH, bookpath);
        contentValue.put(DatabaseHelper.BOOKTEXT, booktext);
        contentValue.put(DatabaseHelper.BOOKLENGTH, booklength);
        contentValue.put(DatabaseHelper.BOOKPOSITION, bookposition);
        contentValue.put(DatabaseHelper.BOOKLASTREADING, booklastread);
        contentValue.put(DatabaseHelper.BOOKWPM, bookwpm);
        return database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOKPATH, DatabaseHelper.BOOKTEXT, DatabaseHelper.BOOKLENGTH, DatabaseHelper.BOOKPOSITION, DatabaseHelper.BOOKWPM};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchwithBookpath(String bookpathselected) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOKPATH, DatabaseHelper.BOOKTEXT, DatabaseHelper.BOOKLENGTH, DatabaseHelper.BOOKPOSITION, DatabaseHelper.BOOKWPM};
        String whereClause = DatabaseHelper.BOOKPATH + "=?";
        String[] whereArgs = new String[]{
                "" + bookpathselected,
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchwithBookID(int bookid) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOKPATH, DatabaseHelper.BOOKTEXT, DatabaseHelper.BOOKLENGTH, DatabaseHelper.BOOKPOSITION, DatabaseHelper.BOOKWPM};
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

/*    public Cursor fetchwithPath (String path) {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.BOOKPATH, DatabaseHelper.BOOKTEXT, DatabaseHelper.BOOKLENGTH, DatabaseHelper.BOOKPOSITION,  DatabaseHelper.BOOKWPM};
        String whereClause = DatabaseHelper.BOOKPATH +"=?";
        String[] whereArgs = new String[] {
                "" + path,
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }*/


    public Cursor fetchchronological() {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.BOOKPATH, DatabaseHelper.BOOKTEXT, DatabaseHelper.BOOKLENGTH, DatabaseHelper.BOOKPOSITION, DatabaseHelper.BOOKWPM};


        String orderclause = DatabaseHelper.BOOKLASTREADING + " DESC LIMIT 10";
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
        contentValues.put(DatabaseHelper.BOOKPATH, name);
        contentValues.put(DatabaseHelper.BOOKTEXT, desc);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public int updateGlobalPosition(long _id, int position) {

        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        long timevalue = tsTemp.getTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOKPOSITION, position);
        contentValues.put(DatabaseHelper.BOOKLASTREADING, timevalue);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public int updateWPM(long _id, int wpm) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOKWPM, wpm);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public int updateText(long _id, String text) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOOKTEXT, text);
        contentValues.put(DatabaseHelper.BOOKLENGTH, text.length());

        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        long timevalue = tsTemp.getTime();

        contentValues.put(DatabaseHelper.BOOKLASTREADING, timevalue);
        contentValues.put(DatabaseHelper.BOOKPOSITION, 0);

        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }


    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}

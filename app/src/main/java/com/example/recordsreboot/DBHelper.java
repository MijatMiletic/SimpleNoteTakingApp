package com.example.recordsreboot;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recordsreboot.NoteContract.NoteEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "notes.db";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_SQL = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                NoteEntry.COLUMN_TIME_STAMP + " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_SQL);
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_CONTENT, "sdfasdf");
        values.put(NoteEntry.COLUMN_TIME_STAMP, System.currentTimeMillis());
        sqLiteDatabase.insert(NoteEntry.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

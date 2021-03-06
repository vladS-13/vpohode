package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vpohode.db"; //name of DB
    private static final int SCHEMA = 1;  // Version of DB
    static final String TABLE = "items"; // Name of Table
    // names of columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TERMID = "termindex";
    public static final String COLUMN_STYLE = "style";
    public static final String COLUMN_TOP = "top";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL ("CREATE TABLE items ("
                + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT, "
                + COLUMN_STYLE + " TEXT, "
                + COLUMN_TOP + " INTEGER, "
                + COLUMN_TERMID + " DOUBLE);");
       // by default
        // db.execSQL ("INSERT INTO " + TABLE + " (" + COLUMN_NAME + ", " + COLUMN_STYLE + ", " + COLUMN_TOP + ", "+ COLUMN_TERMID + ") VALUES ('Sviter', 'Бизнес', 1, 14);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}

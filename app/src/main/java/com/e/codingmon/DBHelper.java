package com.e.codingmon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int dbVersion = 1;
    public static final String dbName = "seouldb";

    public DBHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String testSQL = "CREATE TABLE testtb " + "(num," + "text)";
        db.execSQL(testSQL);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*if(newVersion == dbVersion) {
            db.execSQL("DROP TABLE testtb");
            onCreate(db);
        }*/
    }
}

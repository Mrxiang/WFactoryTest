package com.waterworld.factorytest.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.waterworld.factorytest.Utils;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="factory";
    private static final int    DATABASE_VERSION = 1;

//    public static final String CREATE_USER = "create table user (" +
//                    "userid integer primary key autoincrement, " + "username text, " + "password text)";

    public static final String CREATE_USER = "create table "+ Utils.TABLE+" (" +
            Utils.NAME +" text primary key , " +
            Utils.STATUS+" integer)";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( CREATE_USER );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

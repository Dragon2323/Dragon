package com.example.dragon.mynotepad.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dragon.mynotepad.Activity.addActivity;

/**
 * Created by Dragon on 2018/5/7.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private Context mContent;
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContent = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Content(id integer primary key autoincrement,content varchar(255),time varchar(255))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.cqupt.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";
    private static final int VERTION = 1;
    private static final String SQL_CREATE = "CREATE TABLE thread_info(_id integer primary key autoincrement,thread_id integer,url text,start integer,end integer,finished,integer)";
    private static final String SQL_DROP = "DROP TABLE IF EXISTS thread_info";
    private static DbHelper mHelper = null;

    private DbHelper(Context context) {
        super(context, DB_NAME, null, VERTION);
    }

    /**
     * 获得这个类的对象
     *
     * @param db
     */

    public static DbHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new DbHelper(context);
        }
        return mHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}

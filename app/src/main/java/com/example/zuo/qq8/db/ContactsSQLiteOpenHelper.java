package com.example.zuo.qq8.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by taojin on 2016/6/8.10:50
 */
public class ContactsSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "contact.db";
    private static final int VERSION = 1;
    public static final String TABLE_CONTACT = "t_contacts";

    public static final String CONTACT_COLUMN_USERNAME = "username";
    public static final String CONTACT_COLUMN_CONTACT = "contact";

    private ContactsSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    public ContactsSQLiteOpenHelper(Context context){
        
        this(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+TABLE_CONTACT+"(_id integer primary key ,"+CONTACT_COLUMN_USERNAME+" varchar(20),"+CONTACT_COLUMN_CONTACT+" varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

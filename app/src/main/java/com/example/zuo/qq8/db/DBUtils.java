package com.example.zuo.qq8.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taojin on 2016/6/8.10:56
 */
public class DBUtils {
    public static void updateContacts(Context context, String username, List<String> contacts) {
        ContactsSQLiteOpenHelper openHelper = new ContactsSQLiteOpenHelper(context);

        SQLiteDatabase database = openHelper.getWritableDatabase();

        database.beginTransaction();

        //先删除username的所有联系人
        database.delete(ContactsSQLiteOpenHelper.TABLE_CONTACT, ContactsSQLiteOpenHelper.CONTACT_COLUMN_USERNAME + "=?", new String[]{username});
        //然后再添加

        for (int i = 0; i < contacts.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(ContactsSQLiteOpenHelper.CONTACT_COLUMN_CONTACT, contacts.get(i));
            values.put(ContactsSQLiteOpenHelper.CONTACT_COLUMN_USERNAME, username);
            database.insert(ContactsSQLiteOpenHelper.TABLE_CONTACT, null, values);
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();
        openHelper.close();
        ;
    }

    public static List<String> getContactsFromDB(Context context, String username) {
        ContactsSQLiteOpenHelper contactsSQLiteOpenHelper = new ContactsSQLiteOpenHelper(context);
        SQLiteDatabase readableDatabase = contactsSQLiteOpenHelper.getReadableDatabase();
        List<String> contacts = new ArrayList<>();
        Cursor cursor = readableDatabase.query(ContactsSQLiteOpenHelper.TABLE_CONTACT, new String[]{ContactsSQLiteOpenHelper.CONTACT_COLUMN_CONTACT}, "username=?", new String[]{username}, null, null, "username");
        while (cursor.moveToNext()) {
            contacts.add(cursor.getString(0));
        }

        cursor.close();
        readableDatabase.close();
        contactsSQLiteOpenHelper.close();


        return contacts;

    }
}

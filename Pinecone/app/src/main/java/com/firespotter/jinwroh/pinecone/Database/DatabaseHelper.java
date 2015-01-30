package com.firespotter.jinwroh.pinecone.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.firespotter.jinwroh.pinecone.Database.DatabaseContract;


/**
 * Created by jinroh on 1/28/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DatabaseContract.photo.CREATE_TABLE);
        database.execSQL(DatabaseContract.contact.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO:
    }
}

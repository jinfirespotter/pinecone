package com.firespotter.jinwroh.pinecone.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.firespotter.jinwroh.pinecone.Database.DatabaseContract;
import com.firespotter.jinwroh.pinecone.Database.DatabaseHelper;
import com.firespotter.jinwroh.pinecone.Database.Photo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinroh on 1/28/15.
 */
public class PhotoDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;

    public PhotoDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    public long createPhoto(String filepath) {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.photo.KEY_FILEPATH, filepath);

        long insertId = database.insert(DatabaseContract.photo.TABLE_NAME, null, values);

        return insertId;
    }

    public Photo retrievePhoto(long id) {
        Cursor cursor = database.query(DatabaseContract.photo.TABLE_NAME,
                DatabaseContract.photo.KEY_ARRAY,
                DatabaseContract.photo._ID + " = " + id,
                null, null, null, null);

        cursor.moveToFirst();
        Photo photo = cursorToPhoto(cursor);
        cursor.close();

        return photo;
    }

    public List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<Photo>();

        Cursor cursor = database.query(DatabaseContract.photo.TABLE_NAME,
                DatabaseContract.photo.KEY_ARRAY, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Photo photo = cursorToPhoto(cursor);
            photos.add(photo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return photos;
    }


    private Photo cursorToPhoto(Cursor cursor) {
        long id = cursor.getLong(0);
        String filepath = cursor.getString(1);

        return new Photo(id, filepath);
    }
}

package com.firespotter.jinwroh.pinecone.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.firespotter.jinwroh.pinecone.Database.DatabaseContract;
import com.firespotter.jinwroh.pinecone.Database.DatabaseHelper;
import com.firespotter.jinwroh.pinecone.Database.Photo;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinroh on 1/28/15.
 */
// TODO: Guard against SQL injections
public class PhotoDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;

    private Context context;


    public PhotoDataSource(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }


    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }


    public void close() {
        databaseHelper.close();
    }


    public long createPhoto(Photo photo) {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.photo.KEY_FILEPATH, photo.getFilepath());

        long insertId = database.insert(DatabaseContract.photo.TABLE_NAME, null, values);

        return insertId;
    }


    public long updatePhoto(Photo photo) {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.photo.KEY_FILEPATH, photo.getFilepath());
        database.update(DatabaseContract.photo.TABLE_NAME,
                values,
                DatabaseContract.photo._ID + " = " + photo.getId(),
                null);

        return photo.getId();
    }


    public long updateOrInsert(Photo photo) {
        if (retrievePhoto(photo.getId()) == null) {
            return createPhoto(photo);
        }
        else {
            return updatePhoto(photo);
        }
    }


    public boolean deletePhoto(Photo photo) {

        File photoFile = new File(photo.getFilepath());
        photoFile.delete();

        return database.delete(DatabaseContract.photo.TABLE_NAME,
                DatabaseContract.photo._ID + " = " + photo.getId(), null) > 0;
    }


    public Photo retrievePhoto(long id) {
        Cursor cursor = database.query(DatabaseContract.photo.TABLE_NAME,
                DatabaseContract.photo.KEY_ARRAY,
                DatabaseContract.photo._ID + " = " + id,
                null, null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Photo photo = cursorToPhoto(cursor);
        cursor.close();

        return photo;
    }


    public void deleteAll() {
        database.execSQL("delete from " + DatabaseContract.contact.TABLE_NAME);

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        for(File file: directory.listFiles()) {
            file.delete();
        }
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

        cursor.close();
        return photos;
    }


    private Photo cursorToPhoto(Cursor cursor) {
        long id = cursor.getLong(0);
        String filepath = cursor.getString(1);

        return new Photo(id, filepath);
    }
}

package com.firespotter.jinwroh.pinecone.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinroh on 1/30/15.
 */

// TODO: Guard against SQL injections
public class ContactDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;


    public ContactDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }


    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }


    public void close() {
        databaseHelper.close();
    }


    public long createContact(Contact contact) {

        ContentValues values = new ContentValues();

        values.put(DatabaseContract.contact.KEY_PHOTO_ID, contact.getPhotoId());
        values.put(DatabaseContract.contact.KEY_NAME, contact.getName());
        values.put(DatabaseContract.contact.KEY_EMAIL, contact.getEmail());
        values.put(DatabaseContract.contact.KEY_PHONE_NUMBER, contact.getPhoneNumber());
        values.put(DatabaseContract.contact.KEY_COMPANY, contact.getCompany());
        values.put(DatabaseContract.contact.KEY_POSITION, contact.getPosition());
        values.put(DatabaseContract.contact.KEY_NOTES, contact.getNotes());

        long insertId = database.insert(DatabaseContract.contact.TABLE_NAME, null, values);

        return insertId;
    }


    public long updateOrInsert(Contact contact) {

        if (retrieveContact(contact.getId()) == null) {
            return createContact(contact);
        } else {
            return updateContact(contact);
        }
    }


    public long updateContact(Contact contact) {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.contact.KEY_PHOTO_ID, contact.getPhotoId());
        values.put(DatabaseContract.contact.KEY_NAME, contact.getName());
        values.put(DatabaseContract.contact.KEY_EMAIL, contact.getEmail());
        values.put(DatabaseContract.contact.KEY_PHONE_NUMBER, contact.getPhoneNumber());
        values.put(DatabaseContract.contact.KEY_COMPANY, contact.getCompany());
        values.put(DatabaseContract.contact.KEY_POSITION, contact.getPosition());
        values.put(DatabaseContract.contact.KEY_NOTES, contact.getNotes());

        database.update(DatabaseContract.contact.TABLE_NAME,
                values,
                DatabaseContract.contact._ID + " = " + contact.getId(),
                null);

        return contact.getId();
    }


    public boolean deleteContact(Contact contact) {
        return database.delete(DatabaseContract.contact.TABLE_NAME,
                DatabaseContract.contact._ID + " = " + contact.getId(), null) > 0;
    }


    public void deleteAll() {
        database.execSQL("delete from " + DatabaseContract.contact.TABLE_NAME);
    }


    public Contact retrieveContact(long id) {
        Cursor cursor = database.query(DatabaseContract.contact.TABLE_NAME,
                DatabaseContract.contact.KEY_ARRAY,
                DatabaseContract.contact._ID + " = " + id,
                null, null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Contact contact = cursorToContact(cursor);
        cursor.close();

        return contact;
    }


    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        Cursor cursor = database.query(DatabaseContract.contact.TABLE_NAME,
                DatabaseContract.contact.KEY_ARRAY, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact contact = cursorToContact(cursor);
            contacts.add(contact);
            cursor.moveToNext();
        }

        cursor.close();
        return contacts;
    }


    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact();

        contact.setId( cursor.getLong(0) );
        contact.setPhotoId(cursor.getLong(1)  );
        contact.setName( cursor.getString(2) );
        contact.setEmail( cursor.getString(3) );
        contact.setPhoneNumber(cursor.getString(4) );
        contact.setCompany( cursor.getString(5) );
        contact.setPosition( cursor.getString(6) );
        contact.setNotes( cursor.getString(7) );

        return contact;
    }
}

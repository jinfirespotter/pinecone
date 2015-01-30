package com.firespotter.jinwroh.pinecone;

import android.provider.BaseColumns;

/**
 * Created by jinroh on 1/28/15.
 */
public final class DatabaseContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";
    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String COMMA_SEPARATOR = ",";

    // Private constructor to prevent instantiation of contract class
    private DatabaseContract() {
        // EMPTY!
    }


    public static abstract class photo implements BaseColumns {
        public static final String TABLE_NAME = "photo";
        public static final String KEY_FILEPATH = "filepath";

        private photo() {}

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEPARATOR
                + KEY_FILEPATH + TEXT_TYPE
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] KEY_ARRAY = {
                _ID,
                KEY_FILEPATH
        };
    }


    public static abstract class contact implements BaseColumns {
        public static final String TABLE_NAME = "contact";
        public static final String KEY_PHOTO_ID = "photo_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PHONE_NUMBER = "phone_number";
        public static final String KEY_COMPANY = "company";
        public static final String KEY_POSITION = "position";

        private contact() {}

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT " + COMMA_SEPARATOR
                + KEY_PHOTO_ID + INTEGER_TYPE + COMMA_SEPARATOR
                + KEY_NAME + TEXT_TYPE + COMMA_SEPARATOR
                + KEY_EMAIL + TEXT_TYPE + COMMA_SEPARATOR
                + KEY_PHONE_NUMBER + TEXT_TYPE + COMMA_SEPARATOR
                + KEY_COMPANY + TEXT_TYPE + COMMA_SEPARATOR
                + KEY_POSITION + TEXT_TYPE
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] KEY_ARRAY = {
                _ID,
                KEY_PHOTO_ID,
                KEY_NAME,
                KEY_EMAIL,
                KEY_PHONE_NUMBER,
                KEY_COMPANY,
                KEY_POSITION
        };
    }
}

package com.firespotter.jinwroh.pinecone.Database;

import android.provider.BaseColumns;

/**
 * Created by jinroh on 1/28/15.
 */
public final class DatabaseContract {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "pinecone_database.db";

    // Private constructor to prevent instantiation of contract class
    private DatabaseContract() {
        // EMPTY!
    }
}

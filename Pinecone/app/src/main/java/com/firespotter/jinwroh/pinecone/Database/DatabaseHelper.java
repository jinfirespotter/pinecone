package com.firespotter.jinwroh.pinecone.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by jinroh on 2/4/15.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Context context;
    private ArrayList<Class<?>> tables;

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);

        this.context = context;

        tables = new ArrayList<Class<?>>();
        tables.add(Photo.class);
        tables.add(Contact.class);
    }


    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            for (Class<?> clazz : tables) {
                TableUtils.createTableIfNotExists(connectionSource, clazz);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        // TODO: ?
        try {
            for (Class<?> clazz : tables) {
                TableUtils.createTableIfNotExists(connectionSource, clazz);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() {
        super.close();
    }
}

package com.firespotter.jinwroh.pinecone.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by jinroh on 1/28/15.
 */

@DatabaseTable
public class Photo implements Serializable{

    @DatabaseField(index = true, unique = true, generatedId = true)
    private long id;

    @DatabaseField
    private String filepath;

    public Photo() { }

    public Photo(String filepath) {
       this.filepath = filepath;
    }

    public Photo(long id, String filepath) {
        this.id = id;
        this.filepath = filepath;
    }

    public long getId() {
        return this.id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}

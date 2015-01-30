package com.firespotter.jinwroh.pinecone.Database;

/**
 * Created by jinroh on 1/28/15.
 */
public class Photo {

    private long id;
    private String filepath;

    public Photo(long id, String filepath) {
        this.id = id;
        this.filepath = filepath;
    }

    public long getId() {
        return this.id;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}

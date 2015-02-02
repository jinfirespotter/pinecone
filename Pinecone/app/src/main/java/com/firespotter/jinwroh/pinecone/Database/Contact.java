package com.firespotter.jinwroh.pinecone.Database;

import java.io.Serializable;

/**
 * Created by jinroh on 1/28/15.
 */
public class Contact implements Serializable{

    private long id;
    private long photoId;
    private String name;
    private String email;
    private String phoneNumber;
    private String company;
    private String position;
    private String notes;

    public Contact() {};

    public Contact(long id, long photoId) {
        this.id = id;
        this.photoId = photoId;
    }

    public long getId() { return this.id; }

    public void setId(long id) { this.id = id; }

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }
}

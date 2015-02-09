package com.firespotter.jinwroh.pinecone.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by jinroh on 1/28/15.
 */

@DatabaseTable
public class Contact implements Serializable{

    @DatabaseField(index = true, unique = true, generatedId = true)
    private long id;

    @DatabaseField (foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Photo photo;

    @DatabaseField
    private String name;

    @DatabaseField
    private String email;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private String company;

    @DatabaseField
    private String position;

    @DatabaseField
    private String notes;


    public Contact() {
        //Needed by ORMLite
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // This string is used during filtering.
    @Override
    public String toString() {
        return name + " " + email + " " + phoneNumber + " " + company + " " + position;
    }
}

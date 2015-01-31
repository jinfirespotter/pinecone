package com.firespotter.jinwroh.pinecone;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.Photo;

/**
 * Created by jinroh on 1/30/15.
 */
public class HomeListItem {

    private Photo photo;
    private Contact contact;

    public HomeListItem(Photo photo, Contact contact) {
        this.photo = photo;
        this.contact = contact;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Contact getContact() {
        return contact;
    }
}

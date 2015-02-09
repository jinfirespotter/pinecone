package com.firespotter.jinwroh.pinecone.Adapter;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.Photo;

/**
 * Created by jinroh on 1/30/15.
 */
public class HomeListItem {

    private Contact contact;

    public HomeListItem(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}

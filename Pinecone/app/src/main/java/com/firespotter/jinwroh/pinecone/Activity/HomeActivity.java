package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.ContactDataSource;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.Database.PhotoDataSource;
import com.firespotter.jinwroh.pinecone.Adapter.HomeListAdapter;
import com.firespotter.jinwroh.pinecone.Adapter.HomeListItem;
import com.firespotter.jinwroh.pinecone.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        super.initialiseDrawer();

        this.initializeListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public void initializeListView() {

        ListView homeList = (ListView) findViewById(R.id.frame_container);

        PhotoDataSource photoDataSource = new PhotoDataSource(this);
        ContactDataSource contactDataSource = new ContactDataSource(this);

        try {
            photoDataSource.open();
            contactDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Photo> photoList = photoDataSource.getAllPhotos();
        List<Contact> contactList = contactDataSource.getAllContacts();

        final List<HomeListItem> homeListItemList = new ArrayList<HomeListItem>();

        // O(n^2)! Oh No! TODO: Optimize query
        for (Photo photo : photoList) {
            long photoId = photo.getId();
            for (int i = 0; i < contactList.size(); i++) {
                if (contactList.get(i).getPhotoId() == photoId) {
                    HomeListItem homeListItem = new HomeListItem(photo, contactList.get(i));
                    homeListItemList.add(homeListItem);
                }
            }
        }

        HomeListAdapter homeAdapter = new HomeListAdapter(getApplicationContext(), homeListItemList);
        homeList.setAdapter(homeAdapter);

        homeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);

                Photo photo = homeListItemList.get(position).getPhoto();
                Contact contact = homeListItemList.get(position).getContact();

                intent.putExtra(PhotoActivity.PHOTO_ACTIVITY_PHOTO, photo);
                intent.putExtra(PhotoActivity.PHOTO_ACTIVITY_CONTACT, contact);

                startActivity(intent);
            }
        });

        photoDataSource.close();
        contactDataSource.close();
    }
}

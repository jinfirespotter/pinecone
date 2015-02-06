package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firespotter.jinwroh.pinecone.Adapter.HomeListAdapter;
import com.firespotter.jinwroh.pinecone.Adapter.HomeListItem;
import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.DatabaseHelper;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BaseDrawerActivity {

    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // For testing purposes
        initializeSamples();

        super.initialiseDrawer();

        initializeListView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
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

        List<Photo> photoList = null;
        List<Contact> contactList = null;

        try {
            Dao<Photo, Integer> photoDao = getHelper().getDao(Photo.class);
            Dao<Contact, Integer> contactDao = getHelper().getDao(Contact.class);

            photoList = photoDao.queryForAll();
            contactList = contactDao.queryForAll();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        final List<HomeListItem> homeListItemList = new ArrayList<HomeListItem>();

        // O(n^2)! Oh No! TODO: Optimize query
        for (Photo photo : photoList) {
            long photoId = photo.getId();
            for (int i = 0; i < contactList.size(); i++) {
                if (contactList.get(i).getPhoto().getId() == photoId) {
                    HomeListItem homeListItem = new HomeListItem(photo, contactList.get(i));
                    homeListItemList.add(homeListItem);
                }
            }
        }

        HomeListAdapter homeAdapter = new HomeListAdapter(context, homeListItemList);
        homeList.setAdapter(homeAdapter);

        homeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, EditActivity.class);

                Photo photo = homeListItemList.get(position).getPhoto();
                Contact contact = homeListItemList.get(position).getContact();

                intent.putExtra(BaseActivity.PHOTO_ACTIVITY_PHOTO, photo);
                intent.putExtra(BaseActivity.PHOTO_ACTIVITY_CONTACT, contact);

                startActivity(intent);
            }
        });
    }


    public void initializeSamples() {

        String[] sampleFiles = {"card1.jpg", "card2.png", "card3.jpg"};

        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

           for (String s : sampleFiles) {

               File destination = new File(directory, s);

               if (!destination.exists()) {

                   AssetManager assetManager = getAssets();
                   InputStream in = assetManager.open("sample/" + s);
                   OutputStream out = new FileOutputStream(destination);

                   byte[] buf = new byte[1024];
                   int len;

                   while ((len = in.read(buf)) > 0) {
                       out.write(buf, 0, len);
                   }

                   in.close();
                   out.close();

                   Photo photo = new Photo(destination.getAbsolutePath());

                   Dao<Photo, Integer> photoDao = getHelper().getDao(Photo.class);
                   Dao<Contact, Integer> contactDao = getHelper().getDao(Contact.class);

                   photoDao.createOrUpdate(photo);

                   Contact contact = new Contact();
                   contact.setPhoto(photo);
                   contact.setName("Unnamed");

                   contactDao.createOrUpdate(contact);
               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }
}

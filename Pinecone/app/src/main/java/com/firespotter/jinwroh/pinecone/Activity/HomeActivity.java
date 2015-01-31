package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.ContactDataSource;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.Database.PhotoDataSource;
import com.firespotter.jinwroh.pinecone.HomeListAdapter;
import com.firespotter.jinwroh.pinecone.HomeListItem;
import com.firespotter.jinwroh.pinecone.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends PhotoActivity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        super.initialiseDrawer();

        this.initializeListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        /*
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        */
        return super.onMenuOpened(featureId, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_picture) {
            super.dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            super.saveToInternalStorage(mCurrentPhotoPath);
            super.saveToGallery(mCurrentPhotoPath);

            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EXTRA_MESSAGE, mCurrentPhotoPath);
            startActivity(intent);
        }
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

        List<HomeListItem> homeListItemList = new ArrayList<HomeListItem>();

        // O(n^2)! Oh No!
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

        photoDataSource.close();
        contactDataSource.close();
    }
}

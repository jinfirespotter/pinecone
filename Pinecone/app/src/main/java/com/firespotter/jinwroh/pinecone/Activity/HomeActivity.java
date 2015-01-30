package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.firespotter.jinwroh.pinecone.NavDrawerItem;
import com.firespotter.jinwroh.pinecone.NavDrawerListAdapter;
import com.firespotter.jinwroh.pinecone.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class HomeActivity extends PhotoActivity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    static final int REQUEST_TAKE_PHOTO = 1;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.initializeDrawer(savedInstanceState);
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

        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.action_picture) {
            super.dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            ImageView mImageView = (ImageView) findViewById(R.id.thumbnail);
            mImageView.setImageBitmap(thumbnailUtils.extractThumbnail(myBitmap, 50, 50));

            super.saveToInternalStorage(mCurrentPhotoPath);
            super.saveToGallery(mCurrentPhotoPath);

            try {
                ImageView img = (ImageView) findViewById(R.id.thumbnail);
                img.setImageBitmap(super.retrievePhotoFromStorage(mCurrentPhotoPath));

                TessBaseAPI baseAPI = new TessBaseAPI();
                baseAPI.setDebug(true) ;
                baseAPI.init(Environment.getExternalStorageDirectory().toString() + "/pinecone/", "eng");
                baseAPI.setImage(super.retrievePhotoFromStorage(mCurrentPhotoPath));
                String recognizedText = baseAPI.getUTF8Text();
                System.out.println(recognizedText);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EXTRA_MESSAGE, mCurrentPhotoPath);
            startActivity(intent);
        }
    }



    public void initializeDrawer(Bundle savedInstanceState) {
        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu1);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        int counter = 0;
        for (String title : navMenuTitles) {
            navDrawerItems.add(new NavDrawerItem(title, navMenuIcons.getResourceId(counter, -1)));
            counter++;
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    }


    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onPostCreate (Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }


    private void displayView(int position) {

        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, AboutActivity.class);
                break;
            case 1:
                intent = new Intent(this, AboutActivity.class);
                break;
            case 2:
                intent = new Intent(this, AboutActivity.class);
                break;
            default:
                intent = new Intent(this, AboutActivity.class);
                break;
        }

        startActivity(intent);

    }
}

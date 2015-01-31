package com.firespotter.jinwroh.pinecone.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firespotter.jinwroh.pinecone.NavDrawerItem;
import com.firespotter.jinwroh.pinecone.NavDrawerListAdapter;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.Database.PhotoDataSource;
import com.firespotter.jinwroh.pinecone.R;

import java.sql.SQLException;
import java.util.ArrayList;


public class AboutActivity extends PhotoActivity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        super.initialiseDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
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

            ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            super.saveToInternalStorage(mCurrentPhotoPath);
            super.saveToGallery(mCurrentPhotoPath);

            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EXTRA_MESSAGE, mCurrentPhotoPath);
            startActivity(intent);
        }
    }

}

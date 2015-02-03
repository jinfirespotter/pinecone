package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jinroh on 1/30/15.
 */
public class PhotoActivity extends BaseActivity {

    static final int REQUEST_TAKE_PHOTO = 100;

    public final static String PHOTO_ACTIVITY_FIRST = "com.firespotter.pinecone.first";
    public final static String PHOTO_ACTIVITY_PHOTO = "com.firespotter.pinecone.photo";
    public final static String PHOTO_ACTIVITY_CONTACT = "com.firespotter.pinecone.contact";

    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";

    protected Photo photo;
    protected Contact contact;

    String photoFilePath;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_picture) {
            this.takeNewPicture();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void takeNewPicture() {
        this.photo = new Photo();
        this.contact = new Contact();

        this.dispatchTakePictureIntent();
    }


    public void retakePicture(Photo photo, Contact contact) {
        this.photo = photo;
        this.contact = contact;

        this.dispatchTakePictureIntent();
    }


    /*
        NOTE regarding onSaveInstanceState and OnRestoreInstanceState

        For Samsung phones only, when the orientation is changed while using the Camera interface,
        the activity that called the Camera is destroyed, and recreated again when the activity
        is returned (on onActivityResult()).

        This creates a slight problem since we have to update the this.photo object
        filepath accordingly, but the photo object is destroyed when the activity is destroyed.

        To circumvent this, we save the photo instance inside the Bundle instanceState on destroy,
        and recreate it on restoreInstanceState.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.photo != null) {
            outState.putSerializable(this.PHOTO_ACTIVITY_PHOTO, this.photo);
        }
        if (this.contact != null) {
            outState.putSerializable(this.PHOTO_ACTIVITY_CONTACT, this.contact);
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(this.PHOTO_ACTIVITY_PHOTO)) {
            this.photo = (Photo) savedInstanceState.getSerializable(this.PHOTO_ACTIVITY_PHOTO);
        }
        if (savedInstanceState.containsKey(this.PHOTO_ACTIVITY_CONTACT)) {
            this.contact = (Contact) savedInstanceState.getSerializable(this.PHOTO_ACTIVITY_CONTACT);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                this.photo.setFilepath(photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, this.REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            System.out.println(this.photo.getFilepath());

            String savedPath = this.saveToInternalStorage(this.photo.getFilepath());
            this.photo.setFilepath(savedPath);

            Intent intent = new Intent(this, EditActivity.class);

            intent.putExtra(this.PHOTO_ACTIVITY_FIRST, true);
            intent.putExtra(this.PHOTO_ACTIVITY_PHOTO, photo);
            intent.putExtra(this.PHOTO_ACTIVITY_CONTACT, contact);

            startActivity(intent);
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = this.JPEG_FILE_PREFIX + timeStamp + "_";
        File album = getAlbumDir();
        File image = File.createTempFile(imageFileName, this.JPEG_FILE_SUFFIX, album);
        return image;
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.album_name));

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        }
        else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    public Bitmap retrievePhotoFromStorage(String path) throws FileNotFoundException {
        File file = new File(path);
        return BitmapFactory.decodeStream(new FileInputStream(file));
    }


    public String saveToInternalStorage(String path) {

        File source = new File(path);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File destination = new File(directory, source.getName());

        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return destination.getAbsolutePath();
    }
}

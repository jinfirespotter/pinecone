package com.firespotter.jinwroh.pinecone.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.ContactDataSource;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.Database.PhotoDataSource;
import com.firespotter.jinwroh.pinecone.Module.ImageReader;
import com.firespotter.jinwroh.pinecone.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class EditActivity extends PhotoActivity {

    private Photo photo;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        photo = (Photo) intent.getSerializableExtra(PhotoActivity.PHOTO_ACTIVITY_PHOTO);
        contact = (Contact) intent.getSerializableExtra(PhotoActivity.PHOTO_ACTIVITY_CONTACT);

        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream(new FileInputStream(photo.getFilepath()));

            ImageReader imageReader = new ImageReader(this, image);
            imageReader.convertImageToText();

            ImageView img = (ImageView) findViewById(R.id.edit_photo);
            img.setImageBitmap(image);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        EditText editName = (EditText) findViewById(R.id.edit_name);
        EditText editCompany = (EditText) findViewById(R.id.edit_company);
        EditText editPosition = (EditText) findViewById(R.id.edit_position);
        EditText editEmail = (EditText) findViewById(R.id.edit_email);
        EditText editPhone = (EditText) findViewById(R.id.edit_phone);
        EditText editNotes = (EditText) findViewById(R.id.edit_notes);

        editName.setText(contact.getName());
        editCompany.setText(contact.getCompany());
        editPosition.setText(contact.getPosition());
        editEmail.setText(contact.getEmail());
        editPhone.setText(contact.getPhoneNumber());
        editNotes.setText(contact.getNotes());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                this.save();
                break;

            case R.id.action_save_to_gallery:
                this.savePictureToGallery(this.photo.getFilepath());
                break;

            case R.id.action_retake_picture:
                super.retakePicture(this.photo, this.contact);
                break;

            case R.id.action_send_email:
                break;

            case R.id.action_delete:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    public void scanImage(View view) {
        testTess();
    }


    public void save() {

        PhotoDataSource photoDataSource = new PhotoDataSource(this);
        ContactDataSource contactDataSource = new ContactDataSource(this);

        try {
            photoDataSource.open();
            contactDataSource.open();

            long photoId = photoDataSource.updateOrInsert(photo);

            EditText nameText = (EditText) findViewById(R.id.edit_name);
            EditText companyText = (EditText) findViewById(R.id.edit_company);
            EditText titleText = (EditText) findViewById(R.id.edit_position);
            EditText emailText = (EditText) findViewById(R.id.edit_email);
            EditText phoneText = (EditText) findViewById(R.id.edit_phone);
            EditText notesText = (EditText) findViewById(R.id.edit_notes);

            contact.setPhotoId(photoId);
            contact.setName(nameText.getText().toString());
            contact.setEmail(emailText.getText().toString());
            contact.setPhoneNumber(phoneText.getText().toString());
            contact.setCompany(companyText.getText().toString());
            contact.setPosition(titleText.getText().toString());
            contact.setNotes(notesText.getText().toString());

            contactDataSource.updateOrInsert(contact);

            photoDataSource.close();
            contactDataSource.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        photoDataSource.close();
        contactDataSource.close();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    public void savePictureToGallery(String path) {
        System.out.println(path);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void testTess() {
        try {

            File f=new File("/storage/sdcard/Download/20_offset-pantone-business-card-design-print-downtown-new-york-city-emboss-deboss-corporate-logo-designer-ny.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            TessBaseAPI baseAPI = new TessBaseAPI();

            String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/pinecone/";
            String TAG = "PINE";
            String lang = "eng";

            String[] paths = new String[] { DATA_PATH, DATA_PATH + "src/main/asset/tessdata/"};

            for (String path : paths) {
                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                        return;
                    } else {
                        Log.v(TAG, "Created directory " + path + " on sdcard");
                    }
                }
            }

            /*
            if ((new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
                try {

                    AssetManager assetManager = getAssets();
                    InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                    //GZIPInputStream gin = new GZIPInputStream(in);
                    OutputStream out = new FileOutputStream(DATA_PATH
                            + "tessdata/" + lang + ".traineddata");

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    //while ((lenf = gin.read(buff)) > 0) {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    //gin.close();
                    out.close();

                    Log.v(TAG, "Copied " + lang + " traineddata");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
                }
            }
            */


            baseAPI.setDebug(true) ;
            baseAPI.init(DATA_PATH, lang);
            baseAPI.setImage(b);
            String recognizedText = baseAPI.getUTF8Text();
            System.out.println(recognizedText);

            Context context = getApplicationContext();
            //CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, recognizedText, duration);
            toast.show();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

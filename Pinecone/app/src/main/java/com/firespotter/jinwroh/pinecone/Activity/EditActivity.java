package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.DatabaseHelper;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.Module.ImageReader;
import com.firespotter.jinwroh.pinecone.Module.TextExtractor;
import com.firespotter.jinwroh.pinecone.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class EditActivity extends BaseActivity {

    private DatabaseHelper mDatabaseHelper;

    private Photo photo;
    private Contact contact;

    private EditText editName;
    private EditText editCompany;
    private EditText editPosition;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        photo = (Photo) intent.getSerializableExtra(BaseActivity.PHOTO_ACTIVITY_PHOTO);
        contact = (Contact) intent.getSerializableExtra(BaseActivity.PHOTO_ACTIVITY_CONTACT);

        // Has the picture been taken? If true, it's a new card
        // If false, we're just editing an existing card.
        Boolean pictureTaken = intent.getBooleanExtra(BaseActivity.PHOTO_ACTIVITY_FIRST, false);

        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream(new FileInputStream(photo.getFilepath()));
            ImageView img = (ImageView) findViewById(R.id.edit_photo);
            img.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        editName = (EditText) findViewById(R.id.edit_name);
        editCompany = (EditText) findViewById(R.id.edit_company);
        editPosition = (EditText) findViewById(R.id.edit_position);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        editNotes = (EditText) findViewById(R.id.edit_notes);

        // Only scan the picture again when it's a new picture is taken.
        if (pictureTaken) {
            rescanPicture();
        } else {
            editName.setText(contact.getName());
            editCompany.setText(contact.getCompany());
            editPosition.setText(contact.getPosition());
            editEmail.setText(contact.getEmail());
            editPhone.setText(contact.getPhoneNumber());
            editNotes.setText(contact.getNotes());
        }
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
            case android.R.id.home:
            case R.id.action_save:
                save();
                break;
            case R.id.action_rescan_picture:
                rescanPicture();
                break;
            case R.id.action_add_to_contacts:
                addToContacts();
                break;
            case R.id.action_save_to_gallery:
                savePictureToGallery(photo.getFilepath());
                break;
            case R.id.action_retake_picture:
                super.retakePicture(photo, contact);
                break;
            case R.id.action_send_email:
                sendEmail();
                break;
            case R.id.action_delete:
                delete();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
    }


    public void rescanPicture() {
        new ImageScanOperation(this).execute(photo);
    }


    private class ImageScanOperation extends AsyncTask<Photo, String, String> {

        private Context context;

        public ImageScanOperation(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {
            CharSequence text = "Scanning in Progress!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        protected String doInBackground(Photo... photo) {
            Bitmap image = null;
            String text = "";
            try {
                image = BitmapFactory.decodeStream(new FileInputStream(photo[0].getFilepath()));
                ImageReader imageReader = new ImageReader(context, image);
                text = imageReader.convertImageToText();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return text;
        }

        protected void onPostExecute(String text) {
            TextExtractor textExtractor = new TextExtractor(text);

            String emailString = textExtractor.extractEmail();
            String phoneNumberString = textExtractor.extractPhoneNumber();

            editNotes.setText(text);
            editEmail.setText(emailString);
            editPhone.setText(phoneNumberString);

            CharSequence toastText = "Scanning Complete!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }
    }


    public void save() {
        try {
            Dao<Photo, Integer> photoDao = getHelper().getDao(Photo.class);
            Dao<Contact, Integer> contactDao = getHelper().getDao(Contact.class);

            photoDao.createOrUpdate(photo);

            contact.setPhoto(photo);
            contact.setName(editName.getText().toString());
            contact.setEmail(editEmail.getText().toString());
            contact.setPhoneNumber(editPhone.getText().toString());
            contact.setCompany(editCompany.getText().toString());
            contact.setPosition(editPosition.getText().toString());
            contact.setNotes(editNotes.getText().toString());

            contactDao.createOrUpdate(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shouldSaveToGallery = preferences.getBoolean("preference_autosave_to_gallery", false);

        if (shouldSaveToGallery) {
            savePictureToGallery(photo.getFilepath());
        }

        CharSequence text = "Saved Successfully!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }


    public void delete() {
        try {
            Dao<Photo, Integer> photoDao = getHelper().getDao(Photo.class);
            Dao<Contact, Integer> contactDao = getHelper().getDao(Contact.class);

            photoDao.delete(photo);
            contactDao.delete(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    public void addToContacts() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhoneNumber());
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.getPosition());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail());
        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, contact.getCompany());
        intent.putExtra(ContactsContract.Intents.Insert.NOTES, contact.getNotes());

        startActivity(intent);
    }


    public void savePictureToGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }


    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/octet-stream");

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an Email Client:"));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No Email Client Installed!", Toast.LENGTH_SHORT).show();
        }
    }


    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }
}

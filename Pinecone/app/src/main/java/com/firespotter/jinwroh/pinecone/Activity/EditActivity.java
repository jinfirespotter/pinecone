package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.ContactDataSource;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.Database.PhotoDataSource;
import com.firespotter.jinwroh.pinecone.Module.ImageReader;
import com.firespotter.jinwroh.pinecone.Module.TextExtractor;
import com.firespotter.jinwroh.pinecone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class EditActivity extends PhotoActivity {

    PhotoDataSource photoDataSource;
    ContactDataSource contactDataSource;

    private Photo photo;
    private Contact contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.photoDataSource = new PhotoDataSource(this);
        this.contactDataSource = new ContactDataSource(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        this.photo = (Photo) intent.getSerializableExtra(PhotoActivity.PHOTO_ACTIVITY_PHOTO);
        this.contact = (Contact) intent.getSerializableExtra(PhotoActivity.PHOTO_ACTIVITY_CONTACT);

        // Has the picture been taken? If true, it's a new card
        // If false, we're just editing an existing card.
        Boolean pictureTaken = intent.getBooleanExtra(PhotoActivity.PHOTO_ACTIVITY_FIRST, false);

        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream(new FileInputStream(this.photo.getFilepath()));
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

        // Only scan the picture again when it's a new picture is taken.
        if (pictureTaken) {
            rescanPicture();
        }
        else {
            editName.setText(this.contact.getName());
            editCompany.setText(this.contact.getCompany());
            editPosition.setText(this.contact.getPosition());
            editEmail.setText(this.contact.getEmail());
            editPhone.setText(this.contact.getPhoneNumber());
            editNotes.setText(this.contact.getNotes());
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
            case R.id.action_save:
                this.save();
                break;

            case R.id.action_rescan_picture:
                this.rescanPicture();
                break;

            case R.id.action_add_to_contacts:
                this.addToContacts();
                break;

            case R.id.action_save_to_gallery:
                this.savePictureToGallery(this.photo.getFilepath());
                break;

            case R.id.action_retake_picture:
                super.retakePicture(this.photo, this.contact);
                break;

            case R.id.action_send_email:
                this.sendEmail();
                break;

            case R.id.action_delete:
                this.delete();
                break;

            case android.R.id.home:
                this.save();
                super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void rescanPicture() {
        new ImageScanOperation().execute(photo);
    }


    private class ImageScanOperation extends AsyncTask<Photo, String, String> {

        protected void onPreExecute() {
            Context context = getApplicationContext();
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
                ImageReader imageReader = new ImageReader(getApplicationContext(), image);
                text = imageReader.convertImageToText();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return text;
        }

        protected void onPostExecute(String text) {
            TextExtractor textExtractor = new TextExtractor(text);

            String emailString = textExtractor.extractEmail();
            String phoneNumberString = textExtractor.extractPhoneNumber();

            System.out.println(phoneNumberString);

            EditText editNotes = (EditText) findViewById(R.id.edit_notes);
            EditText editEmail = (EditText) findViewById(R.id.edit_email);

            editNotes.setText(text);
            editEmail.setText(emailString);

            Context context = getApplicationContext();
            CharSequence toastText = "Scanning Complete!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }
    }


    public void save() {
        try {
            this.photoDataSource.open();
            this.contactDataSource.open();

            long photoId = this.photoDataSource.updateOrInsert(photo);

            EditText nameText = (EditText) findViewById(R.id.edit_name);
            EditText companyText = (EditText) findViewById(R.id.edit_company);
            EditText titleText = (EditText) findViewById(R.id.edit_position);
            EditText emailText = (EditText) findViewById(R.id.edit_email);
            EditText phoneText = (EditText) findViewById(R.id.edit_phone);
            EditText notesText = (EditText) findViewById(R.id.edit_notes);

            this.contact.setPhotoId(photoId);
            this.contact.setName(nameText.getText().toString());
            this.contact.setEmail(emailText.getText().toString());
            this.contact.setPhoneNumber(phoneText.getText().toString());
            this.contact.setCompany(companyText.getText().toString());
            this.contact.setPosition(titleText.getText().toString());
            this.contact.setNotes(notesText.getText().toString());

            this.contactDataSource.updateOrInsert(this.contact);

            this.photoDataSource.close();
            this.contactDataSource.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        Context context = getApplicationContext();
        CharSequence text = "Saved Successfully!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public void delete() {
        try {
            this.photoDataSource.open();
            this.contactDataSource.open();

            this.photoDataSource.deletePhoto(photo);
            this.contactDataSource.deleteContact(contact);

            this.photoDataSource.close();
            this.contactDataSource.close();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addToContacts() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, this.contact.getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, this.contact.getPhoneNumber());
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, this.contact.getPosition());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, this.contact.getEmail());
        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, this.contact.getCompany());
        intent.putExtra(ContactsContract.Intents.Insert.NOTES, this.contact.getNotes());

        startActivity(intent);
    }


    public void savePictureToGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/octet-stream");

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an Email Client:"));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No Email Client Installed!", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.firespotter.jinwroh.pinecone.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

        photoDataSource = new PhotoDataSource(this);
        contactDataSource = new ContactDataSource(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        photo = (Photo) intent.getSerializableExtra(PhotoActivity.PHOTO_ACTIVITY_PHOTO);
        contact = (Contact) intent.getSerializableExtra(PhotoActivity.PHOTO_ACTIVITY_CONTACT);

        Boolean pictureTaken = intent.getBooleanExtra(PhotoActivity.PHOTO_ACTIVITY_FIRST, false);

        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream(new FileInputStream(photo.getFilepath()));
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

        if (pictureTaken) {
            ImageReader imageReader = new ImageReader(this, image);
            String text = imageReader.convertImageToText();

            editNotes.setText(text);
        }
        else {
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
            case R.id.action_save:
                this.save();
                break;

            case R.id.action_rescan_picture:
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
                break;

            case R.id.action_delete:
                this.delete();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    public void rescanPicture() {

    }


    public void save() {

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

        Context context = getApplicationContext();
        CharSequence text = "Saved Successfully!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public void delete() {

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
        System.out.println(path);

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


}

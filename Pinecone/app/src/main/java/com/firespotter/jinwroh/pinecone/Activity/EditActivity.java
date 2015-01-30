package com.firespotter.jinwroh.pinecone.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firespotter.jinwroh.pinecone.Database.ContactDataSource;
import com.firespotter.jinwroh.pinecone.Database.PhotoDataSource;
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

public class EditActivity extends Activity {

    private String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        this.filepath = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);
        File file = new File(filepath);
        Bitmap photo = null;
        try {
            photo = BitmapFactory.decodeStream(new FileInputStream(filepath));
            ImageView img=(ImageView) findViewById(R.id.edit_photo);
            img.setImageBitmap(photo);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void scanImage(View view) {
        testTess();

    }

    public void save(View view) {

        PhotoDataSource photoDataSource = new PhotoDataSource(this);
        ContactDataSource contactDataSource = new ContactDataSource(this);

        try {
            photoDataSource.open();
            contactDataSource.open();

            long photoId = photoDataSource.createPhoto(filepath);
            contactDataSource.createContact(photoId, "", "", "", "", "", "NOTES");

            photoDataSource.close();
            contactDataSource.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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

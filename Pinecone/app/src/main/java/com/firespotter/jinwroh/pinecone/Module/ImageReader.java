package com.firespotter.jinwroh.pinecone.Module;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jinroh on 2/1/15.
 */
public class ImageReader {

    private static final String LANG = "eng";

    private String destinationDirectory;

    private Context context;
    private Bitmap bitmap;

    private TessBaseAPI tess;


    public ImageReader(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        initialize();

        tess = new TessBaseAPI();
    }


    private void initialize() {

        destinationDirectory = Environment.getExternalStorageDirectory().toString() + "/pinecone/";

        String[] paths = new String[]{ destinationDirectory, destinationDirectory + "tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
        }

        String destDir = destinationDirectory + "tessdata/" + LANG + ".traineddata";

        if (!(new File(destDir)).exists()) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream in = assetManager.open("tessdata/" + LANG + ".traineddata");
                OutputStream out = new FileOutputStream(destDir);

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
        }
    }


    public void setBitmap(Bitmap bitmap) {
        bitmap = bitmap;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }


    public String convertImageToText() {
        tess.setDebug(true);
        tess.init(destinationDirectory, LANG);
        tess.setImage(bitmap);
        return tess.getUTF8Text();
    }
}

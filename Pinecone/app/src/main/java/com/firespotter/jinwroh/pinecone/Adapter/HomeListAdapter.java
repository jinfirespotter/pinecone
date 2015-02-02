package com.firespotter.jinwroh.pinecone.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by jinroh on 1/30/15.
 */
public class HomeListAdapter extends BaseAdapter {

    private Context context;
    private List<HomeListItem> navItems;


    public HomeListAdapter(Context context, List<HomeListItem> navItems) {
        this.context = context;
        this.navItems = navItems;
    }


    @Override
    public int getCount() {
        return navItems.size();
    }


    @Override
    public Object getItem(int position) {
        return navItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.home_list_item, null);
        }

        convertView.setFocusable(false);
        convertView.setClickable(false);

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.thumbnail2);

        TextView nameText = (TextView) convertView.findViewById(R.id.name);
        TextView positionText = (TextView) convertView.findViewById(R.id.position);
        TextView companyText = (TextView) convertView.findViewById(R.id.company);

        Photo photo = navItems.get(position).getPhoto();

        File file = new File(photo.getFilepath());
        try {
            Bitmap thumbnail  = BitmapFactory.decodeStream(new FileInputStream(file));

            ThumbnailUtils thumbnailUtils = new ThumbnailUtils();

            imgIcon.setImageBitmap(thumbnailUtils.extractThumbnail(thumbnail, 120, 80));

            nameText.setText(navItems.get(position).getContact().getName());
            positionText.setText(navItems.get(position).getContact().getPosition());
            companyText.setText(navItems.get(position).getContact().getCompany());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}

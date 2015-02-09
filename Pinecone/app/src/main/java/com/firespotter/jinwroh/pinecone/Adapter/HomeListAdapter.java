package com.firespotter.jinwroh.pinecone.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firespotter.jinwroh.pinecone.Database.Contact;
import com.firespotter.jinwroh.pinecone.Database.Photo;
import com.firespotter.jinwroh.pinecone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinroh on 1/30/15.
 */
public class HomeListAdapter extends BaseAdapter {

    private Context context;
    private List<HomeListItem> navItems;
    private List<HomeListItem> navItemsCopy;
    private LayoutInflater mInflater;

    private static final int THUMBNAIL_WIDTH = 120;
    private static final int THUMBNAIL_HEIGHT = 80;

    public HomeListAdapter(Context context, List<HomeListItem> navItems) {
        this.context = context;
        setItemList(navItems);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return navItems.size();
    }


    @Override
    public HomeListItem getItem(int position) {
        return navItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HomeListItemViewHolder homeListItemViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_list_item, null);

            homeListItemViewHolder = new HomeListItemViewHolder();
            homeListItemViewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail2);
            homeListItemViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            homeListItemViewHolder.title = (TextView) convertView.findViewById(R.id.position);
            homeListItemViewHolder.company = (TextView) convertView.findViewById(R.id.company);

            convertView.setTag(homeListItemViewHolder);

            // Must set these two values to false to stop the view from interfering with
            // onItemClickListener for Adapter view. (onItemClickListener won't work
            // if these are not set to false.
            convertView.setFocusable(false);
            convertView.setClickable(false);

        } else {
            homeListItemViewHolder = (HomeListItemViewHolder) convertView.getTag();

        }

        homeListItemViewHolder.setValues(getItem(position).getContact(), position);

        return convertView;
    }


    public void setItemList(List<HomeListItem> homeListItemList) {
        navItems = homeListItemList;
        navItemsCopy = new ArrayList<HomeListItem>();
        navItemsCopy.addAll(navItems);
        notifyDataSetChanged();
    }


    public void filter(String filterText) {
        filterText = filterText.toLowerCase().trim();

        navItems.clear();
        if (filterText.length() != 0) {
            for (HomeListItem photoContactPair : navItemsCopy) {
                String infoString = photoContactPair.getContact().toString().toLowerCase();
                if (infoString.contains(filterText)) {
                    navItems.add(photoContactPair);
                }
            }
            notifyDataSetChanged();
        }
        else {
            navItems.addAll(navItemsCopy);
        }
    }


    static class HomeListItemViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView title;
        TextView company;
        int position;

        public void setValues(Contact contact, int position) {

            Photo photo = contact.getPhoto();
            File file = new File(photo.getFilepath());
            try {
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file));
                ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
                thumbnail.setImageBitmap(thumbnailUtils.extractThumbnail(
                        image,
                        THUMBNAIL_WIDTH,
                        THUMBNAIL_HEIGHT));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            name.setText(contact.getName());
            title.setText(contact.getPosition());
            company.setText(contact.getCompany());
            this.position = position;
        }
    }
}

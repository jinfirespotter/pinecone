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

    private static final int THUMBNAIL_WIDTH = 120;
    private static final int THUMBNAIL_HEIGHT = 80;

    public HomeListAdapter(Context context, List<HomeListItem> navItems) {
        this.context = context;
        this.navItems = navItems;
    }


    @Override
    public int getCount() {
        return this.navItems.size();
    }


    @Override
    public Object getItem(int position) {
        return this.navItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HomeListItemViewHolder homeListItemViewHolder;

        if (convertView == null) {
            LayoutInflater mInflater =
                    (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.home_list_item, null);

            homeListItemViewHolder = new HomeListItemViewHolder();
            homeListItemViewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail2);
            homeListItemViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            homeListItemViewHolder.title = (TextView) convertView.findViewById(R.id.position);
            homeListItemViewHolder.company = (TextView) convertView.findViewById(R.id.company);

            convertView.setTag(homeListItemViewHolder);
        }
        else {
            homeListItemViewHolder = (HomeListItemViewHolder) convertView.getTag();
        }

        // Must set these two values to false to stop the view from interfering with
        // onItemClickListener for Adapter view. (onItemClickListener won't work
        // if these are not set to false.
        convertView.setFocusable(false);
        convertView.setClickable(false);

        Photo photo = this.navItems.get(position).getPhoto();

        File file = new File(photo.getFilepath());
        try {
            Bitmap thumbnail  = BitmapFactory.decodeStream(new FileInputStream(file));

            ThumbnailUtils thumbnailUtils = new ThumbnailUtils();

            homeListItemViewHolder.thumbnail.setImageBitmap(thumbnailUtils.extractThumbnail(
                    thumbnail,
                    this.THUMBNAIL_WIDTH,
                    this.THUMBNAIL_HEIGHT));
            homeListItemViewHolder.name.setText(this.navItems.get(position).getContact().getName());
            homeListItemViewHolder.title.setText(this.navItems.get(position).getContact().getPosition());
            homeListItemViewHolder.company.setText(this.navItems.get(position).getContact().getCompany());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class HomeListItemViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView title;
        TextView company;
        int position;
    }
}

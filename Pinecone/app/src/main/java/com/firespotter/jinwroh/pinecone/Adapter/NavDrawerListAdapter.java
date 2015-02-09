package com.firespotter.jinwroh.pinecone.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firespotter.jinwroh.pinecone.R;

import java.util.ArrayList;

/**
 * Created by jinroh on 1/29/15.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private LayoutInflater mInflater;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return navDrawerItems.size();
    }


    @Override
    public NavDrawerItem getItem(int position) {
        return navDrawerItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavDrawerListItemViewHolder navDrawerListItemViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);

            navDrawerListItemViewHolder = new NavDrawerListItemViewHolder();
            navDrawerListItemViewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            navDrawerListItemViewHolder.txtTitle = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(navDrawerListItemViewHolder);
        } else {
            navDrawerListItemViewHolder = (NavDrawerListItemViewHolder) convertView.getTag();
        }

        navDrawerListItemViewHolder.setValues(getItem(position));

        return convertView;
    }


    static class NavDrawerListItemViewHolder {
        ImageView imgIcon;
        TextView txtTitle;

        public void setValues(NavDrawerItem item) {
            imgIcon.setImageResource(item.getIcon());
            txtTitle.setText(item.getTitle());
        }
    }
}
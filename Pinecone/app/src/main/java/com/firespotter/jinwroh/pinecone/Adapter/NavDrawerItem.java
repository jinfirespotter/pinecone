package com.firespotter.jinwroh.pinecone.Adapter;

/**
 * Created by jinroh on 1/29/15.
 */
public class NavDrawerItem {

    private String title;
    private int icon;

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}

package com.firespotter.jinwroh.pinecone.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.firespotter.jinwroh.pinecone.R;

/**
 * Created by jinroh on 2/3/15.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}

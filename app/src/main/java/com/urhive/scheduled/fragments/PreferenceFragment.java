package com.urhive.scheduled.fragments;

import android.os.Bundle;

import com.urhive.scheduled.R;

/**
 * Created by Chirag Bhatia on 27-05-2016.
 */
public class PreferenceFragment extends android.preference.PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}

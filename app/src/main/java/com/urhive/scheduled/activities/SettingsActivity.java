package com.urhive.scheduled.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.urhive.scheduled.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new com.urhive.scheduled.fragments.PreferenceFragment()).commit();
    }
}
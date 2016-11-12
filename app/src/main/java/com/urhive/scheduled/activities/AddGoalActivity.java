package com.urhive.scheduled.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.urhive.scheduled.R;

public class AddGoalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        setTitle(getString(R.string.add_goal));
    }
}

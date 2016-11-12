package com.urhive.scheduled;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.urhive.scheduled.adapters.ButtonAdapter;
import com.urhive.scheduled.models.GoalReminder;

import java.util.ArrayList;
import java.util.List;

public class Temporary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary);

        List<GoalReminder> reminders = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            reminders.add(new GoalReminder(i));
        }

        RecyclerView recyclerView;
        ButtonAdapter adapter;
        recyclerView = (RecyclerView) findViewById(R.id.weekdays_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Temporary.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new ButtonAdapter(Temporary.this, reminders);
        recyclerView.setAdapter(adapter);
    }
}

package com.urhive.scheduled.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urhive.scheduled.R;
import com.urhive.scheduled.activities.MainActivity;
import com.urhive.scheduled.adapters.ReminderRecyclerAdapter;

/**
 * Created by Chirag Bhatia on 03-06-2016.
 */
public class ReminderFragment extends Fragment {
    public static ReminderRecyclerAdapter adapter;
    Context context;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        /*List<Reminder> reminders = Reminder.listAll(Reminder.class);*/

        recyclerView = (RecyclerView) view.findViewById(R.id.reminderRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ReminderRecyclerAdapter(context, MainActivity.reminderList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}

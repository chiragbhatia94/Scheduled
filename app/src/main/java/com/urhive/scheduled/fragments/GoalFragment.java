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
import com.urhive.scheduled.adapters.GoalRecyclerAdapter;

/**
 * Created by Chirag Bhatia on 03-06-2016.
 */
public class GoalFragment extends Fragment {
    public static GoalRecyclerAdapter adapter;
    Context context;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        context = getActivity();

        RecyclerView recyclerView;
        recyclerView = (RecyclerView) view.findViewById(R.id.goalRecyclerView);

        /*List<Reminder> reminders = Reminder.listAll(Reminder.class);*/


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GoalRecyclerAdapter(context, MainActivity.reminderList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}

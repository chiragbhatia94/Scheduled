package com.urhive.scheduled.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.urhive.scheduled.R;
import com.urhive.scheduled.activities.AddReminderActivity;

/**
 * Created by Chirag Bhatia on 23-06-2016.
 */
public class RepeatTimesFragment extends AppCompatDialogFragment {
    private TextView discardTV, saveTV, foreverTV;
    private EditText repeatTimesET;

    public static RepeatTimesFragment showRepeatTimesFragment(int times) {
        RepeatTimesFragment repeatTimesFragment = new RepeatTimesFragment();
        Bundle args = new Bundle();
        args.putInt("times", times);

        repeatTimesFragment.setArguments(args);
        return repeatTimesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repeat_times, container, false);
        repeatTimesET = (EditText) view.findViewById(R.id.getRepeatTimeTV);
        foreverTV = (TextView) view.findViewById(R.id.repeatTimesFragmentForeverTV);
        discardTV = (TextView) view.findViewById(R.id.repeatTimesFragmentDiscardTV);
        saveTV = (TextView) view.findViewById(R.id.repeatTimesFragmentSaveTV);

        getDialog().setTitle("No Of Times To Show");

        Bundle args = getArguments();
        int times = args.getInt("times");

        if (times == -1){
            repeatTimesET.setText("5");
        } else {
            repeatTimesET.setText(""+times);
        }

        foreverTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddReminderActivity.noToShow = -1;
                AddReminderActivity.noToShowTV.setText("Forever");
                dismiss();
            }
        });

        discardTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = repeatTimesET.getText().toString();
                if (temp.isEmpty()){
                    int times = 5;
                    AddReminderActivity.noToShowTV.setText("" + times);
                    AddReminderActivity.noToShow = times;
                    dismiss();
                    return;
                }
                if (temp.equals("Forever")){
                    AddReminderActivity.noToShowTV.setText("Forever");
                    AddReminderActivity.noToShow = -1;
                    dismiss();
                    return;
                }
                int times = Integer.parseInt(temp);
                AddReminderActivity.noToShowTV.setText("" + times);
                AddReminderActivity.noToShow = times;
                dismiss();
            }
        });
        return view;
    }
}

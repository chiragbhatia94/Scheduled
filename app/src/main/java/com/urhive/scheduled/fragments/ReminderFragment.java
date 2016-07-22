package com.urhive.scheduled.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.urhive.scheduled.R;
import com.urhive.scheduled.utils.DateTimeUtil;

/**
 * Created by Chirag Bhatia on 03-06-2016.
 */
public class ReminderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        TextView tv = (TextView) view.findViewById(R.id.tv);

        tv.setText(DateTimeUtil.getQuotationTime(getContext()));

        return view;
    }
}

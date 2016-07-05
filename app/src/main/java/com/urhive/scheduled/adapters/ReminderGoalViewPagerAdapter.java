package com.urhive.scheduled.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.urhive.scheduled.fragments.GoalFragment;
import com.urhive.scheduled.fragments.ReminderFragment;

/**
 * Created by Chirag Bhatia on 03-06-2016.
 */
public class ReminderGoalViewPagerAdapter extends FragmentStatePagerAdapter {
    public ReminderGoalViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ReminderFragment();
            case 1:
                return new GoalFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Reminders";
            case 1:
                return "Goals";
            default:
                return null;
        }
    }
}

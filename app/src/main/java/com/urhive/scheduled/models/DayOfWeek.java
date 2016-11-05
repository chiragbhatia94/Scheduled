package com.urhive.scheduled.models;

import com.orm.SugarRecord;

/**
 * Created by Chirag Bhatia on 11-07-2016.
 */
public class DayOfWeek extends SugarRecord {
    long reminderID;
    boolean SUNDAY;
    boolean MONDAY;
    boolean TUESDAY;
    boolean WEDNESDAY;
    boolean THURSDAY;
    boolean FRIDAY;
    boolean SATURDAY;

    public DayOfWeek(boolean[] dow) {
        SUNDAY = dow[0];
        MONDAY = dow[1];
        TUESDAY = dow[2];
        WEDNESDAY = dow[3];
        THURSDAY = dow[4];
        FRIDAY = dow[5];
        SATURDAY = dow[6];
    }

    public long getReminderID() {
        return reminderID;
    }

    public void setReminderID(long reminderID) {
        this.reminderID = reminderID;
    }
}

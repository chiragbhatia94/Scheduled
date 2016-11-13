package com.urhive.scheduled.models;

import com.orm.SugarRecord;

/**
 * Created by Chirag Bhatia on 11-07-2016.
 */
public class DayOfWeek extends SugarRecord {

    boolean SUNDAY;
    boolean MONDAY;
    boolean TUESDAY;
    boolean WEDNESDAY;
    boolean THURSDAY;
    boolean FRIDAY;
    boolean SATURDAY;
    int reminderID;

    public DayOfWeek() {

    }

    public DayOfWeek(boolean[] dow) {
        SUNDAY = dow[0];
        MONDAY = dow[1];
        TUESDAY = dow[2];
        WEDNESDAY = dow[3];
        THURSDAY = dow[4];
        FRIDAY = dow[5];
        SATURDAY = dow[6];
    }

    public int getReminderID() {
        return reminderID;
    }

    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    public String getActiveDays() {
        //String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String activeDays = "";

        if (SUNDAY) {
            activeDays = activeDays + "S";
        }
        if (MONDAY) {
            activeDays = activeDays + "M";
        }
        if (TUESDAY) {
            activeDays = activeDays + "T";
        }
        if (WEDNESDAY) {
            activeDays = activeDays + "W";
        }
        if (THURSDAY) {
            activeDays = activeDays + "T";
        }
        if (FRIDAY) {
            activeDays = activeDays + "F";
        }
        if (SATURDAY) {
            activeDays = activeDays + "S";
        }

        return activeDays;
    }

    @Override
    public String toString() {
        return "DayOfWeek{" +
                "reminderID=" + reminderID +
                ", SUNDAY=" + SUNDAY +
                ", MONDAY=" + MONDAY +
                ", TUESDAY=" + TUESDAY +
                ", WEDNESDAY=" + WEDNESDAY +
                ", THURSDAY=" + THURSDAY +
                ", FRIDAY=" + FRIDAY +
                ", SATURDAY=" + SATURDAY +
                '}';
    }
}

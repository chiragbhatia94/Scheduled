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
            activeDays = activeDays + ", Sun";
        }
        if (MONDAY) {
            activeDays = activeDays + ", Mon";
        }
        if (TUESDAY) {
            activeDays = activeDays + ", Tue";
        }
        if (WEDNESDAY) {
            activeDays = activeDays + ", Wed";
        }
        if (THURSDAY) {
            activeDays = activeDays + ", Thu";
        }
        if (FRIDAY) {
            activeDays = activeDays + ", Fri";
        }
        if (SATURDAY) {
            activeDays = activeDays + ", Sat";
        }

        return activeDays.substring(2);
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

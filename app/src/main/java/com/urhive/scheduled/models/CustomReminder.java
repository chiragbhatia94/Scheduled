package com.urhive.scheduled.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.urhive.scheduled.utils.DateTimeUtil;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Chirag Bhatia on 12-07-2016.
 */
public class CustomReminder extends SugarRecord implements Comparable<CustomReminder> {
    @Ignore
    public static final int SHOWN = 1;
    @Ignore
    public static final int NOT_SHOWN = 0;
    long reminderId;
    int number;
    String date;
    String time;
    int statusShown;
    @Ignore
    private Date dateTime;

    public CustomReminder() {

    }

    public CustomReminder(int number, String date, String time, int statusShown) {
        this.number = number;
        this.date = date;
        this.time = time;
        this.statusShown = statusShown;
    }

    public CustomReminder(long reminderId, int number, String date, String time, int statusShown) {
        this.reminderId = reminderId;
        this.number = number;
        this.date = date;
        this.time = time;
        this.statusShown = statusShown;
    }

    // static methods
    public static void sortCustomReminderListByDateTimeAndArrangeByNumber(List<CustomReminder> presetList) {
        Collections.sort(presetList);

        for (int i = 0; i < presetList.size(); i++) {
            presetList.get(i).setNumber(i + 1);
        }
    }

    public static boolean anyReminderInPast(List<CustomReminder> reminders) {
        for (CustomReminder reminder : reminders) {
            if (DateTimeUtil.isInPast(reminder.getDate(), reminder.getTime())) {
                return true;
            }
        }
        return false;
    }

    // getter & setters
    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatusShown() {
        return statusShown;
    }

    public void setStatusShown(int statusShown) {
        this.statusShown = statusShown;
    }

    public Date getDateTime() {
        String dateTimeString = getDate() + " " + getTime();
        try {
            dateTime = DateTimeUtil.dateTimeFormat.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
        this.date = DateTimeUtil.dateFormat.format(datetime);
        this.time = DateTimeUtil.timeFormat.format(datetime);
    }

    @Override
    public int compareTo(CustomReminder another) {
        return getDateTime().compareTo(another.getDateTime());
    }
}

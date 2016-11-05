package com.urhive.scheduled.models;

import android.support.annotation.NonNull;

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
public class AlarmReminders extends SugarRecord implements Comparable<AlarmReminders> {
    @Ignore
    public static final int SHOWN = 1;
    @Ignore
    public static final int NOT_SHOWN = 0;
    @Ignore
    public static final int NORMAL_ALARM_REMINDER = 0;
    @Ignore
    public static final int ADVANCE_ALARM_REMINDER = 1;
    long reminderId;
    int number;
    String date;
    String time;
    int statusShown;
    int reminderType;
    @Ignore
    private Date dateTime;

    public AlarmReminders() {

    }

    public AlarmReminders(int number, String date, String time, int statusShown, int reminderType) {
        this.number = number;
        this.date = date;
        this.time = time;
        this.statusShown = statusShown;
        this.reminderType = reminderType;
    }

    public AlarmReminders(long reminderId, int number, String date, String time, int statusShown, int reminderType) {
        this.reminderId = reminderId;
        this.number = number;
        this.date = date;
        this.time = time;
        this.statusShown = statusShown;
        this.reminderType = reminderType;
    }

    public AlarmReminders(AlarmReminders reminder) {
        this.reminderId = reminder.reminderId;
        this.number = reminder.number;
        this.date = reminder.date;
        this.time = reminder.time;
        this.statusShown = reminder.statusShown;
        this.reminderType = reminder.reminderType;
    }

    // static methods
    public static void sortCustomReminderListByDateTimeAndArrangeByNumber(List<AlarmReminders> presetList) {
        Collections.sort(presetList);

        for (int i = 0; i < presetList.size(); i++) {
            presetList.get(i).setNumber(i + 1);
        }
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

    public int getReminderType() {
        return reminderType;
    }

    public void setReminderType(int reminderType) {
        this.reminderType = reminderType;
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
    public int compareTo(@NonNull AlarmReminders another) {
        return getDateTime().compareTo(another.getDateTime());
    }

    @Override
    public String toString() {
        return "AlarmReminders{" +
                "reminderId=" + reminderId +
                ", number=" + number +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", statusShown=" + statusShown +
                ", dateTime=" + dateTime +
                ", reminderType=" + reminderType +
                '}';
    }
}

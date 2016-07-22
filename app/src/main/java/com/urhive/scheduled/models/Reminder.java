package com.urhive.scheduled.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.urhive.scheduled.utils.DateTimeUtil;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Chirag Bhatia on 06-06-2016.
 */
public class Reminder extends SugarRecord implements Comparable<Reminder> {
    @Ignore
    public static final int ACTIVE = 1;
    @Ignore
    public static final int INACTIVE = 0;
    @Ignore
    public static final int DO_NOT_REPEAT = 0;
    @Ignore
    public static final int EVERY_DAY = 1;
    @Ignore
    public static final int EVERY_WEEK = 2;
    @Ignore
    public static final int EVERY_MONTH = 3;
    @Ignore
    public static final int EVERY_YEAR = 4;
    @Ignore
    public static final int SPECIFIC_DAY_OF_WEEK = 5;
    @Ignore
    public static final int REVISION_PRESET = 6;
    @Ignore
    public static final int ALTERNATE_DAYS = 7;
    @Ignore
    public static final int MWF_TTS_ALTERNATE = 8;
    @Ignore
    public static final int CUSTOM = 9;
    @Ignore
    public static final int STATUS_NORMAL = 1;
    @Ignore
    public static final int STATUS_ARCHIEVED = 2;
    @Ignore
    public static final int STATUS_DELETED = 3;
    @Ignore
    public static final int TYPE_NOTIFICATION = 0;
    @Ignore
    public static final int TYPE_ALARM = 1;
    String title;
    String content;
    String date;
    String time;
    int active;
    long categoryId;
    int noToShow;
    int noShown;
    int repeatType;
    long inAdvanceMillis;
    // added later on
    int status;
    int reminderType;
    @Ignore
    private Date dateTime;

    public Reminder() {
    }

    public Reminder(String title, String content, String date, String time, int type, int active,
                    long categoryId, int noToShow, int noShown, int repeatType, long inAdvanceMillis,
                    int status, int reminderType) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.active = active;
        this.categoryId = categoryId;
        this.noToShow = noToShow;
        this.noShown = noShown;
        this.repeatType = repeatType;
        this.inAdvanceMillis = inAdvanceMillis;
        this.status = status;
        this.reminderType = reminderType;
    }

    // GETTER & SETTER
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getNoToShow() {
        return noToShow;
    }

    public void setNoToShow(int noToShow) {
        this.noToShow = noToShow;
    }

    public int getNoShown() {
        return noShown;
    }

    public void setNoShown(int noShown) {
        this.noShown = noShown;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public long getInAdvanceMillis() {
        return inAdvanceMillis;
    }

    public void setInAdvanceMillis(long inAdvanceMillis) {
        this.inAdvanceMillis = inAdvanceMillis;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReminderType() {
        return reminderType;
    }

    public void setReminderType(int reminderType) {
        this.reminderType = reminderType;
    }

    // TO STRING
    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", dateTime=" + dateTime +
                ", active=" + active +
                ", categoryId=" + categoryId +
                ", noToShow=" + noToShow +
                ", noShown=" + noShown +
                ", repeatType=" + repeatType +
                ", inAdvanceMillis=" + inAdvanceMillis +
                ", status =" + status +
                ", reminderType =" + reminderType +
                '}';
    }

    // for sorting by date time
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
    public int compareTo(Reminder another) {
        return getDateTime().compareTo(another.getDateTime());
    }
}

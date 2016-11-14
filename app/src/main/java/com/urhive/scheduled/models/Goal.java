package com.urhive.scheduled.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.urhive.scheduled.utils.DateTimeUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Chirag Bhatia on 08-11-2016.
 */
public class Goal extends SugarRecord implements Comparable<Goal> {
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
    public static final int MODE_NORMAL = 0;
    @Ignore
    public static final int MODE_ADVANCED = 1;

    String title;
    String content;
    String date;
    String time;
    int longestStreak;
    int currentStreak;
    long categoryId;
    int noToShow;
    int noShown;
    int repeatType;
    // added later on
    int status;
    int mode;

    @Ignore
    private Date dateTime;

    public Goal() {
    }

    public Goal(String title, String content, String date, String time, int longestStreak, int
            currentStreak, long categoryId, int noToShow, int noShown, int repeatType, int
                        status, int mode) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.longestStreak = longestStreak;
        this.currentStreak = currentStreak;
        this.categoryId = categoryId;
        this.noToShow = noToShow;
        this.noShown = noShown;
        this.repeatType = repeatType;
        this.status = status;
        this.mode = mode;
    }

    public static String getActiveDays(Long goalId) {
        List<DayOfWeek> dow = DayOfWeek.find(DayOfWeek.class, "GOAL_ID = ?", String.valueOf
                (goalId));
        DayOfWeek daysActive = dow.get(0);
        return daysActive.getActiveDays();
    }

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

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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
    public int compareTo(Goal another) {
        return getDateTime().compareTo(another.getDateTime());
    }
}

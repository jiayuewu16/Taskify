package com.example.taskify.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParseClassName("Task")
public class Task extends ParseObject implements Comparable<Task> {

    public static final String KEY_TASK_NAME = "taskName";
    public static final String KEY_POINTS_VALUE = "pointsValue";
    public static final String KEY_USER = "user";
    public static final String KEY_ALARM_TIME = "alarmTime";

    public Task() {}

    public Task(String taskName, int pointsValue, Date alarmTime, ParseUser user) {
        this.setTaskName(taskName);
        this.setPointsValue(pointsValue);
        this.setUser(user);
        this.setAlarmTime(alarmTime);
    }

    public Task(String taskName, int pointsValue, Date alarmTime, List<ParseUser> users) {
        this.setTaskName(taskName);
        this.setPointsValue(pointsValue);
        this.setUser(users);
        this.setAlarmTime(alarmTime);
    }

    public void setTaskName(String taskName) {
        put(KEY_TASK_NAME, taskName);
    }

    public void setPointsValue(int pointsValue) {
        put(KEY_POINTS_VALUE, pointsValue);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setUser(List<ParseUser> users) {
        if (!users.isEmpty()) {
            put(KEY_USER, users.get(0)); // Stores one user for now. Change for stretch goals.
        }
        // Silently fail otherwise.
    }

    public void setAlarmTime(Date alarmTime) {
        put(KEY_ALARM_TIME, alarmTime);
    }

    public String getTaskName() {
        return getString(KEY_TASK_NAME);
    }

    public int getPointsValue() {
        return getInt(KEY_POINTS_VALUE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public Date getAlarmTime() {
        return getDate(KEY_ALARM_TIME);
    }

    public String getAlarmTimeString() {
        String newDateFormat = "hh:mm aa";
        SimpleDateFormat newSimpleDateFormat = new SimpleDateFormat(newDateFormat, Locale.ENGLISH);
        newSimpleDateFormat.setLenient(true);

        return newSimpleDateFormat.format(getAlarmTime());
    }

    @Override
    public int compareTo(Task o) {
        return this.getAlarmTime().compareTo(o.getAlarmTime());
    }
}

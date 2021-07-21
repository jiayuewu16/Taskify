package com.example.taskify.models;

import android.util.Log;

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
    public static final String KEY_USERS = "users";
    public static final String KEY_ALARM_TIME = "alarmTime";

    public Task() {}

    public Task(String taskName, int pointsValue, Date alarmTime, List<ParseUser> users) {
        this.setTaskName(taskName);
        this.setPointsValue(pointsValue);
        this.setUsers(users);
        this.setAlarmTime(alarmTime);
    }

    public void setTaskName(String taskName) {
        put(KEY_TASK_NAME, taskName);
    }

    public void setPointsValue(int pointsValue) {
        put(KEY_POINTS_VALUE, pointsValue);
    }

    public void setUsers(List<ParseUser> users) {
        put(KEY_USERS, users);
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

    public List<ParseUser> getUsers() {
        return getList(KEY_USERS);
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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        return this.getObjectId().equals(((Task)o).getObjectId());
    }
}

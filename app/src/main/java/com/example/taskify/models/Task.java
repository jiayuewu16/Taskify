package com.example.taskify.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

@ParseClassName("Task")
public class Task extends ParseObject implements Comparable<Task> {

    private static final String TAG = "Task";
    public static final String KEY_TASK_NAME = "taskName";
    public static final String KEY_POINTS_VALUE = "pointsValue";
    public static final String KEY_USERS = "users";
    public static final String KEY_ALARM = "alarm";

    public Task() {}

    public Task(String taskName, int pointsValue, Alarm alarm, List<ParseUser> users) {
        this.setTaskName(taskName);
        this.setPointsValue(pointsValue);
        this.setUsers(users);
        this.setAlarm(alarm);
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

    public void setAlarm(Alarm alarm) {
        put(KEY_ALARM, alarm);
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

    public Alarm getAlarm() {
        try {
            return getParseObject(KEY_ALARM).fetchIfNeeded();
        }
        catch (ParseException e) {
            Log.e(TAG, "Error fetching alarm.", e);
        }
        return new Alarm();
    }

    public Date getAlarmTime() {
        return getAlarm().getDate();
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

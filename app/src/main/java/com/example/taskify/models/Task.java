package com.example.taskify.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@ParseClassName("Task")
public class Task extends ParseObject implements Comparable<Task> {

    private static final String TAG = "Task";
    public static final String KEY_TASK_NAME = "taskName";
    public static final String KEY_POINTS_VALUE = "pointsValue";
    public static final String KEY_USERS = "users";
    public static final String KEY_ALARM = "alarm";
    private Alarm alarm;

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
        this.alarm = alarm;
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
        if (alarm != null) {
            Log.i(TAG, "Using stored alarm");
            return alarm;
        }
        try {
            Log.i(TAG, "Fetching alarm from database");
            alarm = Objects.requireNonNull(getParseObject(KEY_ALARM)).fetchIfNeeded();
            return alarm;
        }
        catch (ParseException pe) {
            Log.e(TAG, "Error fetching alarm.", pe);
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

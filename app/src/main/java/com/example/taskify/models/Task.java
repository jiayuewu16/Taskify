package com.example.taskify.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.taskify.network.AlarmBroadcastReceiver;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.TimeUtil;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            Alarm alarm = getParseObject(KEY_ALARM).fetchIfNeeded();
            return alarm;
        }
        catch (ParseException e) {
            Log.e(TAG, "Error fetching alarm.", e);
        }
        return new Alarm();
    }

    public Date getAlarmTime() {
        return getAlarm().getDate();
    }

    public String getAlarmTimeString() {
        return TimeUtil.dateToAlarmTimeString(getAlarmTime());
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

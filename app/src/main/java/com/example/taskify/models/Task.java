package com.example.taskify.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseObject;

@ParseClassName("Task")
public class Task extends ParseObject{

    public static final String KEY_TASK_NAME = "taskName";
    public static final String KEY_POINTS_VALUE = "pointsValue";
    public static final String KEY_USER = "user";

    public Task() {}

    public Task(String taskName, int pointsValue, ParseUser user) {
        this.setTaskName(taskName);
        this.setPointsValue(pointsValue);
        this.setUser(user);
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

    public String getTaskName() {
        return getString(KEY_TASK_NAME);
    }

    public int getPointsValue() {
        return getInt(KEY_POINTS_VALUE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

}

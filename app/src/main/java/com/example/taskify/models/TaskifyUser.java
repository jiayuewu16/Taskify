package com.example.taskify.models;

import com.parse.ParseUser;

public class TaskifyUser extends ParseUser {
    private static final String TAG = "ParseUserUtil";
    private static final String KEY_IS_PARENT = "isParent";
    public static final String KEY_POINTS_TOTAL = "pointsTotal";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";

    public TaskifyUser() {
        super();
    }

    public boolean getIsParent() {
        return getBoolean(KEY_IS_PARENT);
    }

    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public int getPointsTotal() {
        return getInt(KEY_POINTS_TOTAL);
    }

    public void setIsParent(boolean isParent) {
        put(KEY_IS_PARENT, isParent);
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRST_NAME, firstName);
    }

    public void setLastName(String lastName) {
        put(KEY_LAST_NAME, lastName);
    }

    public void addPointsValue(int pointsValue) {
        put(KEY_POINTS_TOTAL, getPointsTotal() + pointsValue);
    }
}

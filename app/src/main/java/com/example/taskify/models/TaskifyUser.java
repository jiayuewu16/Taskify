package com.example.taskify.models;

import android.util.Log;
import android.widget.Toast;

import com.example.taskify.util.ParseUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TaskifyUser extends ParseUser {
    private static final String TAG = "TaskifyUser";

    public static final String KEY_IS_PARENT = "isParent";
    public static final String KEY_POINTS_TOTAL = "pointsTotal";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PROFILE_PHOTO_FILE = "profilePhoto";
    public static final String KEY_PARENT = "parent";
    public static final String KEY_USERNAME = "username";

    public TaskifyUser() {
        super();
    }

    public boolean isParent() {
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

    public ParseFile getProfilePhoto() {
        return getParseFile(KEY_PROFILE_PHOTO_FILE);
    }

    public TaskifyUser getParent() {
        return (TaskifyUser) getParseUser(KEY_PARENT);
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

    public void setProfilePhoto(ParseFile photoFile) {
        put(KEY_PROFILE_PHOTO_FILE, photoFile);
    }

    public void setParent(ParseUser user) {
        put(KEY_PARENT, user);
    }

    public List<TaskifyUser> queryChildren() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(KEY_PARENT, this);
        try {
            List<ParseUser> parseUsers = query.find();
            Log.i(TAG, parseUsers.toString());
            return (List<TaskifyUser>)(List<?>) parseUsers;
        }
        catch (ParseException e) {
            Log.e(TAG, ParseUtil.parseExceptionToErrorText(e), e);
            return null;
        }

    }

    public static TaskifyUser queryUser(String username) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(KEY_USERNAME, username);
        try {
            List<ParseUser> parseUsers = query.find();
            if (parseUsers.size() == 0) {
                Log.e(TAG, String.format("No user found with username \"%s\".", username));
                return null;
            }
            if (parseUsers.size() != 1) {
                Log.e(TAG, String.format("Multiple users found with username \"%s\".", username));
                return null;
            }
            return (TaskifyUser) parseUsers.get(0);
        }
        catch (ParseException e) {
            Log.e(TAG, ParseUtil.parseExceptionToErrorText(e), e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        return this.getObjectId().equals(((ParseUser)o).getObjectId());
    }
}

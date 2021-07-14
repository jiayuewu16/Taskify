package com.example.taskify.util;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.concurrent.atomic.AtomicReference;

public class ParseUserUtil {

    private static final String TAG = "ParseUserUtil";
    private static final String KEY_IS_PARENT = "isParent";
    public static final String KEY_POINTS_TOTAL = "pointsTotal";

    public static boolean getIsParent(ParseUser user) {
        return user.getBoolean(KEY_IS_PARENT);
    }

    public static int getPointsTotal(ParseUser user) {
        return user.getInt(KEY_POINTS_TOTAL);
    }

    public static void setIsParent(ParseUser user, boolean isParent) {
        user.put(KEY_IS_PARENT, isParent);
    }

    public static void addPointsValue(ParseUser user, int pointsValue) {
        user.put(KEY_POINTS_TOTAL, getPointsTotal(user) + pointsValue);
    }

    public static void saveUser(ParseUser user) throws ParseException {
        // Tutorial: https://stackoverflow.com/questions/31585090/java-how-to-throw-exception-outside-an-anonymous-class
        final AtomicReference<ParseException> exceptionAtomicReference = new AtomicReference<>();
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e){
                exceptionAtomicReference.set(e);
            }
        });
        if (exceptionAtomicReference.get() != null) {
            throw exceptionAtomicReference.get();
        }
    }
}

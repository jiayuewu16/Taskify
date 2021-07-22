package com.example.taskify.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParseClassName("Alarm")
public class Alarm extends ParseObject {
    private static final String KEY_DATE = "date";
    private static final String KEY_RECURRING = "recurringFlag";
    private static final String KEY_RECURRING_WEEKDAYS = "recurringWeekdays";

    public Alarm() {}

    public Alarm(Date date, boolean recurring, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        this(date, recurring, Arrays.asList(mon, tue, wed, thu, fri, sat, sun));
    }

    public Alarm(Date date, boolean recurring) {
        // Assume recurring false.
        this(date, recurring, Arrays.asList(false, false, false, false, false, false, false));
    }

    public Alarm(Date date, boolean recurring, List<Boolean> recurringWeekdays) {
        setDate(date);
        setRecurring(recurring);
        setRecurringWeekdays(recurringWeekdays);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public boolean getRecurring() {
        return getBoolean(KEY_RECURRING);
    }

    public List<Boolean> getRecurringWeekdays() {
        return getList(KEY_RECURRING_WEEKDAYS);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public void setRecurring(boolean recurring) {
        put(KEY_RECURRING, recurring);
    }

    public void setRecurringWeekdays(List<Boolean> recurringWeekdays) {
        put(KEY_RECURRING_WEEKDAYS, recurringWeekdays);
    }
}
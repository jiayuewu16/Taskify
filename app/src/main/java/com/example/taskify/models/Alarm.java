package com.example.taskify.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ParseClassName("Alarm")
public class Alarm extends ParseObject {
    private static final String KEY_DATE = "date";
    private static final String KEY_RECURRING = "recurringFlag";
    private static final String KEY_RECURRING_WEEKDAYS = "recurringWeekdays";

    public Alarm() {}

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

    public boolean isRecurring() {
        return getRecurring();
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
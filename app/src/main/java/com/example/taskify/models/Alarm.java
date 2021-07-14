package com.example.taskify.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class Alarm {
    private String title;
    private int hour;
    private int minute;

    public Alarm(String title, int hour, int minute) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
    }

    public void saveAlarm() {

    }
}
package com.example.taskify.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.taskify.models.Alarm;
import com.example.taskify.models.Task;
import com.example.taskify.network.AlarmBroadcastReceiver;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public final static long SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final String TAG = "TimeUtil";

    public static String dateToAlarmTimeString(Date date) {
        String newDateFormat = "hh:mm aa";
        SimpleDateFormat newSimpleDateFormat = new SimpleDateFormat(newDateFormat, Locale.ENGLISH);
        newSimpleDateFormat.setLenient(true);
        return newSimpleDateFormat.format(date);
    }

    public static void startAlarm(Context context, Task task) {
        // Tutorial: https://learntodroid.com/how-to-create-a-simple-alarm-clock-app-in-android/
        Alarm alarm = task.getAlarm();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(context, AlarmBroadcastReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task);
        receiverIntent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, GeneralUtil.randomInt(), receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getDate().getHours());
        calendar.set(Calendar.MINUTE, alarm.getDate().getMinutes());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If alarm time has already passed, play the alarm tomorrow;
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!task.getAlarm().isRecurring()) {
            Log.i(TAG, "Set one-time alarm for " + task.getTaskName() + ".");
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else {
            // Check the alarm daily. If the alarm should be played on the current day of the week,
            // play the alarm.
            Log.i(TAG, "Set repeating alarm for " + task.getTaskName() + ".");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), SECONDS_PER_DAY, pendingIntent);
        }
    }

    public static boolean alarmIsToday(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK) - 1; // `today` has range [0, 6], where 0 is Sunday.

        return alarm.getRecurringWeekdays().get(today);
    }
}

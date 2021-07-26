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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TimeUtil {

    private final static String requestCodeFileName = "requestCode.tkf";
    public final static long SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final String TAG = "TimeUtil";

    public static void cancelAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(context, AlarmBroadcastReceiver.class);
        Bundle bundle = new Bundle();
        receiverIntent.putExtra("bundle", bundle);

        for (Integer requestCode : getPendingIntentRequestCodes(context)) {
            Log.i(TAG, "Canceled alarm " + requestCode);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
    }

    public static void startAlarms(Context context, List<Task> tasks) {
        List<Integer> requestCodes = new ArrayList<>();
        for (Task task : tasks) {
            int requestCode = startAlarm(context, task);
            if (requestCode != 0) {
                requestCodes.add(requestCode);
            }
        }
        savePendingIntentRequestCodes(context, requestCodes);
    }

    public static String dateToAlarmTimeString(Date date) {
        String newDateFormat = "hh:mm aa";
        SimpleDateFormat newSimpleDateFormat = new SimpleDateFormat(newDateFormat, Locale.ENGLISH);
        newSimpleDateFormat.setLenient(true);
        return newSimpleDateFormat.format(date);
    }

    public static boolean alarmIsToday(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK) - 1; // `today` has range [0, 6], where 0 is Sunday.

        return alarm.getRecurringWeekdays().get(today);
    }

    public static String getRecurringText(Alarm alarm) {
        List<Boolean> recurringWeekdays = alarm.getRecurringWeekdays();
        /*
        Based on the iOS alarm app,
        One day: Every Sunday
        Two-Six days: Mon Tue Wed Thu
        Seven days: Every day
        */
        int count = 0;
        for (boolean weekday : recurringWeekdays) {
            if (weekday) {
                count++;
            }
        }
        String outputString = "";
        if (count == 1) {
            outputString = outputString.concat("Every ");
            for (int i = 0; i < recurringWeekdays.size(); i++) {
                if (recurringWeekdays.get(i)) {
                    outputString = outputString.concat(intWeekdayToStringFull(i));
                    break;
                }
            }
        }
        else if (count < 7) {
            for (int i = 0; i < recurringWeekdays.size(); i++) {
                if (recurringWeekdays.get(i)) {
                    outputString = outputString.concat(intWeekdayToStringShort(i) + " ");
                }
            }
        }
        else {
            outputString = "Every day";
        }
        return outputString;
    }

    private static List<Integer> getPendingIntentRequestCodes(Context context) {
        File requestCodeFile = GeneralUtil.getFileUri(context, requestCodeFileName);
        List<Integer> requestCodes = new ArrayList<>();
        try {
            Scanner input = new Scanner(new FileReader(requestCodeFile));
            while (input.hasNext()) {
                requestCodes.add(Integer.parseInt(input.nextLine()));
            }
            Log.i(TAG, "Retrieved request codes " + requestCodes.toString());
        }
        catch (FileNotFoundException fe) {
            Log.e(TAG, "Error finding requestCode file or first time creating it", fe);
        }
        catch (NumberFormatException ne) {
            Log.e(TAG, "NumberFormatException in requestCode file", ne);
        }
        return requestCodes;
    }

    private static void savePendingIntentRequestCodes(Context context, List<Integer> requestCodes) {
        try {
            File requestCodeFile = GeneralUtil.getFileUri(context, requestCodeFileName);
            FileOutputStream outputStream = new FileOutputStream(requestCodeFile);
            for (Integer requestCode : requestCodes) {
                outputStream.write((requestCode+"\n").getBytes());
            }
            outputStream.close();
            Log.i(TAG, "Saved request codes " + requestCodes.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error writing to requestCode file", e);
        }
    }

    private static int startAlarm(Context context, Task task) {
        // Tutorial: https://learntodroid.com/how-to-create-a-simple-alarm-clock-app-in-android/
        Alarm alarm = task.getAlarm();
        Date date = alarm.getDate();

        // If alarm time has already passed, don't set the alarm.
        if (!alarm.isRecurring() && date.getTime() <= System.currentTimeMillis()) {
            return 0;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(context, AlarmBroadcastReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task);
        receiverIntent.putExtra("bundle", bundle);
        int requestCode = getRequestCode(task.getTaskName(), alarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!alarm.isRecurring()) {
            Log.i(TAG, "Set one-time alarm for " + task.getTaskName() + ".");
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getDate().getTime(), pendingIntent);
        }
        else {
            // Check the alarm daily. If the alarm should be played on the current day of the week,
            // play the alarm.
            Log.i(TAG, "Set repeating alarm for " + task.getTaskName() + ".");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getDate().getTime(), SECONDS_PER_DAY, pendingIntent);
        }

        return requestCode;
    }

    private static String intWeekdayToStringFull(int weekday) {
        List<String> weekdaysFull = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        if (weekday >= 0 && weekday < 7) {
            return weekdaysFull.get(weekday);
        }
        return "";
    }

    private static String intWeekdayToStringShort(int weekday) {
        List<String> weekdaysFull = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        if (weekday >= 0 && weekday < 7) {
            return weekdaysFull.get(weekday);
        }
        return "";
    }

    /**
     * Gets a requestCode for a specific task based on its attributes. In very rare edge cases, may be the
     * same as a task with a similar task name and similar alarm time.
     *
     * @param taskName name of the task/alarm.
     * @param alarm the alarm.
     * @return an integer requestCode representing the task's pending intent.
     */
    private static int getRequestCode(String taskName, Alarm alarm) {
        int taskNameLength = 15;

        String requestCodeString = taskName.substring(0, Math.min(taskName.length(), taskNameLength)) + alarm.getDate().toString() + alarm.getRecurringWeekdays().toString();
        return requestCodeString.hashCode();
    }
}

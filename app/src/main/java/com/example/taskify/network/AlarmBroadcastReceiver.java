package com.example.taskify.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.taskify.models.Alarm;
import com.example.taskify.models.Task;
import com.example.taskify.util.TimeUtil;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmBroadcastReceiver", "Alarm intent received.");

        Bundle bundle = intent.getBundleExtra("bundle");
        Task task = bundle.getParcelable("task");
        Alarm alarm = task.getAlarm();
        String taskName = task.getTaskName();

        if (!alarm.isRecurring()) {
            startAlarmService(context, taskName);
        } {
            if (TimeUtil.alarmIsToday(alarm)) {
                startAlarmService(context, taskName);
            }
        }
    }

    private void startAlarmService(Context context, String taskName) {
        Intent serviceIntent = new Intent(context, AlarmPlayingService.class);
        serviceIntent.putExtra("taskName", taskName);
        context.startForegroundService(serviceIntent);
    }
}

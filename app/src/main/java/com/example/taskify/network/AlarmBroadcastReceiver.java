package com.example.taskify.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.taskify.models.Task;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmPlayingService.class);
        serviceIntent.putExtra(Task.KEY_TASK_NAME, intent.getExtras().getString(Task.KEY_TASK_NAME));
        context.startForegroundService(serviceIntent);
        Log.i("AlarmBroadcastReceiver", "Alarm intent received.");
    }
}

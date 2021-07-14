package com.example.taskify.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmPlayingService.class);
        context.startService(serviceIntent);
        Log.i("AlarmBroadcastReceiver", "Alarm intent fired.");

    }
}

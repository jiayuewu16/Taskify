package com.example.taskify.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;

public class TaskQueryBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TaskQueryBroadcastReceiver", "Task query intent received.");
        Intent serviceIntent = new Intent(context, TaskQueryService.class);
        serviceIntent.putExtra("bundle", (Bundle) intent.getExtras().get("bundle"));
        context.startService(serviceIntent);
    }
}

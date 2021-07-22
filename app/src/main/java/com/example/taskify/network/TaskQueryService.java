package com.example.taskify.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.PhotoUtil;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskQueryService extends Service {

    private final static String TAG = "TaskQueryService";
    private final static String createdAtFileName = "createdAtDate.tkf";
    private NotificationManager notificationManager;
    private Context activityContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel(String channelId, String channelName) {
        // Tutorial: https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLightColor(getColor(R.color.red));
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Task query service activated.");

        activityContext = getApplicationContext();

        //query from database, if new task then set up that pending intent and immediately push a
        //notification saying that the user has a new task.
        Bundle bundle = intent.getBundleExtra("bundle");
        TaskifyUser user = bundle.getParcelable("user");
        if (user == null) {
            Log.e(TAG, "TaskQueryService error; user doesn't exist.");
            return START_NOT_STICKY;
        }

        File createdAtFile = GeneralUtil.getFileUri(this, createdAtFileName);
        try {
            BufferedReader input = new BufferedReader(new FileReader(createdAtFile));
            String createdAtString = input.readLine();
            Date createdAtDate = new Date(Date.parse(createdAtString));

            ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
            query = query.include(Task.KEY_USERS);
            if (user.isParent()) {
                //just query all for parent for now. fix later.
            }
            else {
                query.whereEqualTo(Reward.KEY_USERS, user);
            }
            //query.whereGreaterThan(Task.KEY_UPDATED_AT, createdAtDate);

            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with getting posts", e);
                        return;
                    }
                    Log.i(TAG, "Count: " + count + ", " + MainActivity.tasks.size());
                    if (count != MainActivity.tasks.size()) {
                        Log.i(TAG, "Tasks updated.");
                        if (user.isParent()) {
                            Log.i(TAG, "Parent tasks updated.");
                            Toast.makeText(TaskQueryService.this, "Your children have completed a task! Please refresh.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.i(TAG, "Child tasks updated.");
                            Toast.makeText(TaskQueryService.this, "Your tasks have been updated! Please refresh.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (FileNotFoundException fe) {
            Log.e(TAG, "Error finding createdAtDate file", fe);
        } catch (IOException ie) {
            Log.e(TAG, "createdAtDate file has io exceptions", ie);
        }

        //write a new createdatdate file with the current time
        Date nowDate = new Date();
        try {
            FileOutputStream outputStream = new FileOutputStream(createdAtFile);
            outputStream.write(nowDate.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    private void startNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        String NOTIFICATION_CHANNEL_ID = "com.example.taskify";
        String channelName = "Taskify";
        int notificationId = 166;
        createNotificationChannel(NOTIFICATION_CHANNEL_ID, channelName);

        String alarmTitle = getString(R.string.alarm_new_task_alert);
        Notification notificationPopup = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setSmallIcon(R.drawable.ic_taskify_logo_transparent_white)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(notificationId, notificationPopup);
        stopForeground(false);
    }
}

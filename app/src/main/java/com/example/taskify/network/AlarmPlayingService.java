package com.example.taskify.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.models.Task;

public class AlarmPlayingService extends Service {
    // Tutorial: https://www.c-sharpcorner.com/article/create-alarm-android-application/

    private NotificationManager notificationManager;

    private MediaPlayer mediaSong;
    private int startId;
    private boolean isRunning;

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
        Log.i("AlarmPlayingService", "Alarm playing service activated.");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        String NOTIFICATION_CHANNEL_ID = "com.example.taskify";
        String channelName = "Taskify";
        createNotificationChannel(NOTIFICATION_CHANNEL_ID, channelName);

        String alarmTitle = intent.getStringExtra(Task.KEY_TASK_NAME);
        Notification notificationPopup = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle(alarmTitle)
                .setSmallIcon(R.drawable.ic_taskify_logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(1, notificationPopup);

        startId = 1;

        if (!this.isRunning && startId == 1) {
            this.isRunning = true;
            this.startId = 0;

            mediaSong = MediaPlayer.create(this, R.raw.bradbreeckmelody);
            mediaSong.start();
        }
        else {
            this.isRunning = false;
            this.startId = 0;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        super.onDestroy();
        this.isRunning = false;
    }
}

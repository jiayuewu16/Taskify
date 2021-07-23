package com.example.taskify.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.util.GeneralUtil;

public class AlarmPlayingService extends Service {
    // Tutorial: https://www.c-sharpcorner.com/article/create-alarm-android-application/

    private static final String NOTIFICATION_CHANNEL_ID = "com.example.taskify";
    private static final String CHANNEL_NAME = "Taskify";
    private NotificationManager notificationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        // Tutorial: https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
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

        createNotificationChannel();

        String alarmTitle = intent.getStringExtra("taskName");
        Notification notificationPopup = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setSmallIcon(R.drawable.ic_taskify_logo_transparent_white)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(GeneralUtil.randomInt(), notificationPopup);
        stopForeground(false);

        return START_NOT_STICKY;
    }
}

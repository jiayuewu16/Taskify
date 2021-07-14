package com.example.taskify.network;

import android.app.Notification;
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

    private MediaPlayer mediaSong;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AlarmPlayingService", "Alarm playing service activated.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        String alarmTitle = intent.getStringExtra(Task.KEY_TASK_NAME);
        /*NotificationCompat.Builder notificationPopup = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setSmallIcon(R.drawable.ic_taskify_logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();*/

        startId = 1;

        if (!this.isRunning && startId == 1) {
            this.isRunning = true;
            this.startId = 0;

            //notificationManager.notify(0, notificationPopup);

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

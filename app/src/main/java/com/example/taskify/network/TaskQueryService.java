package com.example.taskify.network;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.taskify.activities.MainActivity;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.GeneralUtil;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskQueryService extends Service {

    private final static String TAG = "TaskQueryService";
    private final static String createdAtFileName = "createdAtDate.tkf";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Task query service activated.");

        //query from database, if new task then set up that pending intent and immediately push a
        //notification saying that the user has a new task.
        Bundle bundle = intent.getBundleExtra("bundle");
        TaskifyUser user = bundle.getParcelable("user");
        if (user == null) {
            Log.e(TAG, "TaskQueryService error; user doesn't exist.");
            return START_NOT_STICKY;
        }

        File createdAtFile = GeneralUtil.getFileUri(this, createdAtFileName);
        /*try {
            BufferedReader input = new BufferedReader(new FileReader(createdAtFile));
            String createdAtString = input.readLine();
            Date createdAtDate = new Date(Date.parse(createdAtString));*/

            ParseQuery<Task> query;
            if (user.isParent()) {
                List<TaskifyUser> children = user.queryChildren();
                List<ParseQuery<Task>> queries = new ArrayList<>();
                for (TaskifyUser child : children) {
                    ParseQuery<Task> tempQuery = ParseQuery.getQuery(Task.class);
                    tempQuery.whereEqualTo(Task.KEY_USERS, child);
                    queries.add(tempQuery);
                }
                if (queries.isEmpty()) {
                    return START_NOT_STICKY;
                }
                query = ParseQuery.or(queries);
            }
            else {
                query = ParseQuery.getQuery(Task.class);
                query = query.include(Task.KEY_USERS);
                query.whereEqualTo(Reward.KEY_USERS, user);
            }
            //query.whereGreaterThan(Task.KEY_UPDATED_AT, createdAtDate);

            query.countInBackground((count, e) -> {
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
            });
        /*} catch (FileNotFoundException fe) {
            Log.e(TAG, "Error finding createdAtDate file", fe);
        } catch (IOException ie) {
            Log.e(TAG, "createdAtDate file has io exceptions", ie);
        }*/

        //write a new createdAtDate file with the current time
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
}

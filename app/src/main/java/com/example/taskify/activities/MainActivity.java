package com.example.taskify.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.taskify.R;
import com.example.taskify.databinding.ActivityMainBinding;
import com.example.taskify.fragments.ProfileFragment;
import com.example.taskify.fragments.RewardsFragment;
import com.example.taskify.fragments.TasksFragment;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.network.AlarmBroadcastReceiver;
import com.example.taskify.network.TaskQueryBroadcastReceiver;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_QUERY = 16;
    private final static String TAG = "MainActivity";
    private ActivityMainBinding binding;
    public final static List<Reward> rewards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar_main));

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "No user.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment tasksFragment = TasksFragment.newInstance();
        final Fragment rewardsFragment = RewardsFragment.newInstance();
        final Fragment profileFragment = ProfileFragment.newInstance();

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(
                item -> {
                    Fragment fragment;
                    if (item.getItemId() == R.id.action_rewards) {
                        fragment = rewardsFragment;
                    }
                    else if (item.getItemId() == R.id.action_profile) {
                        fragment = profileFragment;
                    }
                    else {
                        fragment = tasksFragment;
                    }
                    fragmentManager.beginTransaction().replace(binding.frameLayoutDisplayFragment.getId(), fragment).commit();
                    return true;
                });
        // Set default selection
        binding.bottomNavigationBar.setSelectedItemId(R.id.action_tasks);

        rewards.clear();
        ParseUtil.queryRewards(this, user, rewards, null);

        if (!user.isParent()) {
            startBackgroundTaskQuery(user);
        }
    }

    private void startBackgroundTaskQuery(TaskifyUser user) {
        // Tutorial: https://stackoverflow.com/questions/32138061/how-to-run-a-parse-query-in-background-or-schedule-it-in-android
        // and https://www.thepolyglotdeveloper.com/2014/10/use-broadcast-receiver-background-services-android/
        Log.i(TAG, "Query intent started.");

        int MILLISECONDS = 1000;
        int INTERVAL_SECONDS = 30;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(this, TaskQueryBroadcastReceiver.class);
        // Tutorial: https://stackoverflow.com/questions/67094131/broadcast-receiver-not-receiving-extras
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        receiverIntent.putExtra("bundle", bundle);
        //requestCode is always 16, so only one query pendingIntent is ever running.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_QUERY, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), INTERVAL_SECONDS * MILLISECONDS, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        TextView textViewPointsTotal = (TextView) menu.findItem(R.id.textViewPointsTotal).getActionView();
        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        if (user != null && !user.isParent()) {
            textViewPointsTotal.setText(String.format(getResources().getString(R.string.display_points_format), user.getPointsTotal(), getResources().getString(R.string.points_value_suffix_text)));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
package com.example.taskify.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.taskify.R;
import com.example.taskify.adapters.OnSwipeTouchListener;
import com.example.taskify.databinding.ActivityMainBinding;
import com.example.taskify.fragments.ProfileFragment;
import com.example.taskify.fragments.RewardsFragment;
import com.example.taskify.fragments.TasksFragment;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.network.TaskQueryBroadcastReceiver;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.ParseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_QUERY = 16;
    private final static String TAG = "MainActivity";
    private ActivityMainBinding binding;
    public final static List<Task> tasks = new ArrayList<>();
    public final static List<Reward> rewards = new ArrayList<>(); // Also contains redeemed rewards.
    public final static List<TaskifyUser> associatedUsers = new ArrayList<>();
    private TaskifyUser user;
    private int selectedItem;
    private NavController navController;
    public TestInterface testInterface;

    public void setActivityListener(TestInterface testInterface) {
        this.testInterface = testInterface;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (TaskifyUser) ParseUser.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "No user.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar_main));

        // Tutorial: https://medium.com/@freedom.chuks7/how-to-use-jet-pack-components-bottomnavigationview-with-navigation-ui-19fb120e3fb9
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerDisplayFragment);
        navController = navHostFragment.getNavController();
        //NavigationUI.setupWithNavController(binding.bottomNavigationBar, navController);
        BottomNavigationView bottomNavigationView = binding.bottomNavigationBar;
        selectedItem = R.id.tasks;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tasks:
                        if (selectedItem == R.id.rewards){
                            // Go to tasks from rewards
                            navController.navigate(R.id.action_rewards_to_tasks);
                        }
                        else if (selectedItem == R.id.profile){
                            // Go to tasks from profile
                            navController.navigate(R.id.action_profile_to_tasks);
                        }
                        selectedItem = R.id.tasks;
                        break;
                    case R.id.rewards:
                        if (selectedItem == R.id.tasks) {
                            // Go to rewards from tasks
                            navController.navigate(R.id.action_tasks_to_rewards);
                        }
                        else if (selectedItem == R.id.profile) {
                            // Go to rewards from tasks
                            navController.navigate(R.id.action_profile_to_rewards);
                        }
                        selectedItem = R.id.rewards;
                        break;
                    case R.id.profile:
                        if (selectedItem == R.id.tasks) {
                            // Go to profile from tasks
                            navController.navigate(R.id.action_tasks_to_profile);
                        }
                        else if (selectedItem == R.id.rewards) {
                            // Go to profile from rewards
                            navController.navigate(R.id.action_rewards_to_profile);
                        }
                        selectedItem = R.id.profile;
                        break;
                }
                return true;
            }
        });

        binding.fragmentContainerDisplayFragment.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (selectedItem == R.id.rewards) {
                    // Go to profile from rewards
                    navController.navigate(R.id.action_rewards_to_profile);
                    selectedItem = R.id.profile;
                }
                else if (selectedItem == R.id.tasks) {
                    // Go to rewards from tasks
                    navController.navigate(R.id.action_tasks_to_rewards);
                    selectedItem = R.id.rewards;
                }
            }

            @Override
            public void onSwipeRight() {
                if (selectedItem == R.id.profile) {
                    // Go to rewards from profile
                    navController.navigate(R.id.action_profile_to_rewards);
                    selectedItem = R.id.rewards;
                }
                else if (selectedItem == R.id.rewards) {
                    // Go to tasks from rewards
                    navController.navigate(R.id.action_rewards_to_tasks);
                    selectedItem = R.id.tasks;
                }
            }
        });

        populateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startBackgroundTaskQuery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopBackgroundTaskQuery();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopBackgroundTaskQuery();
    }

    private void startBackgroundTaskQuery() {
        // Tutorial: https://stackoverflow.com/questions/32138061/how-to-run-a-parse-query-in-background-or-schedule-it-in-android
        // and https://www.thepolyglotdeveloper.com/2014/10/use-broadcast-receiver-background-services-android/
        Log.i(TAG, "Query intent started.");

        int MILLISECONDS = 1000;
        int INTERVAL_SECONDS = 60;

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

    private void stopBackgroundTaskQuery() {
        Intent receiverIntent = new Intent(this, TaskQueryBroadcastReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        receiverIntent.putExtra("bundle", bundle);
        PendingIntent.getBroadcast(this, REQUEST_CODE_QUERY, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        Log.i(TAG, "Query intent stopped.");
    }

    /**
     * Populates all tasks, rewards, redeemed rewards, and children upon startup to reduce calls
     * to the database.
     */
    private void populateData() {
        tasks.clear();
        ParseUtil.queryTasks(this, user, tasks, null, testInterface);

        rewards.clear();
        ParseUtil.queryRewards(this, user, rewards, null);

        associatedUsers.clear();
        if (user.isParent()) {
            associatedUsers.addAll(user.queryChildren());
        }
        else {
            associatedUsers.add(user.getParent());
        }
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
        textViewPointsTotal.setTypeface(ResourcesCompat.getFont(this, R.font.soon_font));

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        if (user != null && !user.isParent()) {
            textViewPointsTotal.setText(GeneralUtil.getPointsValueString(user.getPointsTotal()));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public interface TestInterface {
        public void setNetworkCallCompleted();
    }
}
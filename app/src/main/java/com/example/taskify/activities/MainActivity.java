package com.example.taskify.activities;

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
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskify.R;
import com.example.taskify.adapters.OnSwipeTouchListener;
import com.example.taskify.databinding.ActivityMainBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.ParseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private ActivityMainBinding binding;
    public final static List<Task> tasks = new ArrayList<>();
    public final static List<Reward> rewards = new ArrayList<>();
    private TaskifyUser user;
    private int selectedItem;

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
        NavController navController = navHostFragment.getNavController();
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

        rewards.clear();
        ParseUtil.queryRewards(this, user, rewards, null);
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
}
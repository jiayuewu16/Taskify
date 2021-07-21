package com.example.taskify.activities;

import android.os.Bundle;
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
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public final static List<Reward> rewards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar_main));

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

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
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
        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        if (user != null && !user.isParent()) {
            textViewPointsTotal.setText(String.format(getResources().getString(R.string.display_points_format), user.getPointsTotal(), getResources().getString(R.string.points_value_suffix_text)));
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
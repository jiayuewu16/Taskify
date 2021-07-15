package com.example.taskify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.taskify.R;
import com.example.taskify.databinding.ActivityMainBinding;
import com.example.taskify.fragments.ProfileFragment;
import com.example.taskify.fragments.RewardsFragment;
import com.example.taskify.fragments.TasksFragment;
import com.example.taskify.util.ParseUserUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar_main));

        ParseUser user = ParseUser.getCurrentUser();
        binding.textViewPointsTotal.setText(ParseUserUtil.getPointsTotal(user) + " " + getResources().getString(R.string.points_value_suffix_text));

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment tasksFragment = new TasksFragment();
        final Fragment rewardsFragment = new RewardsFragment();
        final Fragment profileFragment = new ProfileFragment();

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_tasks:
                                fragment = tasksFragment;
                                break;
                            case R.id.action_rewards:
                                fragment = rewardsFragment;
                                break;
                            case R.id.action_profile:
                            default:
                                fragment = profileFragment;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(binding.frameLayoutDisplayFragment.getId(), fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        binding.bottomNavigationBar.setSelectedItemId(R.id.action_tasks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
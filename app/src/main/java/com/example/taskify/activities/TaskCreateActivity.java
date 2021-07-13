package com.example.taskify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.taskify.databinding.ActivityTaskCreateBinding;
import com.example.taskify.models.Task;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class TaskCreateActivity extends AppCompatActivity {

    private final static String TAG = "TaskCreateActivity";
    private ActivityTaskCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.checkBoxSetRecurringTrue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.checkBoxSetRecurringWeekdays.setVisibility(isChecked? View.VISIBLE : View.GONE);
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = binding.editTextTaskName.getText().toString();
                if (taskName.isEmpty()) {
                    Toast.makeText(TaskCreateActivity.this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pointsValue;
                try {
                    pointsValue = Integer.parseInt(binding.editTextPoints.getText().toString());
                    if (pointsValue < 0) throw new IllegalArgumentException();
                }
                catch (NumberFormatException ne) {
                    Toast.makeText(TaskCreateActivity.this, "Points cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser user = ParseUser.getCurrentUser();
                saveTask(taskName, pointsValue, user);
            }
        });
    }

    private void saveTask(String taskName, int pointsValue, ParseUser user) {
        Task task = new Task(taskName, pointsValue, user);
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving task", e);
                    Toast.makeText(TaskCreateActivity.this, "Error while saving task!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Task save was successful!");
                Toast.makeText(TaskCreateActivity.this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
                binding.editTextTaskName.setText("");
                binding.editTextPoints.setText("");
            }
        });
    }

    private void scheduleAlarm() {

    }
}
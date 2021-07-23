package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskify.R;
import com.example.taskify.adapters.AssignedChildAdapter;
import com.example.taskify.databinding.FragmentTaskDetailsBinding;
import com.example.taskify.models.Alarm;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.TimeUtil;
import com.parse.ParseUser;

import java.util.List;

public class TaskDetailsFragment extends DialogFragment {

    private final static String TAG = "TaskDetailsFragment";
    private FragmentTaskDetailsBinding binding;
    private Task task;
    private Activity activity;

    // Required empty public constructor.
    public TaskDetailsFragment() {}

    public static TaskDetailsFragment newInstance(Task task) {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setTask(task);
        return fragment;
    }

    private void setTask(Task task) {
        this.task = task;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaskDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();

        Alarm alarm = task.getAlarm();
        binding.textViewTaskName.setText(task.getTaskName());
        String pointsValueString = task.getPointsValue() + " " + getString(R.string.points_value_suffix_text);
        binding.textViewPointsValue.setText(pointsValueString);
        binding.textViewAlarmTime.setText(TimeUtil.dateToAlarmTimeString(alarm.getDate()));
        binding.textViewRecurring.setText(TimeUtil.getRecurringText(alarm));

        if (user.isParent()) {
            binding.textViewAssignedToText.setVisibility(View.VISIBLE);
            binding.recyclerViewAssignedChild.setVisibility(View.VISIBLE);
            List<TaskifyUser> children = (List<TaskifyUser>) (List<?>) task.getUsers();
            AssignedChildAdapter assignedChildAdapter = new AssignedChildAdapter(activity, children);
            binding.recyclerViewAssignedChild.setAdapter(assignedChildAdapter);
            binding.recyclerViewAssignedChild.setLayoutManager(new LinearLayoutManager(activity));
        }
        else {
            binding.textViewAssignedToText.setVisibility(View.GONE);
            binding.recyclerViewAssignedChild.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            activity = (FragmentActivity)context;
        }
    }
}
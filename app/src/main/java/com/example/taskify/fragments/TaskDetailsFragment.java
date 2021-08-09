package com.example.taskify.fragments;

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
import android.widget.TextView;

import com.example.taskify.R;
import com.example.taskify.adapters.AssignedChildAdapter;
import com.example.taskify.databinding.FragmentTaskDetailsBinding;
import com.example.taskify.models.Alarm;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ColorUtil;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.TimeUtil;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TaskDetailsFragment extends DialogFragment {

    private FragmentTaskDetailsBinding binding;
    private Task task;
    private FragmentActivity fragmentActivity;

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
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        Alarm alarm = task.getAlarm();

        int primaryColor = ColorUtil.getPrimaryColor(fragmentActivity);
        List<TextView> list = Arrays.asList(binding.textViewTaskName, binding.textViewAlarmTime,
                binding.textViewRecurring, binding.textViewPointsValue, binding.textViewAssignedToText);
        ColorUtil.alternateTextViewColors(list, ColorUtil.getTextColor(fragmentActivity), primaryColor);
        binding.imageViewClock.setColorFilter(primaryColor, android.graphics.PorterDuff.Mode.SRC_IN);
        binding.textViewTaskName.setText(task.getTaskName());
        binding.textViewPointsValue.setText(GeneralUtil.getPointsValueString(task.getPointsValue()));
        binding.textViewAlarmTime.setText(TimeUtil.dateToAlarmTimeString(alarm.getDate()));
        binding.textViewRecurring.setText(TimeUtil.getRecurringText(alarm));

        if (user.isParent() || user.isSolo()) {
            binding.imageButtonEdit.setVisibility(View.VISIBLE);
            binding.imageButtonEdit.setOnClickListener(v -> {
                // Go to edit task dialog fragment.
                TaskEditFragment taskEditFragment = TaskEditFragment.newInstance(task);
                taskEditFragment.show(fragmentActivity.getSupportFragmentManager(), "fragment_task_edit");
                dismiss();
            });
        }
        else {
            binding.imageButtonEdit.setVisibility(View.GONE);
        }

        if (user.isParent()) {
            binding.textViewAssignedToText.setVisibility(View.VISIBLE);
            binding.recyclerViewAssignedChild.setVisibility(View.VISIBLE);
            List<TaskifyUser> children = (List<TaskifyUser>) (List<?>) task.getUsers();
            AssignedChildAdapter assignedChildAdapter = new AssignedChildAdapter(fragmentActivity, children);
            binding.recyclerViewAssignedChild.setAdapter(assignedChildAdapter);
            binding.recyclerViewAssignedChild.setLayoutManager(new LinearLayoutManager(fragmentActivity));
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
            fragmentActivity = (FragmentActivity)context;
        }
    }
}
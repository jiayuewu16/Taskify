package com.example.taskify.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.adapters.AssignChildAdapter;
import com.example.taskify.databinding.FragmentTaskCreateBinding;
import com.example.taskify.models.Alarm;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TaskEditFragment extends DialogFragment {

    public final static String TAG = "TaskEditFragment";
    private FragmentTaskCreateBinding binding;
    private FragmentActivity activity;
    private Task task;

    // Required empty public constructor
    public TaskEditFragment() {}

    public static TaskEditFragment newInstance(Task task) {
        TaskEditFragment fragment = new TaskEditFragment();
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
        binding = FragmentTaskCreateBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<TaskifyUser> children = ((MainActivity)activity).associatedUsers;
        AssignChildAdapter assignChildAdapter = new AssignChildAdapter(activity, children);
        binding.recyclerViewAssignChild.setAdapter(assignChildAdapter);
        binding.recyclerViewAssignChild.setLayoutManager(new LinearLayoutManager(activity));

        setCurrentTaskValues(assignChildAdapter);

        binding.checkBoxSetRecurringTrue.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.layoutCheckBoxSetRecurringWeekdays.setVisibility(isChecked? View.VISIBLE : View.GONE));

        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonConfirm.setOnClickListener(v -> {
            String taskName = binding.editTextTaskName.getText().toString();
            if (taskName.isEmpty()) {
                Toast.makeText(activity, getString(R.string.error_empty_task_name_message), Toast.LENGTH_SHORT).show();
                return;
            }
            int pointsValue;
            try {
                pointsValue = Integer.parseInt(binding.editTextPoints.getText().toString());
                if (pointsValue < 0) throw new IllegalArgumentException();
            }
            catch (NumberFormatException ne) {
                Toast.makeText(activity, getString(R.string.error_empty_points_message), Toast.LENGTH_SHORT).show();
                return;
            }
            catch (IllegalArgumentException ie) {
                Toast.makeText(activity, getString(R.string.error_negative_points_message), Toast.LENGTH_SHORT).show();
                return;
            }

            List<ParseUser> selectedChildren = assignChildAdapter.getSelectedChildren();
            if (selectedChildren.isEmpty()) {
                Toast.makeText(activity, getString(R.string.error_empty_child_message), Toast.LENGTH_SHORT).show();
                return;
            }
            Alarm alarm;
            Date date = new Date();
            date.setHours(binding.timePicker.getHour());
            date.setMinutes(binding.timePicker.getMinute());
            boolean recurring = binding.checkBoxSetRecurringTrue.isChecked();
            if (recurring) {
                List<Boolean> recurringWeekdays = Arrays.asList(
                        binding.checkBoxSetRecurringSun.isChecked(),
                        binding.checkBoxSetRecurringMon.isChecked(),
                        binding.checkBoxSetRecurringTue.isChecked(),
                        binding.checkBoxSetRecurringWed.isChecked(),
                        binding.checkBoxSetRecurringThu.isChecked(),
                        binding.checkBoxSetRecurringFri.isChecked(),
                        binding.checkBoxSetRecurringSat.isChecked()
                );
                alarm = new Alarm(date, true, recurringWeekdays);
            }
            else {
                alarm = new Alarm(date, false);
            }
            Alarm oldAlarm = task.getAlarm();
            oldAlarm.deleteInBackground();
            ParseUtil.save(alarm, activity, TAG, null, null);

            task.setTaskName(taskName);
            task.setPointsValue(pointsValue);
            task.setAlarm(alarm);
            task.setUsers(selectedChildren);
            ParseUtil.save(task, activity, TAG, getString(R.string.success_save_task_message), getString(R.string.error_save_task_message));

            Objects.requireNonNull(NavHostFragment.findNavController(this).getCurrentBackStackEntry()).getSavedStateHandle().set("task", task);
            dismiss();
        });
    }

    private void setCurrentTaskValues(AssignChildAdapter adapter) {
        binding.editTextTaskName.setText(task.getTaskName());
        binding.editTextPoints.setText(String.valueOf(task.getPointsValue()));
        Alarm alarm = task.getAlarm();
        binding.timePicker.setHour(alarm.getDate().getHours());
        binding.timePicker.setMinute(alarm.getDate().getMinutes());

        List<Boolean> recurringWeekdays = alarm.getRecurringWeekdays();
        binding.checkBoxSetRecurringTrue.setChecked(alarm.isRecurring());
        if (binding.checkBoxSetRecurringTrue.isChecked()) {
            binding.layoutCheckBoxSetRecurringWeekdays.setVisibility(View.VISIBLE);
        }
        else {
            binding.layoutCheckBoxSetRecurringWeekdays.setVisibility(View.GONE);
        }
        binding.checkBoxSetRecurringSun.setChecked(recurringWeekdays.get(0));
        binding.checkBoxSetRecurringMon.setChecked(recurringWeekdays.get(1));
        binding.checkBoxSetRecurringTue.setChecked(recurringWeekdays.get(2));
        binding.checkBoxSetRecurringWed.setChecked(recurringWeekdays.get(3));
        binding.checkBoxSetRecurringThu.setChecked(recurringWeekdays.get(4));
        binding.checkBoxSetRecurringFri.setChecked(recurringWeekdays.get(5));
        binding.checkBoxSetRecurringSat.setChecked(recurringWeekdays.get(6));

        adapter.setSelectedChildren(task.getUsers());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            activity = (FragmentActivity)context;
        }
    }

}
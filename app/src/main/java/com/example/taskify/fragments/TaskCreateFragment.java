package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.taskify.R;
import com.example.taskify.databinding.FragmentTaskCreateBinding;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;

public class TaskCreateFragment extends DialogFragment {

    public final static String TAG = "TaskCreateFragment";
    private FragmentTaskCreateBinding binding;
    protected FragmentActivity activity;

    public interface TaskCreateDialogListener {
        void onFinishTaskCreateDialog(Task task);
    }

    // Required empty public constructor
    public TaskCreateFragment() {}

    public static TaskCreateFragment newInstance() {
        TaskCreateFragment fragment = new TaskCreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaskCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.checkBoxSetRecurringTrue.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.layoutCheckBoxSetRecurringWeekdays.setVisibility(isChecked? View.VISIBLE : View.GONE));

        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonConfirm.setOnClickListener(v -> {
            String taskName = binding.editTextTaskName.getText().toString();
            if (taskName.isEmpty()) {
                Toast.makeText(activity, activity.getResources().getString(R.string.error_empty_task_name_message), Toast.LENGTH_SHORT).show();
                return;
            }
            int pointsValue;
            try {
                pointsValue = Integer.parseInt(binding.editTextPoints.getText().toString());
                if (pointsValue < 0) throw new IllegalArgumentException();
            }
            catch (NumberFormatException ne) {
                Toast.makeText(activity, activity.getResources().getString(R.string.error_empty_points_message), Toast.LENGTH_SHORT).show();
                return;
            }
            Date date = new Date();
            date.setHours(binding.timePicker.getHour());
            date.setMinutes(binding.timePicker.getMinute());

            TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
            Task task = new Task(taskName, pointsValue, date, user);
            ParseUtil.save(task, activity, TAG,
                    activity.getResources().getString(R.string.success_save_task_message),
                    activity.getResources().getString(R.string.error_save_task_message));

            //TaskCreateDialogListener listener = (TaskCreateDialogListener) activity;
            //listener.onFinishTaskCreateDialog(task);
            Intent intent = new Intent();
            intent.putExtra("task", Parcels.wrap(task));
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            activity = (FragmentActivity)context;
        }
    }

}
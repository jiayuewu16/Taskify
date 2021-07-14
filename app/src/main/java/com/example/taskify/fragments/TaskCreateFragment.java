package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.taskify.databinding.FragmentTaskCreateBinding;
import com.example.taskify.models.Task;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

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

        binding.checkBoxSetRecurringTrue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.layoutCheckBoxSetRecurringWeekdays.setVisibility(isChecked? View.VISIBLE : View.GONE);
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = binding.editTextTaskName.getText().toString();
                if (taskName.isEmpty()) {
                    Toast.makeText(activity, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pointsValue;
                try {
                    pointsValue = Integer.parseInt(binding.editTextPoints.getText().toString());
                    if (pointsValue < 0) throw new IllegalArgumentException();
                }
                catch (NumberFormatException ne) {
                    Toast.makeText(activity, "Points cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser user = ParseUser.getCurrentUser();
                Task task = new Task(taskName, pointsValue, user);
                saveTask(task);
                //TaskCreateDialogListener listener = (TaskCreateDialogListener) activity;
                //listener.onFinishTaskCreateDialog(task);
                Intent intent = new Intent();
                intent.putExtra("task", Parcels.wrap(task));
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });
    }

    private void saveTask(Task task) {
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving task", e);
                    Toast.makeText(activity, "Error while saving task!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Task save was successful!");
                Toast.makeText(activity, "Task saved successfully!", Toast.LENGTH_SHORT).show();
            }
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
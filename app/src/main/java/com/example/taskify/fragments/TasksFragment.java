package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.adapters.TaskAdapter;
import com.example.taskify.databinding.FragmentTasksBinding;
import com.example.taskify.models.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private final static String TAG = "TasksFragment";
    private final static int KEY_TASK_CREATE_FRAGMENT = 1;
    private FragmentTasksBinding binding;
    private TaskAdapter adapter;
    private List<Task> tasks;

    // Required empty public constructor
    public TasksFragment() {}

    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasks = new ArrayList<>();
        adapter = new TaskAdapter(getActivity(), tasks);

        binding.recyclerViewTasksStream.setAdapter(adapter);
        binding.recyclerViewTasksStream.setLayoutManager(new LinearLayoutManager(getActivity()));

        queryTasks();

        binding.floatingActionButtonCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCreateFragment taskCreateFragment = TaskCreateFragment.newInstance();
                taskCreateFragment.setTargetFragment(TasksFragment.this, KEY_TASK_CREATE_FRAGMENT);
                taskCreateFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "fragment_task_create");
            }
        });
    }

    private void queryTasks() {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query = query.include(Task.KEY_USER);
        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> queryTasks, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                }
                else {
                    for (Task task : queryTasks) {
                        Log.i(TAG, "Task Name: " + task.getTaskName() + ", assigned to: " + task.getUser().getUsername());
                    }
                    tasks.addAll(queryTasks);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_TASK_CREATE_FRAGMENT && resultCode == Activity.RESULT_OK) {
            Task task = Parcels.unwrap(data.getExtras().getParcelable("task"));
            tasks.add(task);
            adapter.notifyDataSetChanged();
            binding.recyclerViewTasksStream.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }
}
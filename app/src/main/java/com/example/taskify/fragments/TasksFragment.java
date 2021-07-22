package com.example.taskify.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.adapters.TaskAdapter;
import com.example.taskify.databinding.FragmentTasksBinding;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.network.AlarmBroadcastReceiver;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksFragment extends Fragment {

    private final static String TAG = "TasksFragment";
    private final static int KEY_TASK_CREATE_FRAGMENT = 1;
    private FragmentTasksBinding binding;
    private TaskAdapter adapter;
    private List<Task> tasks;

    // Required empty public constructor
    public TasksFragment() {}

    public static TasksFragment newInstance() {
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

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        ParseUtil.queryTasks(getContext(), user, tasks, adapter);

        if (!user.isParent()) {
            binding.floatingActionButtonCreateTask.setVisibility(View.GONE);
        }
        else {
            binding.floatingActionButtonCreateTask.setOnClickListener(v -> {
                TaskCreateFragment taskCreateFragment = TaskCreateFragment.newInstance();
                taskCreateFragment.setTargetFragment(TasksFragment.this, KEY_TASK_CREATE_FRAGMENT);
                taskCreateFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "fragment_task_create");
            });
        }

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshLayout.setRefreshing(true);
                tasks.clear();
                ParseUtil.queryTasks(getContext(), user, tasks, adapter);
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_TASK_CREATE_FRAGMENT && resultCode == Activity.RESULT_OK) {
            Task task = Parcels.unwrap(data.getExtras().getParcelable("task"));

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent receiverIntent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
            receiverIntent.putExtra(Task.KEY_TASK_NAME, task.getTaskName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), GeneralUtil.randomInt(), receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, task.getAlarmTime().getTime(), pendingIntent);

            tasks.add(task);
            Collections.sort(tasks);
            adapter.notifyDataSetChanged();
            binding.recyclerViewTasksStream.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }
}
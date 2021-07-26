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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.adapters.TaskAdapter;
import com.example.taskify.databinding.FragmentStreamBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

public class TasksFragment extends Fragment {

    private final static String TAG = "TasksFragment";
    private final static int KEY_TASK_CREATE_FRAGMENT = 1;
    private FragmentStreamBinding binding;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStreamBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasks = MainActivity.tasks;
        adapter = new TaskAdapter(getActivity(), tasks);

        binding.recyclerViewStream.setAdapter(adapter);
        binding.recyclerViewStream.setLayoutManager(new LinearLayoutManager(getActivity()));

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        tasks.clear();
        ParseUtil.queryTasks(getContext(), user, tasks, adapter);

        if (!user.isParent()) {
            binding.floatingActionButtonCreate.setVisibility(View.GONE);
        }
        else {
            binding.floatingActionButtonCreate.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_tasks_to_taskCreateFragment);
            });
        }

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            tasks.clear();
            ParseUtil.queryTasks(getContext(), user, tasks, adapter);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        NavController navController = NavHostFragment.findNavController(this);
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData<Task> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("task");
        liveData.observe(getViewLifecycleOwner(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task == null) {
                    Log.i(TAG, "No task returned");
                    return;
                }
                tasks.add(task);
                Collections.sort(tasks);
                adapter.notifyDataSetChanged();
                binding.recyclerViewStream.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_TASK_CREATE_FRAGMENT && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.i(TAG, "No task returned");
                return;
            }
            Task task = Parcels.unwrap(data.getExtras().getParcelable("task"));
            tasks.add(task);
            Collections.sort(tasks);
            adapter.notifyDataSetChanged();
            binding.recyclerViewStream.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }
}
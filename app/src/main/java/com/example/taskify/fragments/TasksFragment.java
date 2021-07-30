package com.example.taskify.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.adapters.TaskAdapter;
import com.example.taskify.databinding.FragmentStreamBinding;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.design.VerticalSpaceItemDecoration;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TasksFragment extends Fragment {

    private final static String TAG = "TasksFragment";
    private FragmentStreamBinding binding;
    private TaskAdapter adapter;
    private List<Task> tasks;

    // Required empty public constructor
    public TasksFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStreamBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasks = ((MainActivity)requireActivity()).tasks;
        adapter = ((MainActivity)requireActivity()).taskAdapter;
        adapter.setFragmentStreamBinding(binding);
        //adapter = new TaskAdapter(getActivity(), binding, tasks);

        binding.recyclerViewStream.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewStream.setLayoutManager(linearLayoutManager);
        VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration();
        binding.recyclerViewStream.addItemDecoration(dividerItemDecoration);

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        if (!user.isParent()) {
            binding.floatingActionButtonCreate.setVisibility(View.GONE);
        }
        else {
            binding.floatingActionButtonCreate.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_tasks_to_taskCreateFragment));
        }

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            tasks.clear();
            ParseUtil.queryTasks(getContext(), user, tasks, adapter);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        NavController navController = NavHostFragment.findNavController(this);

        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData<Task> liveData = Objects.requireNonNull(navController.getCurrentBackStackEntry())
                .getSavedStateHandle()
                .getLiveData("task");
        liveData.observe(getViewLifecycleOwner(), task -> {
            if (task == null) {
                Log.i(TAG, "No task returned");
                return;
            }
            if (!tasks.contains(task)) {
                tasks.add(task);
            }
            Collections.sort(tasks);
            adapter.notifyDataSetChanged();
            binding.recyclerViewStream.smoothScrollToPosition(adapter.getItemCount()-1);
        });
    }
}
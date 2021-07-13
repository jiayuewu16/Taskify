package com.example.taskify.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.activities.TaskCreateActivity;
import com.example.taskify.adapters.TaskAdapter;
import com.example.taskify.databinding.FragmentTasksBinding;
import com.example.taskify.models.Task;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

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

        binding.floatingActionButtonCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
                startActivity(intent);
            }
        });
    }
}
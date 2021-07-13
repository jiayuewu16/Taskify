package com.example.taskify.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.databinding.ItemTaskBinding;
import com.example.taskify.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        ItemTaskBinding binding;

        public ViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(Task task) {
            binding.textViewTaskName.setText(task.getTaskName());
            String pointsValueString = String.valueOf(task.getPointsValue()) + " " + context.getResources().getString(R.string.points_value_suffix_text);
            binding.textViewPointsValue.setText(pointsValueString);
        }

        @Override
        public void onClick(View v) {
            //go to details screen
        }

        @Override
        public boolean onLongClick(View v) {
            //mark task complete
            return false;
        }
    }
}

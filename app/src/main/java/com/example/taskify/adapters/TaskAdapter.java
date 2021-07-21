package com.example.taskify.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.databinding.ItemTaskBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final static String TAG = "TaskAdapter";

    private final Context context;
    private final List<Task> tasks;

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
            itemView.setOnLongClickListener(this);
        }

        public void bind(Task task) {
            binding.textViewTaskName.setText(task.getTaskName());
            String pointsValueString = task.getPointsValue() + " " + context.getResources().getString(R.string.points_value_suffix_text);
            binding.textViewPointsValue.setText(pointsValueString);
            binding.textViewAlarmTime.setText(task.getAlarmTimeString());
        }

        @Override
        public void onClick(View v) {
            //go to details screen
            Log.i(TAG, "onClick");
        }

        @Override
        public boolean onLongClick(View v) {
            // Mark task complete.
            Log.i(TAG, "onLongClick");
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return false;
            }
            Task task = tasks.get(position);
            int pointsValue = task.getPointsValue();
            List<ParseUser> users = task.getUsers();
            TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
            if (users.size() == 1 || user.isParent()) {
                task.deleteInBackground(e -> {
                    if (e != null) {
                        Log.e(TAG, "Error while marking task complete", e);
                        Toast.makeText(context, context.getResources().getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    removeTaskFromList(position);
                    updatePoints(user, pointsValue);
                    Log.i(TAG, "Task completion was successful!");
                    if (user.isParent()) {
                        Toast.makeText(context, context.getString(R.string.success_parent_remove_task_message), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(context, String.format(context.getResources().getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"), Toast.LENGTH_SHORT).show();
                });
            }
            else {
                users.remove(user);
                task.setUsers(users);
                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(context, context.getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, context.getString(R.string.error_remove_task_message));
                            return;
                        }
                        ParseUtil.save(task, context, TAG,
                                String.format(context.getResources().getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"),
                                context.getString(R.string.error_remove_task_message));
                        removeTaskFromList(position);
                        updatePoints(user, pointsValue);
                        Log.i(TAG, "Task completion was successful!");
                        if (user.isParent()) {
                            Toast.makeText(context, context.getString(R.string.success_parent_remove_task_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(context, String.format(context.getResources().getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            return true;
        }
    }

    private void removeTaskFromList(int position) {
        tasks.remove(position);
        notifyDataSetChanged();
    }

    private void updatePoints(TaskifyUser user, int pointsValue) {
        int prevPointsValue = user.getPointsTotal();
        user.addPointsValue(pointsValue);
        ParseUtil.save(user, context, TAG, null, context.getResources().getString(R.string.error_save_user_points));
        for (Reward reward : MainActivity.rewards) {
            if (reward.getPointsValue() > prevPointsValue && reward.getPointsValue() < prevPointsValue + pointsValue) {
                Toast.makeText(context, String.format("Congratulations! You've earned a reward: %s!", reward.getRewardName()), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.taskify.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.databinding.ItemTaskBinding;
import com.example.taskify.fragments.TaskCreateFragment;
import com.example.taskify.fragments.TaskDetailsFragment;
import com.example.taskify.fragments.TasksFragment;
import com.example.taskify.models.Alarm;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.TimeUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final static String TAG = "TaskAdapter";

    private final FragmentActivity fragmentActivity;
    private final List<Task> tasks;

    public TaskAdapter(FragmentActivity fragmentActivity, List<Task> tasks) {
        this.fragmentActivity = fragmentActivity;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(fragmentActivity));
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
            Alarm alarm = task.getAlarm();
            binding.textViewTaskName.setText(task.getTaskName());
            String pointsValueString = task.getPointsValue() + " " + fragmentActivity.getResources().getString(R.string.points_value_suffix_text);
            binding.textViewPointsValue.setText(pointsValueString);
            binding.textViewAlarmTime.setText(TimeUtil.dateToAlarmTimeString(alarm.getDate()));
            binding.textViewRecurring.setText(TimeUtil.getRecurringText(alarm));
        }

        @Override
        public void onClick(View v) {
            //go to details screen
            Log.i(TAG, "onClick");

            TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance(tasks.get(getAdapterPosition()));
            taskDetailsFragment.show(fragmentActivity.getSupportFragmentManager(), "fragment_task_details");
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
                        Toast.makeText(fragmentActivity, fragmentActivity.getResources().getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    removeTaskFromList(position);
                    updatePoints(user, pointsValue);
                    Log.i(TAG, "Task completion was successful!");
                    if (user.isParent()) {
                        Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.success_parent_remove_task_message), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(fragmentActivity, String.format(fragmentActivity.getResources().getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"), Toast.LENGTH_SHORT).show();
                });
            }
            else {
                users.remove(user);
                task.setUsers(users);
                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, fragmentActivity.getString(R.string.error_remove_task_message));
                            return;
                        }
                        ParseUtil.save(task, fragmentActivity, TAG,
                                String.format(fragmentActivity.getResources().getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"),
                                fragmentActivity.getString(R.string.error_remove_task_message));
                        removeTaskFromList(position);
                        updatePoints(user, pointsValue);
                        Log.i(TAG, "Task completion was successful!");
                        if (user.isParent()) {
                            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.success_parent_remove_task_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(fragmentActivity, String.format(fragmentActivity.getResources().getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"), Toast.LENGTH_SHORT).show();
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
        ParseUtil.save(user, fragmentActivity, TAG, null, fragmentActivity.getResources().getString(R.string.error_save_user_points));
        for (Reward reward : MainActivity.rewards) {
            if (reward.getPointsValue() > prevPointsValue && reward.getPointsValue() < prevPointsValue + pointsValue) {
                Toast.makeText(fragmentActivity, String.format("Congratulations! You've earned a reward: %s!", reward.getRewardName()), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

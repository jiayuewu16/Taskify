package com.example.taskify.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.databinding.FragmentStreamBinding;
import com.example.taskify.databinding.ItemTaskBinding;
import com.example.taskify.fragments.TaskDetailsFragment;
import com.example.taskify.models.Alarm;
import com.example.taskify.models.Reward;
import com.example.taskify.models.Task;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ColorUtil;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.TimeUtil;
import com.parse.ParseUser;
import java.util.List;

import nl.dionsegijn.konfetti.models.Shape;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final static String TAG = "TaskAdapter";

    private final FragmentActivity fragmentActivity;
    private final List<Task> tasks;
    private FragmentStreamBinding fragmentStreamBinding;

    public TaskAdapter(FragmentActivity fragmentActivity, FragmentStreamBinding fragmentStreamBinding, List<Task> tasks) {
        this.fragmentActivity = fragmentActivity;
        this.fragmentStreamBinding = fragmentStreamBinding;
        this.tasks = tasks;
    }

    public void setFragmentStreamBinding(FragmentStreamBinding fragmentStreamBinding) {
        this.fragmentStreamBinding = fragmentStreamBinding;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(fragmentActivity), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        Drawable backgroundDrawable;
        int foregroundColor;
        if (position % 2 == 0) {
            backgroundDrawable = AppCompatResources.getDrawable(fragmentActivity, R.drawable.background_rounded_primary);
            foregroundColor = ColorUtil.getBackgroundColor(fragmentActivity);
        } else {
            backgroundDrawable = AppCompatResources.getDrawable(fragmentActivity, R.drawable.background_rounded_secondary);
            foregroundColor = ColorUtil.getTextColor(fragmentActivity);
        }
        holder.bind(task, backgroundDrawable, foregroundColor);
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

        public void bind(Task task, Drawable backgroundDrawable, int foregroundColor) {
            Alarm alarm = task.getAlarm();
            binding.getRoot().setBackground(backgroundDrawable);
            ColorUtil.setImageViewColor(binding.imageViewClock, foregroundColor);
            binding.textViewTaskName.setText(task.getTaskName());
            binding.textViewTaskName.setTextColor(foregroundColor);
            binding.textViewPointsValue.setText(GeneralUtil.getPointsValueString(task.getPointsValue()));
            binding.textViewPointsValue.setTextColor(foregroundColor);
            binding.textViewAlarmTime.setText(TimeUtil.dateToAlarmTimeString(alarm.getDate()));
            binding.textViewAlarmTime.setTextColor(foregroundColor);
            binding.textViewRecurring.setText(TimeUtil.getRecurringText(alarm));
            binding.textViewRecurring.setTextColor(foregroundColor);
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
            Alarm alarm = task.getAlarm();
            int pointsValue = task.getPointsValue();
            TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();

            if (alarm.isRecurring() && !user.isParent()) {
                // Do not delete the task. Only update points.
                completeTask(user, pointsValue);
                return true;
            }

            List<ParseUser> users = task.getUsers();
            if (users.size() == 1 || user.isParent()) {
                alarm.deleteInBackground(e -> {
                    if (e != null) {
                        Log.e(TAG, "Error while marking task complete", e);
                        Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                    }});
                task.deleteInBackground(e -> {
                    if (e != null) {
                        Log.e(TAG, "Error while marking task complete", e);
                        Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    removeTaskFromList(position);
                    completeTask(user, pointsValue);
                });
            }
            else {
                users.remove(user);
                task.setUsers(users);
                task.saveInBackground(e -> {
                    if (e != null) {
                        Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.error_remove_task_message), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, fragmentActivity.getString(R.string.error_remove_task_message));
                        return;
                    }
                    ParseUtil.save(task, fragmentActivity, TAG,
                            String.format(fragmentActivity.getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"),
                            fragmentActivity.getString(R.string.error_remove_task_message));
                    removeTaskFromList(position);
                    completeTask(user, pointsValue);
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
        if (user.isParent()) {
            return;
        }
        int prevPointsValue = user.getPointsTotal();
        user.addPointsValue(pointsValue);
        ParseUtil.save(user, fragmentActivity, TAG, null, fragmentActivity.getString(R.string.error_save_user_points));
        for (Reward reward : ((MainActivity)fragmentActivity).rewards) {
            if (reward.getPointsValue() > prevPointsValue && reward.getPointsValue() < prevPointsValue + pointsValue) {
                Toast.makeText(fragmentActivity, String.format("Congratulations! You've earned a reward: %s!", reward.getRewardName()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void completeTask(TaskifyUser user, int pointsValue) {
        Log.i(TAG, "Task completion was successful!");
        if (user.isParent()) {
            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.success_parent_remove_task_message), Toast.LENGTH_SHORT).show();
        }
        else {
            updatePoints(user, pointsValue);
            Toast.makeText(fragmentActivity, String.format(fragmentActivity.getString(R.string.success_remove_task_message), pointsValue, pointsValue == 1 ? "point" : "points"), Toast.LENGTH_SHORT).show();
            fragmentStreamBinding.konfettiView.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(2000L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .setPosition(-50f, fragmentStreamBinding.konfettiView.getWidth() + 50f, -50f, -50f)
                            .streamFor(300, 2000L);
        }
    }
}

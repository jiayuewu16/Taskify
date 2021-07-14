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
import com.example.taskify.databinding.ItemTaskBinding;
import com.example.taskify.models.Task;
import com.example.taskify.util.ParseUserUtil;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final static String TAG = "TaskAdapter";

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
            itemView.setOnLongClickListener(this);
        }

        public void bind(Task task) {
            binding.textViewTaskName.setText(task.getTaskName());
            String pointsValueString = String.valueOf(task.getPointsValue()) + " " + context.getResources().getString(R.string.points_value_suffix_text);
            binding.textViewPointsValue.setText(pointsValueString);
            task.getAlarmTime().toString();
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
            task.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while marking task complete", e);
                        Toast.makeText(context, "Error while marking task complete!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tasks.remove(position);
                    notifyDataSetChanged();
                    Log.i(TAG, "Task completion was successful!");
                    Toast.makeText(context, String.format("Task complete! You earned %d %s!", pointsValue, pointsValue == 1? "point" : "points"), Toast.LENGTH_SHORT).show();
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseUserUtil.addPointsValue(user, pointsValue);
                    try {
                        ParseUserUtil.saveUser(user);
                    }
                    catch (ParseException pe) {
                        Toast.makeText(context, "Error while saving user points.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
    }
}

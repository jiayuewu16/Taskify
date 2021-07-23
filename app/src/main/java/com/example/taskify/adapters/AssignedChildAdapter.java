package com.example.taskify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.databinding.ItemAssignedChildBinding;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseException;
import java.util.List;

public class AssignedChildAdapter extends RecyclerView.Adapter<AssignedChildAdapter.ViewHolder> {

    private final Context context;
    private final List<TaskifyUser> users;

    public AssignedChildAdapter(Context context, List<TaskifyUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAssignedChildBinding binding = ItemAssignedChildBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskifyUser user = null;
        try {
            user = (TaskifyUser) users.get(position).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemAssignedChildBinding binding;

        public ViewHolder(@NonNull ItemAssignedChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TaskifyUser user) {
            ParseUtil.setPhoto(binding.imageViewProfilePhoto, user, AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_24));
            binding.textViewFullUsername.setText(String.format(context.getString(R.string.display_full_username_format), user.getFirstName(), user.getLastName(), user.getUsername()));
        }
    }
}

package com.example.taskify.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.databinding.ItemUserBinding;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final static String TAG = "UserAdapter";

    private final Context context;
    private final List<TaskifyUser> users;

    public UserAdapter(Context context, List<TaskifyUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskifyUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        ItemUserBinding binding;

        public ViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(TaskifyUser user) {
            ParseUtil.setPhoto(binding.imageViewProfilePhoto, user, AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_24));
            binding.textViewFullName.setText(String.format(context.getString(R.string.display_full_name_format), user.getFirstName(), user.getLastName()));
            binding.textViewUsername.setText(String.format(context.getString(R.string.display_username_format), user.getUsername()));
        }

        @Override
        public void onClick(View v) {
            // Go to details screen
            Log.i(TAG, "onClick");
        }

        @Override
        public boolean onLongClick(View v) {
            // Delete child.
            Log.i(TAG, "onLongClick");
            return true;
        }
    }
}

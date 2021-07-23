package com.example.taskify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.databinding.ItemAssignChildBinding;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AssignChildAdapter extends RecyclerView.Adapter<AssignChildAdapter.ViewHolder> {

    private final Context context;
    private final List<TaskifyUser> users;
    private final List<ParseUser> selectedChildren;

    public AssignChildAdapter(Context context, List<TaskifyUser> users) {
        this.context = context;
        this.users = users;
        selectedChildren = new ArrayList<>();
    }

    public List<ParseUser> getSelectedChildren() {
        return selectedChildren;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAssignChildBinding binding = ItemAssignChildBinding.inflate(LayoutInflater.from(context));
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemAssignChildBinding binding;

        public ViewHolder(@NonNull ItemAssignChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(TaskifyUser user) {
            ParseUtil.setPhoto(binding.imageViewProfilePhoto, user, AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_24));
            binding.textViewFullUsername.setText(String.format(context.getString(R.string.display_full_username_format), user.getFirstName(), user.getLastName(), user.getUsername()));
            binding.checkBoxAssignChild.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedChildren.add(user);
                } else {
                    selectedChildren.remove(user);
                }
            });
        }

        @Override
        public void onClick(View v) {
            binding.checkBoxAssignChild.setChecked(!binding.checkBoxAssignChild.isChecked());
        }
    }
}

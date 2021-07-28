package com.example.taskify.adapters;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskify.R;
import com.example.taskify.databinding.FragmentStreamBinding;
import com.example.taskify.databinding.ItemRewardBinding;
import com.example.taskify.fragments.RewardDetailsFragment;
import com.example.taskify.models.Reward;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ColorUtil;
import com.example.taskify.util.GeneralUtil;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private final static String TAG = "RewardAdapter";

    private final FragmentActivity fragmentActivity;
    private final List<Reward> rewards;
    private FragmentStreamBinding fragmentStreamBinding;

    public RewardAdapter(FragmentActivity fragmentActivity, FragmentStreamBinding fragmentStreamBinding, List<Reward> rewards) {
        this.fragmentActivity = fragmentActivity;
        this.fragmentStreamBinding = fragmentStreamBinding;
        this.rewards = rewards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRewardBinding binding = ItemRewardBinding.inflate(LayoutInflater.from(fragmentActivity), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        Drawable backgroundDrawable;
        int foregroundColor;
        if (position % 2 == 0) {
            backgroundDrawable = AppCompatResources.getDrawable(fragmentActivity, R.drawable.background_rounded_primary);
            foregroundColor = ColorUtil.getBackgroundColor(fragmentActivity);
        } else {
            backgroundDrawable = AppCompatResources.getDrawable(fragmentActivity, R.drawable.background_rounded_secondary);
            foregroundColor = ColorUtil.getTextColor(fragmentActivity);
        }
        holder.bind(reward, backgroundDrawable, foregroundColor);
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        ItemRewardBinding binding;

        public ViewHolder(@NonNull ItemRewardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Reward reward, Drawable backgroundDrawable, int foregroundColor) {
            binding.getRoot().setBackground(backgroundDrawable);
            binding.textViewRewardName.setText(reward.getRewardName());
            binding.textViewRewardName.setTextColor(foregroundColor);
            binding.textViewPointsValue.setText(GeneralUtil.getPointsValueString(reward.getPointsValue()));
            binding.textViewPointsValue.setTextColor(foregroundColor);
            TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
            if (user.isParent()) {
                binding.checkBoxEarnedReward.setVisibility(View.GONE);
            }
            else {
                binding.checkBoxEarnedReward.setChecked(user.getPointsTotal() >= reward.getPointsValue());
                ColorUtil.setCheckBoxColor(binding.checkBoxEarnedReward, foregroundColor);
            }
            ParseFile rewardPhoto = reward.getRewardPhoto();
            if (rewardPhoto == null) {
                binding.imageViewRewardPhoto.setImageResource(R.drawable.ic_baseline_star_24);
            }
            else {
                rewardPhoto.getDataInBackground((data, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Image load unsuccessful.", e);
                        binding.imageViewRewardPhoto.setImageResource(R.drawable.ic_baseline_star_24);
                    } else {
                        Bitmap rewardImageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        binding.imageViewRewardPhoto.setImageBitmap(rewardImageBitmap);
                    }
                });

            }
        }

        @Override
        public void onClick(View v) {
            //go to details screen
            Log.i(TAG, "onClick");
            RewardDetailsFragment rewardDetailsFragment = RewardDetailsFragment.newInstance(rewards.get(getAdapterPosition()));
            rewardDetailsFragment.show(fragmentActivity.getSupportFragmentManager(), "fragment_reward_details");
        }

        @Override
        public boolean onLongClick(View v) {
            // Remove reward.
            TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
            if (!user.isParent()) {
                return true;
            }
            Log.i(TAG, "onLongClick");
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return false;
            }
            Reward reward = rewards.get(position);
            reward.deleteInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error while removing reward.", e);
                    Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.error_remove_reward_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                rewards.remove(position);
                notifyDataSetChanged();
                Log.i(TAG, "Reward removed successfully.");
                Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.success_remove_reward_message), Toast.LENGTH_SHORT).show();
            });
            return true;
        }
    }
}

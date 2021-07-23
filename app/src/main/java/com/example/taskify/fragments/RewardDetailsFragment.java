package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskify.R;
import com.example.taskify.adapters.AssignedChildAdapter;
import com.example.taskify.databinding.FragmentRewardDetailsBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.models.TaskifyUser;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class RewardDetailsFragment extends DialogFragment {

    private final static String TAG = "RewardDetailsFragment";
    private FragmentRewardDetailsBinding binding;
    private Reward reward;
    private Activity activity;

    // Required empty public constructor.
    public RewardDetailsFragment() {}

    public static RewardDetailsFragment newInstance(Reward reward) {
        RewardDetailsFragment fragment = new RewardDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setReward(reward);
        return fragment;
    }

    private void setReward(Reward reward) {
        this.reward = reward;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRewardDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();

        binding.textViewRewardName.setText(reward.getRewardName());
        String pointsValueString = reward.getPointsValue() + " " + activity.getResources().getString(R.string.points_value_suffix_text);
        binding.textViewPointsValue.setText(pointsValueString);
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

        if (user.isParent()) {
            binding.textViewAssignedToText.setVisibility(View.VISIBLE);
            binding.recyclerViewAssignedChild.setVisibility(View.VISIBLE);
            binding.textViewPointsProgress.setVisibility(View.GONE);

            List<TaskifyUser> children = (List<TaskifyUser>) (List<?>) reward.getUsers();
            AssignedChildAdapter assignedChildAdapter = new AssignedChildAdapter(activity, children);
            binding.recyclerViewAssignedChild.setAdapter(assignedChildAdapter);
            binding.recyclerViewAssignedChild.setLayoutManager(new LinearLayoutManager(activity));
        }
        else {
            binding.textViewAssignedToText.setVisibility(View.GONE);
            binding.recyclerViewAssignedChild.setVisibility(View.GONE);
            binding.textViewPointsProgress.setVisibility(View.VISIBLE);

            int pointsLeft = reward.getPointsValue() - user.getPointsTotal();
            if (pointsLeft <= 0) {
                binding.textViewPointsProgress.setText(String.format("Congratulations! You earned '%s'!", reward.getRewardName()));
            }
            else if (pointsLeft == 1) {
                binding.textViewPointsProgress.setText("1 point to go!");
            }
            else {
                binding.textViewPointsProgress.setText(String.format("%d points to go!", pointsLeft));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            activity = (FragmentActivity)context;
        }
    }
}
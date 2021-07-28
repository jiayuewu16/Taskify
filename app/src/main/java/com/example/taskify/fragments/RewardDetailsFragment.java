package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taskify.R;
import com.example.taskify.adapters.AssignedChildAdapter;
import com.example.taskify.databinding.FragmentRewardDetailsBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ColorUtil;
import com.example.taskify.util.GeneralUtil;
import com.example.taskify.util.PhotoUtil;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class RewardDetailsFragment extends DialogFragment {

    private final static String TAG = "RewardDetailsFragment";
    private FragmentRewardDetailsBinding binding;
    private Reward reward;
    private Activity activity;
    private ShareDialog shareDialog;

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
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();
        int primaryColor = ColorUtil.getPrimaryColor(activity);
        List<TextView> list = Arrays.asList(binding.textViewRewardName, binding.textViewPointsValue, binding.textViewPointsProgress);
        ColorUtil.alternateTextViewColors(list, ColorUtil.getTextColor(activity), primaryColor);
        binding.textViewRewardName.setText(reward.getRewardName());
        binding.textViewPointsValue.setText(GeneralUtil.getPointsValueString(reward.getPointsValue()));
        ParseFile rewardPhoto = reward.getRewardPhoto();

        if (rewardPhoto == null) {
            binding.imageViewRewardPhoto.setImageResource(R.drawable.ic_baseline_star_24);
            setShareContent(AppCompatResources.getDrawable(activity, R.drawable.ic_taskify_logo_transparent));
        }
        else {
            rewardPhoto.getDataInBackground((data, e) -> {
                if (e != null) {
                    Log.e(TAG, "Image load unsuccessful.", e);
                    binding.imageViewRewardPhoto.setImageResource(R.drawable.ic_baseline_star_24);
                    setShareContent(AppCompatResources.getDrawable(activity, R.drawable.ic_taskify_logo_transparent));
                } else {
                    Bitmap rewardImageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    binding.imageViewRewardPhoto.setImageBitmap(rewardImageBitmap);
                    setShareContent(rewardImageBitmap);
                }
            });
        }

        if (user.isParent()) {
            binding.shareButtonFacebook.setVisibility(View.GONE);
            binding.textViewAssignedToText.setVisibility(View.VISIBLE);
            binding.recyclerViewAssignedChild.setVisibility(View.VISIBLE);
            binding.textViewPointsProgress.setVisibility(View.GONE);

            binding.textViewAssignedToText.setTextColor(ColorUtil.getTextColor(activity));
            List<TaskifyUser> children = (List<TaskifyUser>) (List<?>) reward.getUsers();
            AssignedChildAdapter assignedChildAdapter = new AssignedChildAdapter(activity, children);
            binding.recyclerViewAssignedChild.setAdapter(assignedChildAdapter);
            binding.recyclerViewAssignedChild.setLayoutManager(new LinearLayoutManager(activity));
        }
        else {
            binding.shareButtonFacebook.setVisibility(View.VISIBLE);
            binding.textViewAssignedToText.setVisibility(View.GONE);
            binding.recyclerViewAssignedChild.setVisibility(View.GONE);
            binding.textViewPointsProgress.setVisibility(View.VISIBLE);

            int pointsLeft = reward.getPointsValue() - user.getPointsTotal();
            if (pointsLeft <= 0) {
                binding.textViewPointsProgress.setText(getString(R.string.reward_details_progress_complete));
            }
            else if (pointsLeft == 1) {
                binding.textViewPointsProgress.setText(getString(R.string.reward_details_progress_1_point));
            }
            else {
                binding.textViewPointsProgress.setText(String.format(getString(R.string.reward_details_progress_plural_points), pointsLeft));
            }
        }

        if (user.isParent()) {
            binding.shareButtonFacebook.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            activity = (FragmentActivity)context;
        }
    }

    private void setShareContent(Drawable drawable) {
        Bitmap bitmap = PhotoUtil.getBitmapFromVectorDrawable(activity, drawable);
        setShareContent(bitmap);
    }

    private void setShareContent(Bitmap bitmap) {
        // Facebook publish tutorial: https://developers.facebook.com/docs/sharing/android/
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        binding.shareButtonFacebook.setShareContent(content);
    }
}
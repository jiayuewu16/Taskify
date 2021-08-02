package com.example.taskify.design;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskify.R;
import com.example.taskify.databinding.FragmentSplashDialogBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.util.GeneralUtil;
import com.parse.ParseFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import nl.dionsegijn.konfetti.models.Shape;

public class SplashDialogFragment extends DialogFragment {

    private static final String TAG = "SplashDialogFragment";
    private Reward reward;
    private FragmentSplashDialogBinding binding;

    // Required empty public constructor
    public SplashDialogFragment() {}

    public static SplashDialogFragment newInstance(Reward reward) {
        SplashDialogFragment fragment = new SplashDialogFragment();
        fragment.setReward(reward);
        return fragment;
    }

    private void setReward(Reward reward) {
        this.reward = reward;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashDialogBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .setPosition(binding.konfettiView.getWidth()/2f, binding.konfettiView.getHeight()/2f)
                .burst(100);

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

        binding.textViewEarnedText.setText(String.format(getString(R.string.reward_splash_earned_reward_format), reward.getRewardName()));

        List<Integer> buttonMessages = Arrays.asList(
                R.string.reward_splash_confirmation_button_0,
                R.string.reward_splash_confirmation_button_1,
                R.string.reward_splash_confirmation_button_2,
                R.string.reward_splash_confirmation_button_3,
                R.string.reward_splash_confirmation_button_4);
        binding.buttonDismiss.setText(getString(buttonMessages.get(GeneralUtil.randomInt(0, 4))));
        binding.buttonDismiss.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.konfettiView.stopGracefully();
    }
}
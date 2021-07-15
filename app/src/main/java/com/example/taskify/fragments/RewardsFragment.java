package com.example.taskify.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.adapters.RewardAdapter;
import com.example.taskify.databinding.FragmentRewardsBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.network.AlarmBroadcastReceiver;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class RewardsFragment extends Fragment {

    private final static String TAG = "RewardsFragment";
    private final static int KEY_REWARD_CREATE_FRAGMENT = 1;
    private FragmentRewardsBinding binding;
    private RewardAdapter adapter;
    private List<Reward> rewards;

    // Required empty public constructor
    public RewardsFragment() {}

    public static RewardsFragment newInstance(String param1, String param2) {
        RewardsFragment fragment = new RewardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRewardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rewards = new ArrayList<>();
        adapter = new RewardAdapter(getActivity(), rewards);

        binding.recyclerViewRewardsStream.setAdapter(adapter);
        binding.recyclerViewRewardsStream.setLayoutManager(new LinearLayoutManager(getActivity()));

        queryRewards();

        binding.floatingActionButtonCreateReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardCreateFragment rewardCreateFragment = RewardCreateFragment.newInstance();
                rewardCreateFragment.setTargetFragment(RewardsFragment.this, KEY_REWARD_CREATE_FRAGMENT);
                rewardCreateFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "fragment_reward_create");
            }
        });
    }

    private void queryRewards() {
        ParseQuery<Reward> query = ParseQuery.getQuery(Reward.class);
        query = query.include(Reward.KEY_USER);
        query.addAscendingOrder(Reward.KEY_POINTS_VALUE);
        query.findInBackground(new FindCallback<Reward>() {
            @Override
            public void done(List<Reward> queryRewards, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                }
                else {
                    for (Reward reward : queryRewards) {
                        Log.i(TAG, "Reward Name: " + reward.getRewardName() + ", assigned to: " + reward.getUser().getUsername());
                    }
                    rewards.addAll(queryRewards);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_REWARD_CREATE_FRAGMENT && resultCode == Activity.RESULT_OK) {
            Reward reward = Parcels.unwrap(data.getExtras().getParcelable("reward"));

            rewards.add(reward);
            Collections.sort(rewards);
            adapter.notifyDataSetChanged();
            binding.recyclerViewRewardsStream.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }
}
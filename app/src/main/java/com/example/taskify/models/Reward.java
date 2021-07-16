package com.example.taskify.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reward")
public class Reward extends ParseObject implements Comparable<Reward> {
    public final static String KEY_REWARD_NAME = "rewardName";
    public final static String KEY_REWARD_PHOTO = "rewardPhoto";
    public final static String KEY_POINTS_VALUE = "rewardPoints";
    public final static String KEY_USER = "user";

    // Required default constructor.
    public Reward() {}

    public Reward(String rewardName, int rewardPoints, ParseFile rewardPhoto, ParseUser user) {
        setRewardName(rewardName);
        if (rewardPhoto != null) {
            setRewardPhoto(rewardPhoto);
        }
        setPointsValue(rewardPoints);
        setUser(user);
    }

    public String getRewardName() {
        return getString(KEY_REWARD_NAME);
    }

    public ParseFile getRewardPhoto() {
        return getParseFile(KEY_REWARD_PHOTO);
    }

    public int getPointsValue() {
        return getInt(KEY_POINTS_VALUE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setRewardName(String rewardName) {
        put(KEY_REWARD_NAME, rewardName);
    }

    public void setRewardPhoto(ParseFile rewardPhoto) {
        put(KEY_REWARD_PHOTO, rewardPhoto);
    }

    public void setPointsValue(int rewardPoints) {
        put(KEY_POINTS_VALUE, rewardPoints);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    @Override
    public int compareTo(Reward o) {
        return this.getPointsValue() - o.getPointsValue();
    }
}

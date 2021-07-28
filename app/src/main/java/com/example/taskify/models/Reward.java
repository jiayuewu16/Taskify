package com.example.taskify.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Reward")
public class Reward extends ParseObject implements Comparable<Reward> {
    public final static String KEY_REWARD_NAME = "rewardName";
    public final static String KEY_REWARD_PHOTO = "rewardPhoto";
    public final static String KEY_POINTS_VALUE = "rewardPoints";
    public final static String KEY_USERS = "users";

    // Required default constructor.
    public Reward() {}

    public Reward(String rewardName, int rewardPoints, ParseFile rewardPhoto, List<ParseUser> users) {
        setRewardName(rewardName);
        if (rewardPhoto != null) {
            setRewardPhoto(rewardPhoto);
        }
        setPointsValue(rewardPoints);
        setUsers(users);
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

    public List<ParseUser> getUsers() {
        List<ParseUser> list = getList(KEY_USERS);
        for (ParseUser user : list) {
            try {
                user.fetchIfNeeded();
            }
            catch (ParseException e) {
                Log.i("Reward", "Error fetching associated users.");
            }
        }
        return list;
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

    public void setUsers(List<ParseUser> users) {
        put(KEY_USERS, users);
    }

    @Override
    public int compareTo(Reward o) {
        return this.getPointsValue() - o.getPointsValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        return this.getObjectId().equals(((Reward)o).getObjectId());
    }
}

package com.example.taskify.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.taskify.R;
import com.example.taskify.databinding.ActivityAdditionalSignupBinding;
import com.example.taskify.databinding.ActivitySignupBinding;
import com.example.taskify.models.Reward;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.PhotoUtil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONObject;

import java.io.File;

public class AdditionalSignupActivity extends AppCompatActivity {

    private static final String TAG = "AdditionalSignupActivity";
    private ActivityAdditionalSignupBinding binding;
    private TaskifyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdditionalSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (TaskifyUser) ParseUser.getCurrentUser();
        setCurrentValues();

        binding.checkBoxIsParent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.checkBoxIsChild.setChecked(!isChecked);
            binding.layoutChildEnterParentUsername.setVisibility(isChecked? View.GONE : View.VISIBLE);
        });

        binding.checkBoxIsChild.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.checkBoxIsParent.setChecked(!isChecked);
            binding.layoutChildEnterParentUsername.setVisibility(isChecked? View.VISIBLE : View.GONE);
        });

        binding.buttonSignup.setOnClickListener(v -> {
            TaskifyUser user = (TaskifyUser) ParseUser.getCurrentUser();

            // Set core properties
            if (binding.editTextFirstName.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_first_name_text), Toast.LENGTH_SHORT).show();
                return;
            }
            user.setFirstName(binding.editTextFirstName.getText().toString());
            if (binding.editTextLastName.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_last_name_text), Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.editTextUsername.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_username_message), Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.editTextPassword.getText() .toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_password_message), Toast.LENGTH_SHORT).show();
                return;
            }
            user.setLastName(binding.editTextLastName.getText().toString());
            user.setUsername(binding.editTextUsername.getText().toString());
            user.setPassword(binding.editTextPassword.getText().toString());

            if (binding.checkBoxIsChild.isChecked()) {
                user.setIsParent(false);
                if (binding.editTextChildEnterParentUsername.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.child_sign_up_missing_parent_username), Toast.LENGTH_SHORT).show();
                    return;
                }
                String parentUsername = binding.editTextChildEnterParentUsername.getText().toString();
                TaskifyUser parent = ParseUtil.queryUser(parentUsername);
                if (parent == null) {
                    Toast.makeText(this, String.format(getString(R.string.child_sign_up_user_not_exist), parentUsername), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!parent.isParent()) {
                    Toast.makeText(this, String.format(getString(R.string.child_sign_up_user_not_parent), parentUsername), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Parent is a valid parent to be linked to this child.
                user.setParent(parent);
            }
            else {
                user.setIsParent(true);
            }

            // Invoke signUpInBackground
            user.saveInBackground(e -> {
                if (e == null) {
                    goToMainActivity();
                } else {
                    // Sign up didn't succeed. Returns error message to user.
                    Toast.makeText(this, ParseUtil.parseExceptionToErrorText(e), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ParseUtil.parseExceptionToErrorText(e), e);
                }
            });
        });
    }

    private void setCurrentValues() {
        // Tutorial: https://stackoverflow.com/questions/36740409/parsefacebookutil-using-android-not-giving-user-information
        // Fills in first and last name from Facebook.
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json, GraphResponse response) {
                // Application code
                if (response.getError() != null) {
                    Log.e(TAG, "Error retrieving Facebook values");
                    return;
                }
                String fbUserFirstName = json.optString("first_name");
                String fbUserLastName = json.optString("last_name");
                binding.editTextFirstName.setText(fbUserFirstName);
                binding.editTextLastName.setText(fbUserLastName);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(AdditionalSignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}

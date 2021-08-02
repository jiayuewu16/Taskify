package com.example.taskify.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.taskify.R;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.databinding.ActivitySignupBinding;
import com.example.taskify.util.ParseUtil;
import com.parse.facebook.ParseFacebookUtils;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_taskify_logo_transparent_white));
        }
        else {
            binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_taskify_logo_transparent));
        }

        binding.checkBoxIsParent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.checkBoxIsChild.setChecked(!isChecked);
            binding.layoutChildEnterParentUsername.setVisibility(isChecked? View.GONE : View.VISIBLE);
        });

        binding.checkBoxIsChild.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.checkBoxIsParent.setChecked(!isChecked);
            binding.layoutChildEnterParentUsername.setVisibility(isChecked? View.VISIBLE : View.GONE);
        });

        binding.buttonLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.buttonSignup.setOnClickListener(v -> {
            TaskifyUser user = new TaskifyUser();
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
            user.signUpInBackground(e -> {
                if (e == null) {
                    goToMainActivity();
                } else {
                    // Sign up didn't succeed. Returns error message to user.
                    Toast.makeText(this, ParseUtil.parseExceptionToErrorText(e), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ParseUtil.parseExceptionToErrorText(e), e);
                }
            });
        });

        binding.buttonFacebookLogin.setOnClickListener(v -> {
            //Tutorial: https://docs.parseplatform.org/android/guide/#facebook-users
            ParseFacebookUtils.logInWithReadPermissionsInBackground(SignupActivity.this, null, (user, err) -> {
                if (user == null) {
                    Log.d(TAG, "The user cancelled the Facebook login.");
                    return;
                }
                Log.d(TAG, "User logged in through Facebook!");
                if (((TaskifyUser)user).getFirstName() == null) {
                    // User has not completed signup yet.
                    goToAdditionalSignup();
                }
                else {
                    goToMainActivity();
                }
            });
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToAdditionalSignup() {
        Intent intent = new Intent(SignupActivity.this, AdditionalSignupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
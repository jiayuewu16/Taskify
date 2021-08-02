package com.example.taskify.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.taskify.R;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Persist login
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        TaskifyUser currentUser = (TaskifyUser) ParseUser.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getFirstName() == null) {
                goToAdditionalSignup();
            }
            else {
                goToMainActivity();
            }
        }
        else if (accessToken != null && !accessToken.isExpired()) {
            goToAdditionalSignup();
        }

        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_taskify_logo_transparent_white));
        }
        else {
            binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_taskify_logo_transparent));
        }

        binding.buttonLogin.setOnClickListener(
                view -> ParseUser.logInInBackground(binding.editTextUsername.getText().toString(),
                            binding.editTextPassword.getText().toString(), (user, e) -> {
                                if (user != null) {
                                    // The user is logged in.
                                    goToMainActivity();
                                } else {
                                    // Log in didn't succeed. Show returned error message to user.
                                    Toast.makeText(this, ParseUtil.parseExceptionToErrorText(e), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, ParseUtil.parseExceptionToErrorText(e), e);
                                }
                            }));

        binding.buttonFacebookLogin.setOnClickListener(v -> {
            //Tutorial: https://docs.parseplatform.org/android/guide/#facebook-users
            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, null, (user, err) -> {
                if (user == null) {
                    Log.d(TAG, "The user cancelled the Facebook login.");
                    return;
                }
                Log.d(TAG, "User logged in through Facebook!");
                if (((TaskifyUser) user).getFirstName() == null) {
                    // User has not completed signup yet.
                    goToAdditionalSignup();
                } else {
                    goToMainActivity();
                }
            });
        });

        binding.buttonSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void goToAdditionalSignup() {
        Intent intent = new Intent(LoginActivity.this, AdditionalSignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
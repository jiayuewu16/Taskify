package com.example.taskify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        TaskifyUser currentUser = (TaskifyUser) ParseUser.getCurrentUser();
        if (currentUser != null || (accessToken != null && !accessToken.isExpired())) {
            goToMainActivity();
        }

        binding.buttonLogin.setOnClickListener(
                view -> ParseUser.logInInBackground(binding.editTextUsername.getText().toString(),
                            binding.editTextPassword.getText().toString(), (user, e) -> {
                                if (user != null) {
                                    // The user is logged in.
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
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
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Facebook!");
                    goToMainActivity();
                } else {
                    Log.d(TAG, "User logged in through Facebook!");
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

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
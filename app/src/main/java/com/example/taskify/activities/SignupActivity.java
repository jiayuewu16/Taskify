package com.example.taskify.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskify.models.TaskifyUser;
import com.example.taskify.databinding.ActivitySignupBinding;
import com.example.taskify.util.ParseUtil;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.buttonSignup.setOnClickListener(v -> {
            TaskifyUser user = new TaskifyUser();
            // Set core properties
            if (!binding.editTextFirstName.getText().toString().isEmpty()) {
                user.setFirstName(binding.editTextFirstName.getText().toString());
            }
            if (!binding.editTextLastName.getText().toString().isEmpty()) {
                user.setLastName(binding.editTextLastName.getText().toString());
            }
            user.setUsername(binding.editTextUsername.getText().toString());
            user.setPassword(binding.editTextPassword.getText().toString());
            // Invoke signUpInBackground
            user.signUpInBackground(e -> {
                if (e == null) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Returns error message to user.
                    binding.textViewLoginError.setText(ParseUtil.parseExceptionToErrorText(e));
                }
            });
        });
    }
}
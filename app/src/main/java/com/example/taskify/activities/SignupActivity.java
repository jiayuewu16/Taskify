package com.example.taskify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskify.models.TaskifyUser;
import com.example.taskify.databinding.ActivitySignupBinding;
import com.example.taskify.util.ParseUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign up didn't succeed. Returns error message to user.
                            binding.textViewLoginError.setText(ParseUtil.parseExceptionToErrorText(e));
                        }
                    }
                });
            }
        });
    }
}
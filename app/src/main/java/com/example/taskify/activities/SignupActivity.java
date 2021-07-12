package com.example.taskify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.taskify.R;
import com.example.taskify.TaskifyUtilities;
import com.example.taskify.databinding.ActivitySignupBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;

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
                ParseUser user = new ParseUser();
                // Set core properties
                if (!binding.editTextFirstName.getText().toString().isEmpty()) {
                    user.put("firstName", binding.editTextFirstName.getText().toString());
                }
                if (!binding.editTextLastName.getText().toString().isEmpty()) {
                    user.put("lastName", binding.editTextLastName.getText().toString());
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
                            binding.textViewLoginError.setText(TaskifyUtilities.parseExceptionToErrorText(e));
                        }
                    }
                });
            }
        });
    }
}
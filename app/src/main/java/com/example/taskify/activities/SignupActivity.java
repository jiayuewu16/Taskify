package com.example.taskify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskify.models.TaskifyUser;
import com.example.taskify.databinding.ActivitySignupBinding;
import com.example.taskify.util.ParseUtil;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.checkBoxIsParent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.checkBoxIsChild.setChecked(!isChecked);
                binding.layoutChildEnterParentUsername.setVisibility(isChecked? View.GONE : View.VISIBLE);
            }
        });

        binding.checkBoxIsChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.checkBoxIsParent.setChecked(!isChecked);
                binding.layoutChildEnterParentUsername.setVisibility(isChecked? View.VISIBLE : View.GONE);
            }
        });

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

            if (binding.checkBoxIsChild.isChecked()) {
                user.setIsParent(false);
                if (binding.editTextChildEnterParentUsername.getText().toString().isEmpty()) {
                    Toast.makeText(this, "You must enter your parent's username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String parentUsername = binding.editTextChildEnterParentUsername.getText().toString();
                TaskifyUser parent = TaskifyUser.queryUser(parentUsername);
                if (parent == null) {
                    Toast.makeText(this, "User " + parentUsername + " does not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!parent.isParent()) {
                    Toast.makeText(this, "User " + parentUsername + " is not a parent.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Parent is a valid parent to be linked to this child.
                parent.setParent(user);
                user.setParent(parent);
            }
            else {
                user.setIsParent(true);
            }

            // Invoke signUpInBackground
            user.signUpInBackground(e -> {
                if (e == null) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Returns error message to user.
                    Toast.makeText(this, ParseUtil.parseExceptionToErrorText(e), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ParseUtil.parseExceptionToErrorText(e), e);
                }
            });
        });
    }
}
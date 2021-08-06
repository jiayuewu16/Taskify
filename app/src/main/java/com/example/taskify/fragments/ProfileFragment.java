package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskify.R;
import com.example.taskify.activities.LoginActivity;
import com.example.taskify.activities.MainActivity;
import com.example.taskify.adapters.UserAdapter;
import com.example.taskify.databinding.FragmentProfileBinding;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ColorUtil;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.PhotoUtil;
import com.example.taskify.util.TimeUtil;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final String TAG = "ProfileFragment";
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private TaskifyUser user;
    private MainActivity mainActivity;

    // Required empty public constructor
    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (TaskifyUser) ParseUser.getCurrentUser();
        ParseUtil.setPhoto(binding.imageViewProfilePhoto, user, AppCompatResources.getDrawable(mainActivity, R.drawable.ic_baseline_person_24));
        binding.textViewFullName.setText(String.format(getString(R.string.display_full_name_format), user.getFirstName(), user.getLastName()));
        binding.textViewFullName.setTextColor(ColorUtil.getTextColor(mainActivity));
        binding.textViewUsername.setText(String.format(getString(R.string.display_username_format), user.getUsername()));
        binding.textViewUsername.setTextColor(ColorUtil.getPrimaryColor(mainActivity));

        binding.floatingActionButtonCamera.setOnClickListener(v -> {
            // CodePath tutorial
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile1 = PhotoUtil.getPhotoFileUri(mainActivity, PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
            Uri fileProvider = FileProvider.getUriForFile(mainActivity, getString(R.string.uri_fileprovider_authority), photoFile1);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (takePictureIntent.resolveActivity(mainActivity.getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        List<TaskifyUser> users = mainActivity.associatedUsers;
        if (user.isParent()) {
            binding.textViewAssociatedUserHeader.setText(getString(R.string.profile_children_header));
        }
        else {
            binding.textViewAssociatedUserHeader.setText(getString(R.string.profile_parent_header));
        }
        binding.textViewAssociatedUserHeader.setTextColor(ColorUtil.getTextColor(mainActivity));
        UserAdapter adapter = new UserAdapter(mainActivity, users);
        binding.recyclerViewParentDisplayChild.setAdapter(adapter);
        binding.recyclerViewParentDisplayChild.setLayoutManager(new LinearLayoutManager(mainActivity));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            if (user.isParent()) {
                mainActivity.associatedUsers.clear();
                mainActivity.associatedUsers.addAll(user.queryChildren());
                adapter.notifyDataSetChanged();
            }
            else {
                mainActivity.associatedUsers.clear();
                mainActivity.associatedUsers.add(user.getParent());
                adapter.notifyDataSetChanged();
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.changeThemeBar.setItemIconTintList(null);
        binding.changeThemeBar.setSelectedItemId(mainActivity.theme);
        binding.changeThemeBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.themeBlueCyan) {
                mainActivity.theme = R.style.Theme_blue_cyan_Taskify;
            }
            else if (item.getItemId() == R.id.themeMaroonPeach) {
                mainActivity.theme = R.style.Theme_maroon_peach_Taskify;
            }
            else if (item.getItemId() == R.id.themePinkOrange) {
                mainActivity.theme = R.style.Theme_pink_orange_Taskify;
            }
            else if (item.getItemId() == R.id.themeBrownGreen) {
                mainActivity.theme = R.style.Theme_brown_green_Taskify;
            }
            else {
                mainActivity.theme = R.style.Theme_Taskify;
            }
            mainActivity.setTheme(mainActivity.theme);
            setTheme();
            // Manually set current theme colors
            mainActivity.binding.bottomNavigationBar.setItemIconTintList(ColorStateList.valueOf(ColorUtil.getPrimaryColor(mainActivity)));
            mainActivity.binding.bottomNavigationBar.setItemTextColor(ColorStateList.valueOf(ColorUtil.getPrimaryColor(mainActivity)));
            binding.textViewFullName.setTextColor(ColorUtil.getTextColor(mainActivity));
            binding.textViewUsername.setTextColor(ColorUtil.getPrimaryColor(mainActivity));
            adapter.notifyDataSetChanged();
            binding.textViewAssociatedUserHeader.setTextColor(ColorUtil.getTextColor(mainActivity));
            binding.floatingActionButtonCamera.setBackgroundTintList(ColorStateList.valueOf(ColorUtil.getSecondaryColor(mainActivity)));
            binding.buttonSignout.setBackgroundColor(ColorUtil.getPrimaryColor(mainActivity));
            return true;
        });

        if (ParseFacebookUtils.isLinked(user)) {
            binding.buttonFacebookLink.setVisibility(View.GONE);
        }
        else {
            binding.buttonFacebookLink.setOnClickListener(v -> {
                if (!ParseFacebookUtils.isLinked(user)) {
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, requireActivity(), null, e -> {
                        if (ParseFacebookUtils.isLinked(user)) {
                            Log.d(TAG, "User linked with Facebook!");
                            Toast.makeText(mainActivity, getString(R.string.success_link_facebook_message), Toast.LENGTH_SHORT).show();
                            binding.buttonFacebookLink.setVisibility(View.GONE);
                            return;
                        }
                        Log.e(TAG, "User link with Facebook failed.", e);
                        Toast.makeText(mainActivity, getString(R.string.error_link_facebook_message), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }

        binding.buttonSignout.setOnClickListener(v -> {
            TimeUtil.cancelAlarms(mainActivity);
            ParseUser.logOut();
            Intent intent = new Intent(mainActivity, LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                // Codepath tutorial
                File photoFile = PhotoUtil.getPhotoFileUri(mainActivity, PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
                // by this point we have the camera photo on disk
                Bitmap takenImage = PhotoUtil.rotateBitmapOrientation(photoFile.getAbsolutePath());
                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = PhotoUtil.scaleToFitWidth(takenImage, PhotoUtil.DEFAULT_WIDTH);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                try {
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    // Write the bytes of the bitmap to file
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                photoFile = PhotoUtil.getPhotoFileUri(mainActivity, PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
                // Load the resized image into a preview
                binding.imageViewProfilePhoto.setImageBitmap(resizedBitmap);
                user.setProfilePhoto(new ParseFile(photoFile));
                ParseUtil.save(user, mainActivity, TAG, getString(R.string.success_save_profile_image), getString(R.string.error_save_profile_image));

            } else { // Result was a failure
                Toast.makeText(mainActivity, getString(R.string.error_take_camera_picture), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTheme() {
        user.setTheme(mainActivity.theme);
        ParseUtil.save(user, mainActivity, TAG, null, getString(R.string.error_save_theme));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mainActivity = (MainActivity) context;
    }
}
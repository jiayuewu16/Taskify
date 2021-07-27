package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.taskify.adapters.UserAdapter;
import com.example.taskify.databinding.FragmentProfileBinding;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ColorUtil;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.PhotoUtil;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final String TAG = "ProfileFragment";
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private TaskifyUser user;
    private Context context;

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
        ParseUtil.setPhoto(binding.imageViewProfilePhoto, user, AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_24));
        if (user.getFirstName() != null) {
            binding.textViewFirstName.setText(user.getFirstName());
            binding.textViewFirstName.setTextColor(ColorUtil.getTextColor(context));
        }
        if (user.getLastName() != null) {
            binding.textViewLastName.setText(user.getLastName());
            binding.textViewLastName.setTextColor(ColorUtil.getTextColor(context));
        }
        binding.textViewUsername.setText(String.format(getString(R.string.display_username_format), user.getUsername()));
        binding.textViewUsername.setTextColor(ColorUtil.getPrimaryColor(context));

        binding.floatingActionButtonCamera.setOnClickListener(v -> {
            // CodePath tutorial
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile1 = PhotoUtil.getPhotoFileUri(context, PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
            Uri fileProvider = FileProvider.getUriForFile(context, getString(R.string.uri_fileprovider_authority), photoFile1);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        List<TaskifyUser> users;
        if (user.isParent()) {
            binding.textViewAssociatedUserHeader.setText(getString(R.string.profile_children_header));
            users = user.queryChildren();
        }
        else {
            binding.textViewAssociatedUserHeader.setText(getString(R.string.profile_parent_header));
            users = Collections.singletonList(user.getParent());
        }
        binding.textViewAssociatedUserHeader.setTextColor(ColorUtil.getTextColor(context));
        UserAdapter adapter = new UserAdapter(context, users);
        binding.recyclerViewParentDisplayChild.setAdapter(adapter);
        binding.recyclerViewParentDisplayChild.setLayoutManager(new LinearLayoutManager(context));

        if (ParseFacebookUtils.isLinked(user)) {
            binding.buttonFacebookLink.setVisibility(View.GONE);
        }
        else {
            binding.buttonFacebookLink.setOnClickListener(v -> {
                Log.i(TAG, "User: " + user.toString());
                if (!ParseFacebookUtils.isLinked(user)) {
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, requireActivity(), null, e -> {
                        if (ParseFacebookUtils.isLinked(user)) {
                            Log.d(TAG, "User linked with Facebook!");
                            Toast.makeText(context, getString(R.string.success_link_facebook_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.e(TAG, "User link with Facebook failed.", e);
                        Toast.makeText(context, getString(R.string.error_link_facebook_message), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }

        binding.buttonSignout.setOnClickListener(v -> {
            ParseUser.logOut();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                // Codepath tutorial
                File photoFile = PhotoUtil.getPhotoFileUri(context, PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
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

                photoFile = PhotoUtil.getPhotoFileUri(context, PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
                // Load the resized image into a preview
                binding.imageViewProfilePhoto.setImageBitmap(resizedBitmap);
                user.setProfilePhoto(new ParseFile(photoFile));
                ParseUtil.save(user, context, TAG, getString(R.string.success_save_profile_image), getString(R.string.error_save_profile_image));

            } else { // Result was a failure
                Toast.makeText(context, getString(R.string.error_take_camera_picture), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
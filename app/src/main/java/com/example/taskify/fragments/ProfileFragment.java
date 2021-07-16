package com.example.taskify.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.taskify.R;
import com.example.taskify.activities.LoginActivity;
import com.example.taskify.databinding.FragmentProfileBinding;
import com.example.taskify.models.TaskifyUser;
import com.example.taskify.util.ParseUtil;
import com.example.taskify.util.PhotoUtil;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final String TAG = "ProfileFragment";
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private TaskifyUser user;

    // Required empty public constructor
    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (TaskifyUser) ParseUser.getCurrentUser();
        ParseFile photoFile = user.getProfilePhoto();
        if (photoFile != null) {
            photoFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error getting profile photo.");
                        return;
                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    binding.imageViewProfilePhoto.setImageBitmap(bitmap);
                }
            });
        }
        if (user.getFirstName() != null) {
            binding.textViewFirstName.setText(user.getFirstName());
        }
        if (user.getLastName() != null) {
            binding.textViewLastName.setText(user.getLastName());
        }
        binding.textViewUsername.setText(String.format("@%s", user.getUsername()));

        binding.floatingActionButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CodePath tutorial
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = PhotoUtil.getPhotoFileUri(getActivity(), PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
                Uri fileProvider = FileProvider.getUriForFile(getActivity(), getActivity().getResources().getString(R.string.uri_fileprovider_authority), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        binding.buttonSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                // Codepath tutorial
                File photoFile = PhotoUtil.getPhotoFileUri(getActivity(), PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
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

                photoFile = PhotoUtil.getPhotoFileUri(getActivity(), PhotoUtil.DEFAULT_PHOTO_FILE_NAME);
                // Load the resized image into a preview
                binding.imageViewProfilePhoto.setImageBitmap(resizedBitmap);
                user.setProfilePhoto(new ParseFile(photoFile));
                ParseUtil.save(user, getActivity(), TAG,
                        getContext().getResources().getString(R.string.success_save_profile_image),
                        getContext().getResources().getString(R.string.error_save_profile_image));

            } else { // Result was a failure
                Toast.makeText(getActivity(), getContext().getResources().getString(R.string.error_take_camera_picture), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
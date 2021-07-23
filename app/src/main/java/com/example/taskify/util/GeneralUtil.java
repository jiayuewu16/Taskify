package com.example.taskify.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class GeneralUtil {

    private static final String TAG = "GeneralUtil";

    /**
     * Returns a random non-negative integer.
     */
    public static int randomInt() {
        return randomInt(0, Integer.MAX_VALUE);
    }

    public static int randomInt(int min, int max) {
        return (int)(Math.random() * (max-min+1) + min);
    }

    // Returns the File for a photo stored on disk given the fileName
    public static File getFileUri(Context context, String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_ALARMS), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.e(TAG, "failed to create directory");
        }

        // Return the file target for the file based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}

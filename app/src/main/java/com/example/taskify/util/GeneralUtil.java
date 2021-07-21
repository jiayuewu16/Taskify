package com.example.taskify.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_ALARMS), PhotoUtil.APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(PhotoUtil.APP_TAG, "failed to create directory");
        }

        // Return the file target for the file based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public static byte[] objectToByteArray(Object object) {
        // Tutorial: https://stackoverflow.com/questions/40480355/pass-serializable-object-to-pending-intent
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] data = bos.toByteArray();
            bos.close();
            return data;
        } catch (IOException e) {
            Log.e(TAG, "Error with converting object to byte array.", e);
        }
        return null;
    }

    public static Object byteArrayToObject(byte[] byteArray) {
        // Tutorial: https://stackoverflow.com/questions/40480355/pass-serializable-object-to-pending-intent
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        ObjectInput in;
        Object object;
        try {
            in = new ObjectInputStream(bis);
            object = in.readObject();
            if (in != null) {
                in.close();
            }
            return object;
        } catch (ClassNotFoundException | IOException e) {
            Log.e(TAG, "Error with converting byte array to object.", e);
        }
        return null;
    }
}

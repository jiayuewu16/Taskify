package com.example.taskify.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;

import com.example.taskify.R;

import java.util.List;

public class ColorUtil {
    public static void alternateTextViewColors(List<TextView> list, int color1, int color2) {
        for (int i = 0; i < list.size(); i++) {
            TextView view = list.get(i);
            if (i % 2 == 0) {
                view.setTextColor(color1);
            }
            else {
                view.setTextColor(color2);
            }
        }
    }

    public static void setImageViewColor(ImageView imageView, int color) {
        imageView.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public static void setCheckBoxColor(CheckBox checkBox, int color) {
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(color));
    }

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static int getSecondaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        return typedValue.data;
    }
}

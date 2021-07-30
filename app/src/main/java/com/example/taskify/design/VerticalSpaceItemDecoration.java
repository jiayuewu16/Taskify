package com.example.taskify.design;

// Tutorial: https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) != Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
            outRect.bottom = 14;
        }
    }
}

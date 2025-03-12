package com.lerchenflo.t10elementekatalog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RightDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private final int dividerHeight;
    private final int margin;

    public RightDividerItemDecoration(Context context, int color, int height, int margin) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(height);
        this.dividerHeight = height;
        this.margin = margin;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount - 1; i++) { // Avoid drawing after the last item
            View child = parent.getChildAt(i);
            int left = child.getLeft() + margin;
            int right = child.getRight() - margin;
            int bottom = child.getBottom(); // Bottom edge of the element

            canvas.drawLine(left, bottom, right, bottom, paint); // Draw horizontal line
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = dividerHeight; // Reserve space for the horizontal divider
    }
}

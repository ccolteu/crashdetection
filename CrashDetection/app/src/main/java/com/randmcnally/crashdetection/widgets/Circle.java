package com.randmcnally.crashdetection.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.randmcnally.crashdetection.R;


public class Circle extends View {

    private static final int START_ANGLE_POINT = 270;

    private Paint paint;
    private RectF rect;
    private float angle;
    final int strokeWidth = 20;

    boolean clearCanvas = false;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.argb(255, 51, 51, 51)); // colorAccent - 333333

        init();
    }

    private void init() {
        int size = getResources().getDimensionPixelSize(R.dimen.count_down_circle_size);
        rect = new RectF(strokeWidth/2, strokeWidth/2, size - strokeWidth/2, size - strokeWidth/2);
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (clearCanvas) {
            canvas.drawColor(Color.TRANSPARENT);
            clearCanvas = false;
            init();
        } else {
            canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void clear() {
        clearCanvas = true;
        invalidate();
    }
}


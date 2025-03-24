package com.example.stridefuel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircularProgressBar extends View {
    private Paint progressPaint;
    private Paint backgroundPaint;
    private RectF rectF;
    private float progress = 0; // Progress value (0 to 100)

    public CircularProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint.setColor(0xFF72D1BF); // Greenish color
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND); // Round edges
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(35f); // Thickness of progress bar

        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFF0F0F0); // Light gray color
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeWidth(35f);

        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = 20f; // Ensure stroke doesn't cut off
        rectF.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background arc (full circle)
        canvas.drawArc(rectF, 135, 270, false, backgroundPaint);

        // Draw progress arc
        float angle = (270 * progress) / 100;
        canvas.drawArc(rectF, 135, angle, false, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate(); // Redraw the view
    }
}


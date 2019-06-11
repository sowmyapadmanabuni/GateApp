package com.oyespace.guards.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.oyespace.guards.R;


public class SampleProgressView extends View {

    public SampleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SampleProgressView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        this.context = context;
        progress_paint.setColor(context.getResources().getColor(R.color.white));
        progress_paint.setStrokeWidth(4);
        progress_paint.setAntiAlias(true);
        progress_paint.setStrokeCap(Paint.Cap.ROUND);
        progress_paint.setStyle(Paint.Style.STROKE);
        paint.setColor(context.getResources().getColor(R.color.app_theme_color));
        paint.setStrokeWidth(6);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    Context context;
    Paint paint = new Paint();
    RectF rect = new RectF();
    Paint progress_paint = new Paint();

    int tag = 0;

    int _X = -90;
    int X = 0;

    public SampleProgressView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int radious = (width / 2) - 20;
        rect.set(width / 2 - radious, height / 2 - radious,
                width / 2 + radious, height / 2 + radious);
        // canvas.drawRect(rect, progress_paint);
        canvas.drawCircle(width / 2, height / 2, (width / 2) - 20, paint);
        if (tag == 1) {
            canvas.drawArc(rect, -90, X, false, progress_paint);
            X++;
            if (X != 660)
                invalidate();
        }
        if (tag == 0) {
            canvas.drawArc(rect, _X, 30, false, progress_paint);
            _X = _X + 4;
            invalidate();
        }
        super.onDraw(canvas);
    }

}

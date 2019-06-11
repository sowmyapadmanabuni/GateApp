package com.oyespace.guards.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.oyespace.guards.R;


/**
 * Created by IntelliJ IDEA.
 * User: keith.
 * Date: 14-9-10.
 * Time: 11:34.
 */
public class LoadingView extends View {

    private int maxCircleRadius;

    private Point mLeftCircleCenter;

    private Point mCenterCircleCenter;

    private Point mRightCircleCenter;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private long AnimationDuration = 1260;

    private long mStartTime;

    private static final int START_ANIMATION = 1;

    private int mLeftCircleRadius = 0;

    private int mCenterCircleRadius = 0;

    private int mRightCircleRadius = 0;

    private Handler animationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_ANIMATION:
                    long currentTime = System.currentTimeMillis();
                    long passTime = currentTime - mStartTime;
                    if (passTime < AnimationDuration) {
                        processKeyTime(passTime);
                        animationHandler.sendEmptyMessage(START_ANIMATION);
                    } else {
                        restore();
                    }
                    break;
            }
        }
    };

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLeftCircleCenter = new Point();
        mCenterCircleCenter = new Point();
        mRightCircleCenter = new Point();
        paint.setColor(getResources().getColor(R.color.app_theme_color));
        start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        maxCircleRadius = Math.min(viewWidth / 3 / 2, viewHeight / 2);
        int gridSize = viewWidth / 3 / 2;
        int y = viewHeight / 2;
        mLeftCircleCenter.set(gridSize, y);
        mCenterCircleCenter.set(gridSize * 3, y);
        mRightCircleCenter.set(gridSize * 5, y);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    public void start() {
        mStartTime = System.currentTimeMillis();
        animationHandler.sendEmptyMessage(START_ANIMATION);
    }

    private void restore() {
        mLeftCircleRadius = 0;
        mRightCircleRadius = 0;
        mRightCircleRadius = 0;
        invalidate();
        mStartTime = System.currentTimeMillis();
        animationHandler.sendEmptyMessage(START_ANIMATION);
    }

    private void processKeyTime(long passTime) {
        long key1 = AnimationDuration / 2;
        if (passTime < key1) {
            mLeftCircleRadius = Math.round(maxCircleRadius * passTime / key1);
        } else {
            long time = AnimationDuration - passTime;
            mLeftCircleRadius = Math.round(maxCircleRadius * time / key1);
            if (mLeftCircleRadius < 2) {
                mLeftCircleRadius = 0;
            }
        }
        long key2 = AnimationDuration / 3;
        if (passTime > key2 && passTime <= key2 * 2) {
            mCenterCircleRadius = Math.round(maxCircleRadius * (passTime - key2) / key2);
        } else if (passTime > key2 * 2) {
            long time = key2 - passTime + key2 * 2;
            mCenterCircleRadius = Math.round(maxCircleRadius * time / key2);
            if (mCenterCircleRadius < 2) {
                mCenterCircleRadius = 0;
            }
        }
        long key3 = AnimationDuration / 6;
        if (passTime > key3 * 4 && passTime < key3 * 5) {
            mRightCircleRadius = Math.round(maxCircleRadius * (passTime - key3 * 4) / key3);
        } else if (passTime > key3 * 5) {
            long time = key3 - passTime + key3 * 5;
            mRightCircleRadius = Math.round(maxCircleRadius * time / key3);
            if (mRightCircleRadius < 2) {
                mRightCircleRadius = 0;
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mLeftCircleCenter.x, mLeftCircleCenter.y, mLeftCircleRadius, paint);
        canvas.drawCircle(mCenterCircleCenter.x, mCenterCircleCenter.y, mCenterCircleRadius, paint);
        canvas.drawCircle(mRightCircleCenter.x, mRightCircleCenter.y, mRightCircleRadius, paint);
    }
}

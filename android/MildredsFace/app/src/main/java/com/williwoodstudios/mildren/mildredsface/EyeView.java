package com.williwoodstudios.mildren.mildredsface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by robwilliams on 15-08-28.
 */
public class EyeView extends View {
    private static final String LOG_TAG = "EyeView";
    private PositionChangedListener mPositionChangedListener;
    private Paint mPaintEyeFill;
    private Paint mPaintEyeOutline;
    private Paint mPaintPupilFill;
    private double mPupilX = 0;
    private double mPupilY = 0;
    private float mLastX, mLastY;

    public EyeView(Context context) {
        super(context);
        Log.e(LOG_TAG, "Created...");
        init();
    }

    public EyeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e(LOG_TAG, "Created without def style");
        init();
    }

    public EyeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.e(LOG_TAG, "Created - with attributes");
        init();
    }

    public void setPositionChangedListener(PositionChangedListener positionChangedListener) {
        mPositionChangedListener = positionChangedListener;
    }

    public void setPosition(double pupilX, double pupilY) {
        if (pupilX != mPupilX || pupilY != mPupilY) {
            mPupilX = pupilX;
            mPupilY = pupilY;
            invalidate();
            if (mPositionChangedListener != null) {
                mPositionChangedListener.positionChanged(this, mPupilX, mPupilY);
            }
        }
    }

    private void init() {
        mPaintEyeFill = new Paint();
        mPaintEyeFill.setColor(0xffefefaf);
        mPaintEyeFill.setAntiAlias(true);
        mPaintEyeFill.setStyle(Paint.Style.FILL);

        mPaintEyeOutline = new Paint();
        mPaintEyeOutline.setColor(0xff000000);
        mPaintEyeOutline.setAntiAlias(true);
        mPaintEyeOutline.setStrokeWidth(6);
        mPaintEyeOutline.setStyle(Paint.Style.STROKE);

        mPaintPupilFill = new Paint();
        mPaintPupilFill.setColor(0xff000000);
        mPaintPupilFill.setAntiAlias(true);
        mPaintPupilFill.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate((int) (mLastX - 20), (int) (mLastY - 20), (int) (mLastX + 20), (int) (mLastY + 20));
        mLastX = event.getX();
        mLastY = event.getY();

        float haWidth = getWidth() / 2.0f;
        float haHeight = getHeight() / 2.0f;

        float radius = min(haHeight - 10, haWidth - 10);
        float radiusForPupil = radius - radius / 5;

        float deltaX = mLastX - haWidth;
        float deltaY = mLastY - haHeight;

        if (deltaX == 0 && deltaY == 0) {
            setPosition(0, 0);
        } else if (deltaX == 0) {
            double possible = deltaY / radiusForPupil;
            if (possible < -1) {
                possible = -1;
            } else if (possible > 1) {
                possible = 1;
            }

            setPosition(0, possible);
        } else if (deltaY == 0) {
            double possible = deltaX / radiusForPupil;
            if (possible < -1) {
                possible = -1;
            } else if (possible > 1) {
                possible = 1;
            }
            setPosition(possible, 0);
        } else {
            double angle = Math.atan(deltaY / deltaX);
            if (deltaX > 0 && deltaY > 0) {
                // good
            } else if (deltaX > 0) {
                angle = Math.PI * 2 + angle;
            } else if (deltaY > 0) {
                angle = Math.PI + angle;
            } else {
                angle = Math.PI + angle;
            }
            double effectiveRadius = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (effectiveRadius > radiusForPupil) {
                setPosition(Math.cos(angle), Math.sin(angle));
            } else {
                setPosition(deltaX / radiusForPupil, deltaY / radiusForPupil);
            }

            Log.e(LOG_TAG, "Angle: " + angle + " " + (angle / 2 / Math.PI * 360));
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float haWidth = width / 2.0f;
        float haHeight = height / 2.0f;
        float radius = min(haHeight - 10, haWidth - 10);
        float radiusForPupil = radius - radius / 5;
//        canvas.drawARGB(255, 127, 127, 127);
        canvas.drawCircle(haWidth, haHeight, radius, mPaintEyeFill);
        canvas.drawCircle(haWidth, haHeight, radius, mPaintEyeOutline);

        float drawPupilX = (float) (haWidth + mPupilX * radiusForPupil);
        float drawPupilY = (float) (haHeight + mPupilY * radiusForPupil);

        canvas.drawCircle(drawPupilX, drawPupilY, radius / 5, mPaintPupilFill);

        // canvas.drawCircle(mLastX,mLastY, 10, mPaintPupilFill);
    }

    private float min(float a, float b) {
        return a < b ? a : b;
    }


    public interface PositionChangedListener {
        void positionChanged(EyeView eyeView, double pupilX, double pupilY);
    }

}

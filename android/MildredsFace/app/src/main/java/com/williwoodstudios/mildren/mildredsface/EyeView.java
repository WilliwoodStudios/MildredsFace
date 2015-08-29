package com.williwoodstudios.mildren.mildredsface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private Paint mPaintEyeFill;
    private Paint mPaintEyeOutline;
    private Paint mPaintPupilFill;

    private double mAngle = 0;
    private double mRadius = 1;

    public EyeView(Context context) {
        super(context);
        Log.e(LOG_TAG,"Created...");
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

    private float mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate((int)(mLastX-20),(int)(mLastY-20),(int)(mLastX+20),(int)(mLastY+20));
        mLastX = event.getX();
        mLastY = event.getY();
        invalidate((int)(mLastX-20),(int)(mLastY-20),(int)(mLastX+20),(int)(mLastY+20));
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
        float radiusForPupil = radius - radius/5;
        canvas.drawARGB(255, 127, 255, 127);
        canvas.drawCircle(haWidth, haHeight, radius, mPaintEyeFill);
        canvas.drawCircle(haWidth,haHeight,radius,mPaintEyeOutline);

        float pupilX = (float)(haWidth + Math.cos(mAngle)*mRadius*radiusForPupil);
        float pupilY = (float)(haHeight + Math.sin(mAngle)*mRadius*radiusForPupil);

        canvas.drawCircle(pupilX, pupilY, radius/5, mPaintPupilFill);

        canvas.drawCircle(mLastX,mLastY, 10, mPaintPupilFill);
    }



    private float min(float a, float b) {
        return a < b ? a : b;
    }

}

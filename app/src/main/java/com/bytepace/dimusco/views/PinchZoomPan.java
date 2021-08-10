package com.bytepace.dimusco.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.bytepace.dimusco.helper.GlobalVariables;

import java.io.IOException;
import java.net.URL;

public class PinchZoomPan extends View {


    private Bitmap mPageImage;
    private int mPageImageWidth, mPageImageHeight;

    private float mPositionX;
    private float mPositionY;
    private float mLastTouchX;
    private float mLastTouchY;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerID = INVALID_POINTER_ID;


    private ScaleGestureDetector mScaleDetector;
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 10f;
    private float scaleFactor = 1.f;

    public PinchZoomPan(Context context) {
        super(context);

        init(null);
    }

    public PinchZoomPan(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public PinchZoomPan(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public PinchZoomPan(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set ) {
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);

        final int action = event.getAction();



        switch (action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{

                final float x = event.getX();
                final float y = event.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                mActivePointerID = event.getPointerId(0);

                break;
            }
            case MotionEvent.ACTION_MOVE:{

                final int pointerIndex = event.findPointerIndex(mActivePointerID);

                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                if(!mScaleDetector.isInProgress() && event.getPointerCount() == 2){
                    final float distanceX = x - mLastTouchX;
                    final float distanceY = y - mLastTouchY;

                    mPositionX += distanceX;
                    mPositionY += distanceY;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP:{
                mActivePointerID = INVALID_POINTER_ID;

                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                mActivePointerID = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:{
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);

                if(pointerId == mActivePointerID){

                    final int newPointerIndex = pointerIndex == 0  ? 1: 0;

                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);

                    mActivePointerID = event.getPointerId(newPointerIndex);

                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mPageImage != null) {
            canvas.save();

            if((mPositionX * -1) < 0){
                mPositionX = 0;
            } else if((mPositionX * -1) > (mPageImageWidth * scaleFactor - getWidth())) {
                mPositionX = (mPageImageWidth * scaleFactor - getWidth()) * -1;
            }

            if((mPositionY * -1) < 0){
                mPositionY = 0;
            } else if((mPositionY * -1) > (mPageImageWidth * scaleFactor - getHeight())) {
                mPositionY = (mPageImageWidth * scaleFactor - getHeight()) * -1;
            }

            if((mPageImageHeight * scaleFactor) < getHeight()){
                mPositionY = 0;
            }

            canvas.translate(mPositionX, mPositionY);
            canvas.scale(scaleFactor, scaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());

            canvas.drawBitmap(mPageImage, 0,0,null);

            canvas.restore();
        }
    }

    public void loadPageImageOnCanvas(String filePath){
        try {
            URL url = new URL(filePath);
            mPageImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            postInvalidate();
        } catch(IOException e) {
            System.out.println(e);
        }

        float aspectratio = (float) mPageImage.getHeight() / (float) mPageImage.getWidth();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenOrientation = GlobalVariables.Companion.getCvScreenOrientation();
        if(screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            mPageImageHeight = displayMetrics.heightPixels - 60;
            mPageImageWidth = Math.round(mPageImageHeight / aspectratio);
        }
        if(screenOrientation == Configuration.ORIENTATION_PORTRAIT){
            mPageImageWidth = displayMetrics.widthPixels;
            mPageImageHeight = Math.round(mPageImageWidth * aspectratio);
        }

        mPageImage = mPageImage.createScaledBitmap(mPageImage, mPageImageWidth, mPageImageHeight, false);
        invalidate();
//        requestLayout();
    }
}

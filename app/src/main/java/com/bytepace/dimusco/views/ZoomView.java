package com.bytepace.dimusco.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.bytepace.dimusco.R;
import com.bytepace.dimusco.helper.GlobalVariables;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ZoomView extends View {

    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 10f;

    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;

    private static final int SQUARE_SIZE_DEF = 200;

    private Rect mRectSquare;
    private Paint mPaintSquare;
    private Paint mPaintCircle;

    private int mSquareColor;
    private int mSquareSize;
    private float mCircleX, mCircleY;
    private float mCircleRadius = 100f;

    private float mSymbolX, mSymbolY;
    private Paint mPaintSymbol;

    private Bitmap mImage;

    private float mPageX, mPageY;
    private Bitmap mPageImage;
    private int mPageImageWidth, mPageImageHeight;

    public ZoomView(Context context) {
        super(context);

        init(null);
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set ) {
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.parseColor("#00ccff"));

        mPaintSymbol = new Paint();
        mPaintSymbol.setAntiAlias(true);
        mPaintSymbol.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN));

        setSymbol(getContext(), "symbols/symbol_1.png");

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mImage = getResizedBitmap(mImage, 100, 100);

//                if(mPageImage != null){
//                    mPageImage = getResizedPageBitmap(mPageImage);
//                }
            }
        });

        if(set == null){
            return;
        }

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);

        mSquareColor = ta.getColor(R.styleable.CustomView_square_color, Color.GREEN);
        mSquareSize = (int) ta.getDimension(R.styleable.CustomView_square_size, SQUARE_SIZE_DEF);

        mPaintSquare.setColor(mSquareColor);

        ta.recycle();

    }

    public void swapColor(String color){
        mPaintSquare.setColor(Color.parseColor(color));

        mPaintSymbol.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN));

        postInvalidate();
    }

    public void setPageImage(String filePath){
        try {
            URL url = new URL(filePath);
            mPageImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            postInvalidate();
        } catch(IOException e) {
            System.out.println(e);
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

    public void setSymbol(Context context, String filePath){
        mImage = getBitmapFromAsset(context, filePath);
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        boolean value = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();

                if(!GlobalVariables.Companion.getMStatus()){
                    if(!GlobalVariables.Companion.getSymbol().isEmpty()){
                        setSymbol( getContext(), "symbols/" + GlobalVariables.Companion.getSymbol() + ".png");
                        mSymbolX = x;
                        mSymbolY = y;

                        postInvalidate();
                    }
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE: {

                float x = event.getX();
                float y = event.getY();

                double dx = Math.pow(Math.abs(x - mCircleX), 2);
                double dy = Math.pow(Math.abs(y - mCircleY), 2);

                if(dx + dy < Math.pow(mCircleRadius,2)) {

                    mSymbolX = x;
                    mSymbolY = y;

                    postInvalidate();

                    return true;
                } else {
                    GlobalVariables.Companion.setMStatus(true);
                }

                return value;
            }
        }

        return value;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mPageImage != null) {
            canvas.save();
            canvas.scale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());

            // ...
            // your canvas-drawing code

//        canvas.drawColor(Color.RED);

            canvas.drawBitmap(mPageImage, 0, 0, null);

            mRectSquare.left = 50;
            mRectSquare.top = 50;
            mRectSquare.right = mRectSquare.left + mSquareSize;
            mRectSquare.bottom =mRectSquare.top + mSquareSize;

            canvas.drawRect(mRectSquare, mPaintSquare);

            if(mCircleX == 0f || mCircleY == 0f) {
                mCircleX = getWidth() / 2.0f;
                mCircleY = getHeight() / 2.0f;
            }
            canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mPaintCircle);

            float imageX = mSymbolX - mImage.getWidth() / 2.0f;
            float imageY = mSymbolY - mImage.getHeight() / 2.0f;

            canvas.drawBitmap(mImage, imageX, imageY, mPaintSymbol);
            // ...

            canvas.restore();
        }
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

    

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int reqWidth, int reqHeight){
        Matrix matrix = new Matrix();

        RectF src = new RectF(mSymbolX, mSymbolY, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(mSymbolX, mSymbolY, reqWidth, reqHeight);

        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(bitmap, (int)mSymbolX, (int)mSymbolY, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap getResizedPageBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();

        Integer cvWidth = getRootView().getWidth();
        Integer cvHeight = getRootView().getHeight();
        Integer bmWidth = bitmap.getWidth();
        Integer bmHeight = bitmap.getHeight();
        Integer tgWidth = cvWidth;
        Integer tgHeight = cvHeight;

        if(GlobalVariables.Companion.getCvScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE && cvHeight > 0 && bmWidth >0 && bmHeight >0){
            tgHeight = cvWidth;
            tgWidth = cvHeight;
            if( (bmHeight * cvHeight / cvWidth) > tgWidth){
                tgHeight = cvHeight * bmHeight / bmWidth;
            } else {
                tgWidth = bmHeight * cvHeight / cvWidth;
            }
        }

        if(GlobalVariables.Companion.getCvScreenOrientation() == Configuration.ORIENTATION_PORTRAIT && cvWidth > 0 && bmWidth > 0 && bmHeight > 0){
            if( (bmWidth * cvHeight / cvWidth) > tgHeight){
                tgWidth = bmHeight * cvWidth/ cvHeight;
            } else {
                tgHeight = cvWidth * bmHeight / bmWidth;
            }
        }

        RectF src = new RectF(0, 0, bmWidth, bmHeight);
        RectF dst = new RectF(0, 0, tgWidth, tgHeight);
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);

        Integer x = (cvWidth - tgWidth) / 2;
        Integer y = (cvHeight - tgHeight) / 2;

        return Bitmap.createBitmap(bitmap, 0, 0, bmWidth, bmHeight, matrix, true);
    }
}

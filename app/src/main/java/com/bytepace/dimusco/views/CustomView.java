package com.bytepace.dimusco.views;

import android.content.Context;
import android.content.res.AssetManager;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.bytepace.dimusco.R;

import java.io.IOException;
import java.io.InputStream;

public class CustomView extends View {

    private static final int SQUARE_SIZE_DEF = 200;

    private Rect mRectSquare;
    private Paint mPaintSquare;
    private Paint mPaintCircle;

    private int mSquareColor;
    private int mSquareSize;
    private float mCircleX, mCircleY;
    private float mCircleRadius = 100f;

    private boolean mStatus = false;
    private float mSymbolX, mSymbolY;
    private Paint mPaintSymbol;

    private Bitmap mImage;

    public CustomView(Context context) {
        super(context);

        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set ){
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

    public void setSymbol(Context context, String filePath){
        mImage = getBitmapFromAsset(context, filePath);

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.RED);

        mRectSquare.left = 50;
        mRectSquare.top = 50;
        mRectSquare.right = mRectSquare.left + mSquareSize;
        mRectSquare.bottom =mRectSquare.top + mSquareSize;

//        canvas.drawRect(mRectSquare, mPaintSquare);

        if(mCircleX == 0f || mCircleY == 0f) {
            mCircleX = getWidth() / 2.0f;
            mCircleY = getHeight() / 2.0f;
        }
//        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mPaintCircle);

        float imageX = mSymbolX - mImage.getWidth() / 2.0f;
        float imageY = mSymbolY - mImage.getHeight() / 2.0f;

        canvas.drawBitmap(mImage, imageX, imageY, mPaintSymbol);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        if(mStatus) return value;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();

                setSymbol( getContext(), "symbols/symbol_0.png");
                mSymbolX = x;
                mSymbolY = y;

                postInvalidate();

                return true;
            }

            case MotionEvent.ACTION_MOVE: {

                float x = event.getX();
                float y = event.getY();

                double dx = Math.abs(x - mSymbolX);
                double dy = Math.abs(y - mSymbolY);

                if(dx < mImage.getWidth() && dy < mImage.getHeight()) {

                    mSymbolX = x;
                    mSymbolY = y;

                    postInvalidate();

                    return true;
                } else {
//                    mStatus = true;
                }

                return value;
            }
        }

        return value;
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int reqWidth, int reqHeight){
        Matrix matrix = new Matrix();

        RectF src = new RectF(mSymbolX, mSymbolY, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(mSymbolX, mSymbolY, reqWidth, reqHeight);

        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(bitmap, (int)mSymbolX, (int)mSymbolY, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}

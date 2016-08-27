package com.portfolio.archit.weathermaster.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.portfolio.archit.weathermaster.R;

/**
 * Created by archit.shah on 4/27/2016.
 */
public class CustomCompassView extends View {

    private Context mContext;
    private Paint mTextPaint;
    private float mTextHeight, mTextWidth;
    private float mTextSize = 15, mStrokeWidth = 8;
    private static final int minHeight = 50, minWidth = 50;
    private float mHeight, mWidth, mainRingRadius = 10;
    private int centerX, centerY;
    private String mAccessibilityDescription = "";

    private int currentAngle = 0;
    private Paint mRings, mPivot, mRedNeedle, mBlueNeedle;

    private ShapeDrawable mDrawable;

    public CustomCompassView(Context context) {
        super(context);
        initView(context, null);
    }

    public CustomCompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomCompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomCompassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context mContext, AttributeSet attrs) {


        AccessibilityManager accessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomCompassView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomCompassView_textsize:
                    mTextSize = a.getDimensionPixelSize(attr, (int) mTextSize);
                    break;
                case R.styleable.CustomCompassView_ring_stroke_width:
                    mStrokeWidth = a.getInt(attr, (int) mStrokeWidth);
                    break;
            }
        }
        a.recycle();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        mRings = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRings.setStyle(Paint.Style.STROKE);
        mRings.setStrokeWidth(mStrokeWidth);
        mRings.setColor(Color.parseColor("#795548"));

        mPivot = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPivot.setStyle(Paint.Style.STROKE);
        mPivot.setStrokeWidth(mStrokeWidth);
        mPivot.setColor(Color.parseColor("#ffab00"));

        mRedNeedle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedNeedle.setStyle(Paint.Style.STROKE);
        mRedNeedle.setStrokeWidth(mStrokeWidth);
        mRedNeedle.setColor(Color.parseColor("#f44336"));

        mBlueNeedle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlueNeedle.setStyle(Paint.Style.STROKE);
        mBlueNeedle.setStrokeWidth(mStrokeWidth);
        mBlueNeedle.setColor(Color.parseColor("#3f51b5"));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) - (int) mTextWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int) mTextWidth, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Resources res = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, res.getDisplayMetrics());

        int w = getWidth();
        int h = getHeight();

        centerX = w / 2;
        centerY = h / 2;

        if (h <= w) {
            mWidth = h / 2;
            mHeight = h / 2;
            mainRingRadius = (h / 2) - 10;

        } else {
            mWidth = w / 2;
            mHeight = w / 2;
            mainRingRadius = (w / 2) - 10;
        }

        canvas.drawCircle(centerX, centerY, mainRingRadius, mRings);
//        canvas.drawCircle(centerX, centerY, mainRingRadius - 20, mRings);
//        canvas.drawCircle(centerX, centerY, mainRingRadius - 40, mRings);
        canvas.drawCircle(centerX, centerY, mainRingRadius - px, mRings);

        int needleRadi = (int) (mainRingRadius / 10);

        float needleX1 = (float) (centerX + (needleRadi * Math.cos(Math.toRadians(currentAngle - 90))));
        float needleY1 = (float) (centerY + (needleRadi * Math.sin(Math.toRadians(currentAngle - 90))));

        float needleX2 = (float) (centerX + (mainRingRadius * Math.cos(Math.toRadians(currentAngle))));
        float needleY2 = (float) (centerY + (mainRingRadius * Math.sin(Math.toRadians(currentAngle))));

        float needleX3 = (float) (centerX + (needleRadi * Math.cos(Math.toRadians(currentAngle + 90))));
        float needleY3 = (float) (centerY + (needleRadi * Math.sin(Math.toRadians(currentAngle + 90))));

        float needleX4 = (float) (centerX + (mainRingRadius * Math.cos(Math.toRadians(currentAngle + 180))));
        float needleY4 = (float) (centerY + (mainRingRadius * Math.sin(Math.toRadians(currentAngle + 180))));

        canvas.drawLine(needleX1, needleY1, needleX2, needleY2, mRedNeedle);
        canvas.drawLine(needleX2, needleY2, needleX3, needleY3, mRedNeedle);
        canvas.drawLine(needleX3, needleY3, needleX4, needleY4, mBlueNeedle);
        canvas.drawLine(needleX4, needleY4, needleX1, needleY1, mBlueNeedle);

        canvas.drawCircle(centerX, centerY, needleRadi, mPivot);

        float textRadi = mainRingRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, res.getDisplayMetrics());
        ;

        int textCentreY = centerY;

        Rect r = new Rect();
        mTextPaint.getTextBounds("W", 0, "W".length(), r);
        textCentreY += (Math.abs(r.height())) / 2;

        canvas.drawText("E", (float) (centerX + (textRadi * Math.cos(Math.toRadians(0)))), (float) (textCentreY + (textRadi * Math.sin(Math.toRadians(0)))), mTextPaint);
        canvas.drawText("N", (float) (centerX + (textRadi * Math.cos(Math.toRadians(90)))), (float) (textCentreY + (textRadi * Math.sin(Math.toRadians(90)))), mTextPaint);
        canvas.drawText("W", (float) (centerX + (textRadi * Math.cos(Math.toRadians(180)))), (float) (textCentreY + (textRadi * Math.sin(Math.toRadians(180)))), mTextPaint);
        canvas.drawText("S", (float) (centerX + (textRadi * Math.cos(Math.toRadians(270)))), (float) (textCentreY + (textRadi * Math.sin(Math.toRadians(270)))), mTextPaint);

    }

    public void setCompassAngle(int angle) {
        currentAngle = angle;
        invalidate();
    }

    public void setmAccessibilityDescription(String description) {
        mAccessibilityDescription = description;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        this.setContentDescription(mAccessibilityDescription);
        return true;
    }
}

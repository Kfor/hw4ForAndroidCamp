package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        size = Math.min(widthWithoutPadding, heightWithoutPadding);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = Math.min(getWidth(), getHeight());

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
            postInvalidateDelayed(1000);
        } else {
            drawNumbers(canvas);
            postInvalidateDelayed(1000);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        //String timeZone ="GMT"+ TimeZone.getDefault().getRawOffset() / (3600 * 1000);
        //Log.d("Clock",timeZone);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(80);
        textPaint.setColor(hoursValuesColor);
        textPaint.setAntiAlias(true);

        for(int i = 0;i < FULL_ANGLE;i+=30) {
            int hour = (FULL_ANGLE + 90 - i)/30 ;//因为默认的0度在正右且逆时针，故这里做个转换
            String text = (hour==0?12:(hour>12?hour-12:hour))+"";//转换后调整数字大小
            Rect textBound = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), textBound);

            //Log.d("Clock",textBound.width()+"");
            int rPadded = mCenterX - (int) (mWidth * 0.05f)  - 80;//80是为缩小半径设的值，即刻度内侧半径再缩短80
            canvas.drawText(text, (float)(mCenterX + rPadded * Math.cos(Math.toRadians(i))-textBound.width()/2 ),//通过textBound来实现相对刻度位置统一
                    (float)(mCenterX - rPadded * Math.sin(Math.toRadians(i))+textBound.height()/2),
                    textPaint);
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));

        final int hourLength = 150;
        final int minuteLength = 200;
        final int secondLength = 300;

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        double angleHour = 180 - (hour + (float) minute / 60) * 360 / 12;//根据时间来计算当前偏转的角度。180-是因为坐标角度和时钟角度的转换
        double angleMinute = 180 - (minute + (float) second / 60) * 360 / 60;
        double angleSecond = 180 - second * 360 / 60;

        Paint paintHour = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintHour.setStyle(Paint.Style.FILL_AND_STROKE);
        paintHour.setStrokeCap(Paint.Cap.ROUND);
        paintHour.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paintHour.setColor(hoursNeedleColor);

        canvas.drawLine(mCenterX,mCenterY,mCenterX+(float)(hourLength*Math.sin(Math.toRadians(angleHour))),
                mCenterY+(float)(hourLength*Math.cos(Math.toRadians(angleHour))),paintHour);

        Paint paintMinute = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMinute.setStyle(Paint.Style.FILL_AND_STROKE);
        paintMinute.setStrokeCap(Paint.Cap.ROUND);
        paintMinute.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paintMinute.setColor(minutesNeedleColor);

        canvas.drawLine(mCenterX,mCenterY,mCenterX+(float)(minuteLength*Math.sin(Math.toRadians(angleMinute))),
                mCenterY+(float)(minuteLength*Math.cos(Math.toRadians(angleMinute))),paintMinute);

        Paint paintSecond = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSecond.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSecond.setStrokeCap(Paint.Cap.ROUND);
        paintSecond.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paintSecond.setColor(secondsNeedleColor);

        canvas.drawLine(mCenterX,mCenterY,mCenterX+(float)(secondLength*Math.sin(Math.toRadians(angleSecond))),
                mCenterY+(float)(secondLength*Math.cos(Math.toRadians(angleSecond))),paintSecond);


    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);//注意后会覆盖前
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        paint1.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint1.setColor(centerOuterColor);
        canvas.drawCircle(mCenterX,mCenterY,(float)12,paint1);

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint2.setColor(centerInnerColor);
        canvas.drawCircle(mCenterX,mCenterY,(float)5,paint2);

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}
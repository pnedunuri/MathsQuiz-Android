package edu.sjsu.cmpe277.org.mathsquizcmpe277;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by pnedunuri on 9/30/16.
 */

public class TimerView extends View implements Runnable {

    private final int CIRCLE_DIAMETER = 120;
    private Bitmap mProgressBitmap;
    private Bitmap mMaskProgressBitmap;
    private Bitmap mResultBitmap;

    private Canvas mResultCanvas;
    private Canvas mMaskCanvas;
    private Canvas mProgressCanvas;

    private Paint mPaint;

    private Handler mHandler = new Handler();

    private final long BREAK_TIME = 1000;
    private static final long FRAME_TIME = 50;

    private float arcLeft = 0;
    private float arcTop = 0;
    private float arcRight = 0;
    private float arcBottom = 0;
    private float arcWidth = 0;
    private float arcHeight = 0;

    public static volatile TimerView timerView = null;

    private boolean isInitialized = false;

    public static volatile long startTimeMillis = -1;

    private float startAngle = 0;
    private float sweepAngle = 0;

    public boolean timeOut = false;
    public boolean answered = false;
    private boolean clearScreen = false;
    private long breakTimeStarted = 0;
    private boolean isBreakTime = false;

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        // init timer
        startTimeMillis = -1;
        clearScreen = timeOut = answered = false;
        startAngle = sweepAngle = 0;

        // if the screen is not initialized return
        if (arcWidth == 0 || arcHeight == 0) {
            arcWidth = this.getWidth();
            arcHeight = this.getHeight();

            arcLeft = arcWidth - (CIRCLE_DIAMETER << 1);
            arcRight = arcLeft + CIRCLE_DIAMETER;
            arcTop = arcHeight - CIRCLE_DIAMETER;
            arcBottom = arcTop + CIRCLE_DIAMETER;
        }

        mProgressBitmap = Bitmap.createBitmap((int) arcWidth, (int) arcHeight, Bitmap.Config.ARGB_8888);
        mProgressCanvas = new Canvas(mProgressBitmap);

        mMaskProgressBitmap = Bitmap.createBitmap((int) arcWidth, (int) arcHeight, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskProgressBitmap);

        mResultBitmap = Bitmap.createBitmap((int) arcWidth, (int) arcHeight, Bitmap.Config.ARGB_8888);
        mResultCanvas = new Canvas(mResultBitmap);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getContext().getResources().getColor((R.color.green)));

        mHandler.postDelayed(this, FRAME_TIME);

        isInitialized = true;
    }

    public boolean isTimeUp() {
        return timeOut;
    }

    public void clearALL() {
        clearScreen = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized) {
            init();

            return;
        }

        if (clearScreen){
            RectF rectF = new RectF(arcLeft, arcTop, arcRight, arcBottom);
            mProgressBitmap.eraseColor(Color.TRANSPARENT);
            Paint white = new Paint();
            white.setColor(Color.WHITE);
            mProgressCanvas.drawArc(rectF, -90, 360, true, white);

            clearScreen = false;
        }
        mResultCanvas.drawBitmap(mProgressBitmap, 0, 0, null);

        canvas.drawBitmap(mResultBitmap, 0, 0, null);
    }

    @Override
    public void run() {
        timerView = this;

        // check if answered
        if (QuesNAnsFrag.quesNAnsFrag != null) {
            // nothing to do here if game's up
            if (QuesNAnsFrag.quesNAnsFrag.gameUp) {
                return;
            }

            QuesNAnsFrag.quesNAnsFrag.checkAnswer();

            QuesNAnsFrag.quesNAnsFrag.timeOut = timeOut;
            QuesNAnsFrag.quesNAnsFrag.answered = answered;
            QuesNAnsFrag.quesNAnsFrag.startTimeMillis = startTimeMillis;
        }

        // no more rendering on time out
        if (!isInitialized || mProgressBitmap == null || QuesNAnsFrag.quesNAnsFrag == null) {
            repeatLoop();

            return;
        }

        if (isBreakTime) {
            if ((System.currentTimeMillis() - breakTimeStarted) > BREAK_TIME) {
                QuesNAnsFrag.quesNAnsFrag.goToNextQuestion();
                releaseAll();
            }

            repeatLoop();
            return;
        }

        if (timeOut || answered) {
            isBreakTime = true;
            breakTimeStarted = System.currentTimeMillis();

            QuesNAnsFrag.quesNAnsFrag.showResult();

            repeatLoop();
            return;
        }

        RectF rectF = new RectF(arcLeft, arcTop, arcRight, arcBottom);

        if (startTimeMillis == -1) {
            startTimeMillis = System.currentTimeMillis() - QuesNAnsFrag.quesNAnsFrag.timeDiff;
        } else {
            long currTimeMillis = System.currentTimeMillis();

            float timeDiff = Math.abs(currTimeMillis - startTimeMillis);
            float diff = (float) (timeDiff * 0.072);
            int timeInSecs = (int) (timeDiff / 1000);
            // reverse countdown
            timeInSecs -= 5;
            timeInSecs = Math.abs(timeInSecs);
            timeOut = (timeInSecs == 0);

            startAngle = -90;
            sweepAngle = diff;

            mProgressBitmap.eraseColor(Color.TRANSPARENT);
            Paint white = new Paint();
            white.setColor(Color.WHITE);
            mProgressCanvas.drawArc(rectF, -90, 360, true, white);

            int timerColor = getContext().getResources().getColor((R.color.greenYellow));
            if (timeInSecs <= 1) {
                timerColor = getContext().getResources().getColor((R.color.red));
            } else if (timeInSecs <= 2) {
                timerColor = getContext().getResources().getColor((R.color.orange));
            } else if (timeInSecs <= 3) {
                timerColor = getContext().getResources().getColor((R.color.yellow));
            }
            mPaint.setColor(timerColor);

            mProgressCanvas.drawArc(rectF, startAngle, sweepAngle, true, mPaint);

            Paint fontColor = new Paint();
            fontColor.setColor(Color.BLUE);
            fontColor.setTypeface(Typeface.DEFAULT_BOLD);
            fontColor.setTextSize(60);

            int horPadding = -15;
            int verPadding = 17;
            mProgressCanvas.drawText(timeInSecs + "", arcLeft + (CIRCLE_DIAMETER >> 1) + horPadding, arcTop + (CIRCLE_DIAMETER >> 1) + verPadding, fontColor);
        }

        repeatLoop();
    }

    private void repeatLoop() {
        this.invalidate();

        mHandler.postDelayed(this, FRAME_TIME);
    }

    public void releaseAll() {
        // init timer
        startTimeMillis = -1;
        QuesNAnsFrag.quesNAnsFrag.timeDiff = 0;
        isBreakTime = isInitialized = clearScreen = timeOut = answered = false;
        startAngle = sweepAngle = 0;

        if (mProgressBitmap != null)
        {
            mProgressBitmap.recycle();
            mMaskProgressBitmap = null;
        }

        if (mResultBitmap != null)
        {
            mResultBitmap.recycle();
            mResultBitmap = null;
        }

        if (mMaskProgressBitmap != null)
        {
            mMaskProgressBitmap.recycle();
            mMaskProgressBitmap = null;
        }
    }
}
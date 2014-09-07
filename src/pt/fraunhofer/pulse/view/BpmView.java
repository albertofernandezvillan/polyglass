package pt.fraunhofer.pulse.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

public class BpmView extends TextView {

    public BpmView(Context context) {
        super(context);
        init();
    }

    public BpmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BpmView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private double bpm;

    private Paint circlePaint;
    private ValueAnimator circlePaintAnimator;

    private void init() {
        setNoBpm();

        this.setAlpha(255);
        setTextSize(TypedValue.COMPLEX_UNIT_FRACTION_PARENT, 60f);
        //setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/ds_digital/DS-DIGIB.TTF"));
        setGravity(Gravity.CENTER);
        setTextColor(Color.argb(0, 255, 0, 0));

        circlePaint = initCirclePaint();

        circlePaintAnimator = ObjectAnimator.ofInt(circlePaint, "Alpha", 0, 256);
        circlePaintAnimator.setInterpolator(new AccelerateInterpolator());
        circlePaintAnimator.setDuration(1000);
        circlePaintAnimator.setRepeatCount(ValueAnimator.INFINITE);
        circlePaintAnimator.setRepeatMode(ValueAnimator.REVERSE);
        circlePaintAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if (getText() == "-" && circlePaint.getAlpha() == 0) {
                    animation.cancel();
                }
            }
        });
        circlePaintAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }

    private Paint initCirclePaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);
        p.setAlpha(0);
        p.setStyle(Paint.Style.FILL);
        return p;
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
        this.setAlpha(0);
        setTextSize(TypedValue.COMPLEX_UNIT_FRACTION_PARENT, 60f);
        //setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/ds_digital/DS-DIGIB.TTF"));
        setGravity(Gravity.CENTER);
        //int color = Integer.parseInt("FF0000", 16)+0xFF000000;
        setTextColor(0xFF000000);
        long rounded = Math.round(bpm);
        if (rounded == 0) {
            setText("NO BPM AVAILABLE");
        } else {
            setText("BPM: "+String.valueOf(rounded));
        }
    }

    public void setNoBpm() {
        setBpm(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(20, getHeight() / 2f, 10, circlePaint);
        if (getText() != "-" && !circlePaintAnimator.isStarted()) {
            circlePaintAnimator.start();
        }
    }

}

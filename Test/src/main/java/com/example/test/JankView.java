package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

public class JankView extends androidx.appcompat.widget.AppCompatTextView {
    private boolean jank;
    public JankView(Context context) {
        super(context);
    }

    public JankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        long start = SystemClock.elapsedRealtime();
        if(jank){
            SystemClock.sleep(500);
        }
        long end = SystemClock.elapsedRealtime();
        Log.d("JankView","BlockMonitor start "+start + "  end "+end + "   "+(end - start));
    }

    private int jankCount;
    public void setJank(boolean jank) {
        this.jank = jank;
        setText("jankCount "+jankCount++);
        requestLayout();
    }
}

package com.mine.overlaysampleapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MovingOverlayLayout extends FrameLayout {
    private final String TAG = "MovingOverLayout";
    public MovingOverlayLayout(@NonNull Context context) {
        super(context);
    }

    public MovingOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MovingOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MovingOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Normal event dispatch to this container's children, ignore the return value
        super.dispatchTouchEvent(ev);

        Log.d(TAG,"[dispatchTouchEvent]");
        // Always consume the event so it is not dispatched further up the chain
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        super.onInterceptTouchEvent(ev);

        Log.d(TAG,"[onInterceptTouchEvent]");
        return false;
    }


}

package com.mine.overlaysampleapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class ScreenOffReceiver extends BroadcastReceiver {
    private final String TAG = "ScreenOffReceiver";
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private View view;

    public ScreenOffReceiver(WindowManager wm, View v){
        windowManager = wm;
        view = v;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            if (MainActivity.isShowing) return;
            Log.v(TAG, "[onReceive] : "+intent.getAction());
            // Layoutのparam設定
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // API 26 or later
//                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,         // API 25 or before
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                        PixelFormat.TRANSLUCENT);
            } else {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // API 26 or later
                        // API 25 or before
//                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        ,
                        PixelFormat.TRANSLUCENT);
            }
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            windowManager.addView(view, params);
            MainActivity.isShowing = true;

        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            if(!MainActivity.isShowing) return;
            windowManager.removeView(view);
            MainActivity.isShowing = false;
        }
        Log.d(TAG, "[onReceive] : "+ intent.getAction());
    }
}
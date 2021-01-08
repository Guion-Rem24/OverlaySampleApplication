package com.mine.overlaysampleapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Array;

public class OverlayService extends Service{//implements View.OnTouchListener, View.OnClickListener {
    private static WindowManager windowManager;
    private final String TAG="OverlayService";
    private static View view;
    private static View root_view;
    private ScreenOffReceiver mReceiver = null;
    private static WindowManager.LayoutParams params;
    private static Point displaySize;
    public OverlayService() {
    }

    //
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public int onStartCommand(Intent intent, int flag, int startId){

        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        displaySize = new Point();
        windowManager.getDefaultDisplay().getSize(displaySize);

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
//                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
//                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                              | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    ,
                    PixelFormat.TRANSLUCENT);
        }

        LayoutInflater inflater = LayoutInflater.from(this);

//        params.gravity = Gravity.END | Gravity.TOP;
        params.gravity = Gravity.TOP | Gravity.START;

        root_view = inflater.inflate(R.layout.overlay_layout, null);
        view = root_view.findViewById(R.id.overlay_layout);
        Button overlayButton = (Button) view.findViewById(R.id.button_overlay);
        Log.d("debug", "overlayButton := ("+overlayButton.getBottom()+","+overlayButton.getRight()+")");
        if(overlayButton != null){
            overlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debug", "onClick");
//                stopSelf();
                    onDestroy();
                }
            });
        }

        if(!view.isAttachedToWindow()){
            Log.d("debug", "view is not attached");
        }

        root_view.setOnTouchListener( new OnFrameMovingListener(root_view) );
        Log.d("debug", "root_view := ("+root_view.getWidth()+","+root_view.getHeight()+")");
        Log.d(TAG,"-- "+Gravity.TOP+" -- "+Gravity.END); //  -- 48 -- 8388613

        root_view.post(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "root_view := ("+root_view.getLeft()+","+root_view.getTop()+")");
                int loc[] = new int[2];
                root_view.getLocationInWindow(loc);
                Log.d("debug", "LocationInWindow:= ("+loc[0]+","+loc[1]+")");
                root_view.getLocationOnScreen(loc);
                Log.d("debug", "LocationOnScreen:= ("+loc[0]+","+loc[1]+")");
                Log.d("debug", "display size := ("+displaySize.x+","+displaySize.y+")");

            }
        });
        view.post(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "view := ("+view.findViewById(R.id.text_overlay).getWidth()+","+view.findViewById(R.id.text_overlay).getHeight()+")");
            }
        });

//        root_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout grandParent = (RelativeLayout) v.getParent().getParent();
//                grandParent.callOnClick();
//            }
//        });
//        root_view.setOnTouchListener(new OnMovingListener());

        // ViewにTouchListenerを設定する ///////////////
        // API 25 以前の場合，TouchListenerが働いていない．
//        view.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("debug","onTouch");
//                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    Log.d("debug","ACTION_DOWN");
//                }
//                if(event.getAction() == MotionEvent.ACTION_UP){
//                    Log.d("debug","ACTION_UP");
//
//                    // warning: override performClick()
//                    view.performClick();
//
//                    // Serviceを自ら停止させる
////                    stopSelf();
//                    onDestroy();
//                    return true;
//                }
//                return false;
//            }
////            @Override
////            public void onTouchEvent(){}
//        });
//        view.setOnTouchListener(new OnMovingListener());

        /* receiverの登録　//////////////////////////// */
//        mReceiver = new ScreenOffReceiver(windowManager, view);
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(mReceiver, filter);



        // WindowManagerによるViewの追加 ///////////////
        windowManager.addView(root_view, params);

//        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);

        return super.onStartCommand(intent, flag, startId);
    }




    @Override
    public void onDestroy(){
        // Viewの削除 /////////////////////////////////
        if(root_view != null) {
            windowManager.removeView(root_view);
            root_view = null;
        }

        // Receiverの登録解除 //////////////////////////
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    // moving listener
    // https://akira-watson.com/android/imageview-drag.html
    private static
    class OnMovingListener implements View.OnTouchListener {
        private int preDx, preDy;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // x,y 位置取得
            int newDx = (int)event.getRawX();
            int newDy = (int)event.getRawY();

            switch (event.getAction()) {
                // タッチダウンでdragされた
                case MotionEvent.ACTION_MOVE:
                    // ACTION_MOVEでの位置
                    // performCheckを入れろと警告が出るので
                    v.performClick();
                    int dx = v.getLeft() + (newDx - preDx);
                    int dy = v.getTop() + (newDy - preDy);
                    int imgW = dx + v.getWidth();
                    int imgH = dy + v.getHeight();

                    // 画像の位置を設定する
                    v.layout(dx, dy, imgW, imgH);

                    String str = "dx="+dx+"\ndy="+dy;
//                    v.setText(str);
                    Log.d("onTouch","ACTION_MOVE: dx="+dx+", dy="+dy);
                    break;
                case MotionEvent.ACTION_DOWN:
                    // nothing to do
                    break;
                case MotionEvent.ACTION_UP:
                    // nothing to do
                    break;
                default:
                    break;
            }

            // タッチした位置を古い位置とする
            preDx = newDx;
            preDy = newDy;

            return true;
        }
    }

    private static
    class OnFrameMovingListener implements View.OnTouchListener {
        private int X, Y;
        private int left, top;
        private int Dx, Dy;
        private int dx=0, dy=0;
//        private final int[] viewWidth = new int[1];
//        private final int[] viewHeight = new int[1];
        private int viewWidth;
        private int viewHeight;
        private Point mWindowPos = new Point(0, 0); //最初に表示される位置
        private Point mTouchPoint = new Point();  //タッチされる位置

        public OnFrameMovingListener(View v){
            X = 0; Y = 0;
//            int loc[] = new int[2];
//            v.getLocationInWindow(loc);
//            Dx = loc[0]; Dy = loc[1];
//            Log.d("OnFrameMovingLayout", "Dx,Dy="+Dx+","+Dy);
            v.post(new Runnable() {
                @Override
                public void run() {
                    viewWidth = v.getWidth();
                    viewHeight = v.getHeight();
                }
            });
            final int layoutDirection = v.getLayoutDirection();
            final int absoluteGravity = Gravity.getAbsoluteGravity(params.gravity, layoutDirection);
        final int horizontalGravity = params.gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
            final int verticalGravity = params.gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (horizontalGravity) {
                case Gravity.END:
                {
                    dx = displaySize.x - viewWidth;
                    Log.d("test", "end");
                    break;
                }
                case Gravity.CENTER_HORIZONTAL:
                {
                    dx = (displaySize.x - viewWidth) / 2;
                    Log.d("test", "center_horizontal");
                    break;
                }
                case Gravity.START:
                {
                    Log.d("test", "start");
                    break;
                }
                default:
//                throw new IllegalStateException("Unexpected value: " + horizontalGravity);
            }

            switch (verticalGravity) {
                case Gravity.TOP:
                    Log.d("test", "top");
                    break;
                case Gravity.CENTER_VERTICAL:
                    Log.d("test", "center_vertical");
                    dy = (displaySize.y-viewHeight)/2;
                    break;
                case Gravity.BOTTOM:
                    Log.d("test", "bottom");
                    dy = displaySize.y - viewHeight;
                    break;
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // x,y 位置取得
            int x = (int)event.getRawX();
            int y = (int)event.getRawY();

            switch (event.getAction()) {
                // タッチダウンでdragされた
                case MotionEvent.ACTION_MOVE:
                    Log.d("debug", "("+X+","+Y+") → ("+x+","+y+")");
                    // ACTION_MOVEでの位置
                    // performCheckを入れろと警告が出るので
//                    v.performClick();
////                    int dx = x - X;
////                    int dy = y - Y;
//                    // TODO:動きがキモい
////                    params.x = dx + left - Dx;
////                    params.y = dy + top - Dy;
////                    X = x;
////                    Y = y;
////
////                    left = params.x;
////                    top = params.y;
////                    windowManager.updateViewLayout(root_view, params);
////                    v.setText(str);
//                    WindowManager.LayoutParams[] p = new WindowManager.LayoutParams[1];
//                    p[0] = params;
//                    convertCoordinateFrom(p, event, dx, dy);
//                    windowManager.updateViewLayout(root_view, p[0]);
//
////                    convertCoordinateFrom(params, event, v);
////                    windowManager.updateViewLayout(root_view,params);
//                    break;
                    WindowManager.LayoutParams[] p = new WindowManager.LayoutParams[1];
                    p[0] = params;
                    //タッチ位置から描画位置の算出
                    mWindowPos.x = x - mTouchPoint.x;
                    mWindowPos.y = y - mTouchPoint.y;
                    p[0].x = mWindowPos.x;
                    p[0].y = mWindowPos.y;
                    windowManager.updateViewLayout(root_view, p[0]);
                    break;
                case MotionEvent.ACTION_DOWN:
                    // nothing to do
//                    Log.d("debug", "("+X+","+Y+") → ("+x+","+y+")");
                    break;
                case MotionEvent.ACTION_UP:
                    // nothing to do
                    break;
                default:
                    break;
            }

            return true;
        }
    }

    private static void convertCoordinateFrom(WindowManager.LayoutParams[] params, MotionEvent event, int dx, int dy){
        final int[] viewWidth = new int[1];
        final int[] viewHeight = new int[1];
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        int X = params[0].x;
        int Y = params[0].y;

        params[0].x = x - ( - dx + X);
        params[0].y = y - ( - dy + Y);

    }
}
package com.mine.overlaysampleapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

// TODO: なぜかServiceが動いていると，背景が透過される．

public class MainActivity extends AppCompatActivity {
    private final int CODE_REQUEST_OVERLAY_PERMISSION = 1000;
    private final String TAG = "MainActivity";
    private ScreenOffReceiver receiver;
    private KeyguardManager keyguardManager;
    private boolean wasLocked = false;
    public static boolean isShowing = false;

    public MainActivity getPointer(){ return this; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "[onCreate]");

        /**
         * 参考URL
         * https://stackoverflow.com/questions/48277302/android-o-flag-show-when-locked-is-deprecated
         */
        // keyguardをdismissにすることで，activityが動いている時にはlock screenにはならない
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
//            setShowWhenLocked(true);
//            setTurnScreenOn(true);
//            keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//            keyguardManager.requestDismissKeyguard(this, null);
//        } else {
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//            );
//        }


//        receiver = new ScreenOffReceiver();

        Button startButton = (Button) findViewById(R.id.button_start);
        Button endButton = (Button) findViewById(R.id.button_end);
        endButton.getParent().requestDisallowInterceptTouchEvent(true);
        startButton.getParent().requestDisallowInterceptTouchEvent(true);

        if(!isGrantedPermission()){
            requestOverlayPermission();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"[onCLick] : Start");
                Intent intent = new Intent(getApplication(), OverlayService.class);
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplication());
                manager.sendBroadcast(intent);
                getPointer().startService(intent);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"[onCLick] : Stop");
                Intent intent = new Intent(getApplication(), OverlayService.class);
                getPointer().stopService(intent);
            }
        });
//        endButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_BUTTON_PRESS:
//                        v.performClick();
//                        Log.d(TAG,"[onCLick] : Stop");
//                        Intent intent = new Intent(getApplication(), OverlayService.class);
//                        getPointer().stopService(intent);
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//
//                }
//                return false;
//            }
//        });
    }


    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG,"[onPause]");
        // Receiverで判定を受け取る．
        /*
        if(receiver.catch()){
            wasLocked = true;
        }
         */
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG,"[onStart]");

        if(wasLocked){
            // 以下を参考に，背景を追加する
            // screen off後，今まで見えていたホーム画面が見えなくなる．
            // https://source.android.com/devices/tech/display/multi_display/system-decorations?hl=ja
            wasLocked = false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG,"[onResume]");

    }



    private boolean isGrantedPermission(){
        return Settings.canDrawOverlays(this);
    }

    private void requestOverlayPermission(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent,CODE_REQUEST_OVERLAY_PERMISSION);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODE_REQUEST_OVERLAY_PERMISSION){
            if(!isGrantedPermission()){
                Toast.makeText(getApplicationContext(), "Permission must be granted...", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}
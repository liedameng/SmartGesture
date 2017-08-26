package com.boway.smartgesture.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import android.text.TextUtils;
import android.util.Log;

import com.boway.smartgesture.R;
import com.boway.smartgesture.settings.Utils;
import com.boway.smartgesture.settings.IncomingCallListener;
import com.boway.smartgesture.settings.ShakeDetector;
import com.boway.smartgesture.settings.FaceDownDetector;
import com.boway.smartgesture.settings.PickUpDetector;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.IllegalArgumentException;



public class SensorEventListenerService extends Service implements IncomingCallListener.Callback,
    ShakeDetector.OnShakeListener, FaceDownDetector.OnFaceDownListener, PickUpDetector.OnPickUpListener {
    private static final String TAG = "SensorEventListenerService";

    private static final int MSG_ANSWER_CALL = 1;
    private static final int MSG_TURN_MUTE = 2;
    private static final int MSG_PICK_UP = 3;

    private boolean mIsRing;
    // @}
    // for UI update
    private MainHandler mMainHandler;
    private IncomingCallListener mIncomingCallListener;
    private ShakeDetector mShakeDetector;
    private FaceDownDetector mFaceDownDetector;
    private PickUpDetector mPickUpDetector;
    private TelephonyManager telephonyManager;
    // Add for event record string OOM

    class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_ANSWER_CALL:
                    acceptCall(SensorEventListenerService.this);

                break;
                case MSG_TURN_MUTE:
                    turnMute(SensorEventListenerService.this);
                break;

                case MSG_PICK_UP:
                    acceptCall(SensorEventListenerService.this);
                break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (Intent.ACTION_SHUTDOWN.equals(action)) {
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        if(!Utils.getSensorStatus(this, Utils.LOG_STATUS)){
            stopSelf();
            return;
        }
        super.onCreate();
        Log.d(TAG, "onCreate");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(mReceiver, filter);

        mMainHandler = new MainHandler();
        mIncomingCallListener = IncomingCallListener.getInstance();
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(mIncomingCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        mIncomingCallListener.registerListener(this);
        mShakeDetector = new ShakeDetector(this);
        mShakeDetector.registerOnShakeListener(this);
        mFaceDownDetector = new FaceDownDetector(this);
        mFaceDownDetector.registerOnFaceDownListener(this);
        mPickUpDetector = new PickUpDetector(this);
        mPickUpDetector.registerOnPickUpListener(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unRegisterAllSensors();
        if(mReceiver != null) {
            try{
               unregisterReceiver(mReceiver);
            }catch(IllegalArgumentException e){
            }
        }
        super.onDestroy();
    }

    private void unRegisterAllSensors() {
        if(mShakeDetector != null) {
            mShakeDetector.unregisterOnShakeListener(this);
        }
        if(mFaceDownDetector != null) {
            mFaceDownDetector.unregisterOnFaceDownListener(this);
        }
        if(mPickUpDetector != null) {
            mPickUpDetector.unregisterOnPickUpListener(this);
        }
    }


    private void startOrStopAllListener(boolean start){
        if(start) {
            if (Utils.getSensorStatus(this, Utils.KEY_SHAKE_TO_ANSWER)) getShakeDetector().start();
            if (Utils.getSensorStatus(this, Utils.KEY_FACE_DOWN)) getFaceDownDetector().start();
            if (Utils.getSensorStatus(this, Utils.KEY_PICK_UP))  getPickUpDetector().start();
        } else {
            getShakeDetector().stop();
            getFaceDownDetector().stop();
            getPickUpDetector().stop();
        }
    }

    @Override
    public void onCallStateChanged(int state) {
       Log.d(TAG, "onCallStateChanged:" + state);
        switch (state) {
        case TelephonyManager.CALL_STATE_IDLE:
        case TelephonyManager.CALL_STATE_OFFHOOK:
            mIsRing = false;
            break;
        case TelephonyManager.CALL_STATE_RINGING:
            mIsRing = true;
            break;
        }
        startOrStopAllListener(mIsRing);
    }

    @Override
    public void onShake() {
        Log.d(TAG, "onShake ");
        mMainHandler.sendEmptyMessage(MSG_ANSWER_CALL);
        getShakeDetector().stop();
    }

    @Override
    public void onFaceDown() {
        Log.d(TAG, "onFaceDown");
        mMainHandler.sendEmptyMessage(MSG_TURN_MUTE);
        getFaceDownDetector().stop();
    }

    @Override
    public void onPickUp() {
        Log.d(TAG, "onPickUp");
        mMainHandler.sendEmptyMessage(MSG_PICK_UP);
        getPickUpDetector().stop();
    }

    private ShakeDetector getShakeDetector() {
       if(mShakeDetector == null){
           mShakeDetector = new ShakeDetector(this);
           mShakeDetector.registerOnShakeListener(this);
       }
       return mShakeDetector;
    }

    private FaceDownDetector getFaceDownDetector() {
        if(mFaceDownDetector == null) {
            mFaceDownDetector = new FaceDownDetector(this);
            mFaceDownDetector.registerOnFaceDownListener(this);
        }
        return mFaceDownDetector;
    }

    private PickUpDetector getPickUpDetector() {
        if(mPickUpDetector == null) {
            mPickUpDetector = new PickUpDetector(this);
            mPickUpDetector.registerOnPickUpListener(this);
        }
        return mPickUpDetector;
    }

    public void acceptCall(Context context) {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (Exception e) {
        }
    }

    public void turnMute(Context context) {
        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null) {
            if (telecomManager.isRinging()) {
                telecomManager.silenceRinger();
            }
        }
    }

}


package com.boway.smartgesture.settings;  
import java.util.ArrayList;  
import android.content.Context;  
import android.hardware.Sensor;  
import android.hardware.SensorEvent;  
import android.hardware.SensorEventListener;  
import android.hardware.SensorManager;  
import android.util.FloatMath;  
import android.util.Log;
import com.boway.smartgesture.settings.Utils;

public class FaceDownDetector implements SensorEventListener {  
    static final int UPDATE_INTERVAL = 100;  
    long mLastUpdateTime;
    float x, y, z;
    Context mContext;  
    SensorManager mSensorManager;  
    ArrayList<OnFaceDownListener> mListeners;  

    public FaceDownDetector(Context context) {  
       mContext = context;  
       mSensorManager = Utils.getSensorManager(context);
       mListeners = new ArrayList<OnFaceDownListener>();  
    } 
 
    public interface OnFaceDownListener {  
        void onFaceDown();  
    }  

    public void registerOnFaceDownListener(OnFaceDownListener listener) {  
        if (mListeners.contains(listener))  
            return;  
        mListeners.add(listener);  
    }  

    public void unregisterOnFaceDownListener(OnFaceDownListener listener) {  
        mListeners.remove(listener);  
    }  
   
     public void start() {  
        if (mSensorManager == null) {  
            throw new UnsupportedOperationException();  
        } 
        isFirstTime = true; 
        Sensor sensor = mSensorManager  
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  
        if (sensor == null) {  
            throw new UnsupportedOperationException();  
        }  
        boolean success = mSensorManager.registerListener(this, sensor,  
                SensorManager.SENSOR_DELAY_GAME);  
         if (!success) {  
            throw new UnsupportedOperationException();  
        }  
    }  
    
    public void stop() {  
        if (mSensorManager != null)  
            mSensorManager.unregisterListener(this);  
    }  

    @Override  
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  
        // TODO Auto-generated method stub  
    }

    boolean isFirstTime;
    boolean mIsDown;

    @Override  
    public void onSensorChanged(SensorEvent event) {  
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL)
            return;
        mLastUpdateTime = currentTime;

         x = event.values[0];   
         y = event.values[1];   
         z = event.values[2];  
         if(isFirstTime) {
             mIsDown = z < 0 ? true : false;
             isFirstTime = false;
             return;
         }

         if (x < 2 && x > -2 && y < 2 && y > -2 && z < 0) {       
             if(!mIsDown) { 
                  notifyListeners();  
             }
             mIsDown = true;
         } else if (z > 0){
             mIsDown = false;
         }
    }  

    private void notifyListeners() {  
        for (OnFaceDownListener listener : mListeners) {  
            listener.onFaceDown();  
        }  
    }  
}  

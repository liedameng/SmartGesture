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

public class PickUpDetector implements SensorEventListener {  
    static final int UPDATE_INTERVAL = 100;  
    Context mContext;  
    long mLastUpdateTime;
    SensorManager mSensorManager;  
    ArrayList<OnPickUpListener> mListeners;  

    public PickUpDetector(Context context) {  
       mContext = context;  
       mSensorManager = Utils.getSensorManager(context);
       mListeners = new ArrayList<OnPickUpListener>();  
    } 
 
    public interface OnPickUpListener {  
        void onPickUp();  
    }  

    public void registerOnPickUpListener(OnPickUpListener listener) {  
        if (mListeners.contains(listener))  
            return;  
        mListeners.add(listener);  
    }  

    public void unregisterOnPickUpListener(OnPickUpListener listener) {  
        mListeners.remove(listener);  
    }  

    public void start() {  
        if (mSensorManager == null) {  
            throw new UnsupportedOperationException(); 
        } 
        isFirstTime = true; 
        mPickUp = false;
        Sensor sensor = mSensorManager  
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensor2 = mSensorManager
                .getDefaultSensor(Sensor.TYPE_PROXIMITY);  
        if (sensor == null || sensor2 == null) {  
            throw new UnsupportedOperationException();  
        }  
        boolean success = mSensorManager.registerListener(this, sensor,  
                SensorManager.SENSOR_DELAY_GAME) 
                && mSensorManager.registerListener(this, sensor2, SensorManager.SENSOR_DELAY_GAME);  
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
    boolean isClosing;
    boolean mPickUp;

    @Override  
    public void onSensorChanged(SensorEvent event) {  
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float x = event.values[0];  
            if(isFirstTime) {
                isClosing = x == 0.0f ? true : false;
                isFirstTime = false;
                return;
            } 
            boolean closed = x == 0.0f ? true : false;
            if(closed != isClosing) {
                isClosing = closed;
                mPickUp = closed;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - mLastUpdateTime;
            if (diffTime < UPDATE_INTERVAL)
                return;
            mLastUpdateTime = currentTime;

            if(event.values[1] > 7 && mPickUp){
                notifyListeners();
            }
        }
    }  

    private void notifyListeners() {  
        for (OnPickUpListener listener : mListeners) {  
            listener.onPickUp();  
        }  
    }  
}  

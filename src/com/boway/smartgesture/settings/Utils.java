package com.boway.smartgesture.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {

    private static final String TAG = "SmartGestureUtils";

    public static final String SHARED_PREF_SMART_GESTURE = "shared_smartgesture";
    public static final String KEY_ANSWER_CALL_STATUS = "answer_call_status";
    public static final String LOG_STATUS = "log_status";
    public static final String KEY_SHAKE_TO_ANSWER = "shake_to_answer";
    public static final String KEY_FACE_DOWN = "face_down";
    public static final String KEY_PICK_UP = "pick_up";
    private static SensorManager mSensorManager;

    public static SensorManager getSensorManager(Context context) {
        if(mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        return mSensorManager;
    }

    public static void setSensorStatus(Context context, String key, boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences(context, SHARED_PREF_SMART_GESTURE)
                .edit();
        editor.putBoolean(key, status);
        editor.apply();
    }

    public static boolean checkSensor(Context context, int type) {
        List<Sensor> list = getSensorManager(context).getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : list) {
            if (sensor.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean getSensorStatus(Context context,String sensorType) {
        return getSharedPreferences(context, SHARED_PREF_SMART_GESTURE).getBoolean(sensorType, false);
    }

    public static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}

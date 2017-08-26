package com.boway.smartgesture.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.os.IBinder;
import android.preference.PreferenceActivity;

import com.boway.smartgesture.service.SensorEventListenerService;
import com.boway.smartgesture.settings.Utils;

import java.util.HashMap;

public class BaseActivity extends PreferenceActivity {

    public void startOrStopService(boolean start) {
        Intent intent = new Intent(this, SensorEventListenerService.class);
        if (start) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }
}

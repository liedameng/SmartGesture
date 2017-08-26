package com.boway.smartgesture.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;

import com.boway.smartgesture.settings.Utils;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // bring the service up
        if(Utils.getSensorStatus(context, Utils.LOG_STATUS)) {
            intent.setClass(context, SensorEventListenerService.class);
            context.startService(intent);
        }
    }

}


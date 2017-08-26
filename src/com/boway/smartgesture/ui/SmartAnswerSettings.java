package com.boway.smartgesture.ui;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mediatek.sensorhub.Action;
import com.mediatek.sensorhub.ActionDataResult;
import com.mediatek.sensorhub.Condition;
import com.mediatek.sensorhub.ContextInfo;
import com.mediatek.sensorhub.DataCell;
import com.mediatek.sensorhub.SensorHubManager;
import com.boway.smartgesture.settings.Utils;
import com.boway.smartgesture.R;

import java.util.Arrays;

public class SmartAnswerSettings extends BaseActivity implements OnPreferenceChangeListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SmartAnswer";
    
    private static final String KEY_SHAKE_TO_ANSWER_PREF = "shake_to_answer_pref";
    private static final String KEY_FACE_DOWN_PREF = "face_down_pref";
    private static final String KEY_PICK_UP_PREF = "pick_up_pref";
    private int mRequestId;
    private Switch mActionBarSwitch;

    private SwitchPreference mShakeToAnswer;
    private SwitchPreference mFaceDown;
    private SwitchPreference mPickUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.smart_answer_settings);
        addSensorsList();
        initActionButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferenceStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initActionButton() {
        mActionBarSwitch = new Switch(getLayoutInflater().getContext());
        final int padding = getResources().getDimensionPixelSize(R.dimen.action_bar_switch_padding);
        mActionBarSwitch.setPaddingRelative(0, 0, padding, 0);
        getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar()
                .setCustomView(
                        mActionBarSwitch,
                        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                                        | Gravity.END));
        getActionBar().setDisplayHomeAsUpEnabled(false);
        mActionBarSwitch.setChecked(Utils.getSensorStatus(this, Utils.LOG_STATUS));
        mActionBarSwitch.setOnCheckedChangeListener(this);
    }

    private void addSensorsList() {
        mShakeToAnswer = (SwitchPreference) findPreference(KEY_SHAKE_TO_ANSWER_PREF);
        mFaceDown = (SwitchPreference) findPreference(KEY_FACE_DOWN_PREF);
        mPickUp = (SwitchPreference) findPreference(KEY_PICK_UP_PREF);

        mShakeToAnswer.setOnPreferenceChangeListener(this);
        mFaceDown.setOnPreferenceChangeListener(this);
        mPickUp.setOnPreferenceChangeListener(this);
    }

    private void updatePreferenceStatus() {
        updatePreferenceStatus(mShakeToAnswer, Utils.KEY_SHAKE_TO_ANSWER);
        updatePreferenceStatus(mFaceDown, Utils.KEY_FACE_DOWN);
        updatePreferenceStatus(mPickUp, Utils.KEY_PICK_UP);
    }

    private void updatePreferenceStatus(Preference pref, String statusKey) {
        boolean status = Utils.getSensorStatus(this, statusKey);
        boolean enabled = Utils.getSensorStatus(this, Utils.LOG_STATUS);
        boolean haveSensor =  Utils.checkSensor(this, Sensor.TYPE_ACCELEROMETER);
        if (pref != null) {
            if (pref instanceof SwitchPreference) {
                SwitchPreference prefer = (SwitchPreference) pref;
                prefer.setChecked(status);
                if(enabled && haveSensor) {
                    pref.setEnabled(true);
                    pref.setSummary("");
                } else {
                    pref.setEnabled(false);
                    if (haveSensor) {
                        pref.setSummary("");
                    } else {
                        pref.setSummary(R.string.no_sensor);
                    }
                }
            } else if (pref instanceof Preference) {
             //   pref.setSummary(status ? R.string.running_summary : R.string.space_summary);
            }
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean bNewValue = (Boolean) newValue;
        if (KEY_SHAKE_TO_ANSWER_PREF.equals(preference.getKey())) {
            Utils.setSensorStatus(this, Utils.KEY_SHAKE_TO_ANSWER, bNewValue);
        } else if (KEY_FACE_DOWN_PREF.equals(preference.getKey())) {
            Utils.setSensorStatus(this, Utils.KEY_FACE_DOWN, bNewValue);
        } else if (KEY_PICK_UP_PREF.equals(preference.getKey())) {
            Utils.setSensorStatus(this, Utils.KEY_PICK_UP, bNewValue);
        }
            
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
         Utils.setSensorStatus(this, Utils.LOG_STATUS, (Boolean) arg1);
         updatePreferenceStatus();
         super.startOrStopService(arg1);
    }
}

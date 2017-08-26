package com.boway.smartgesture.settings;

import android.telephony.PhoneStateListener;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class IncomingCallListener extends PhoneStateListener {
    private static IncomingCallListener sIncomingCallListener;
    private List<Callback> mCallbacks = new ArrayList<Callback>();

    public interface Callback {
        void onCallStateChanged(int state);
    }

    public synchronized static IncomingCallListener getInstance() {
        if (sIncomingCallListener == null) {
            sIncomingCallListener = new IncomingCallListener();
        }
        return sIncomingCallListener;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        for (Callback cb : mCallbacks) {
            cb.onCallStateChanged(state);
        }
    }

    public void registerListener(Callback listener) {
        mCallbacks.add(listener);
    }
}

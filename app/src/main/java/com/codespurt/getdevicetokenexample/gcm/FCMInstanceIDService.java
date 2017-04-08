package com.codespurt.getdevicetokenexample.gcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FCMInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = FCMInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        storeDeviceTokenInPref(refreshedToken);
        sendDeviceTokenToServer(refreshedToken);

        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendDeviceTokenToServer(final String refreshedToken) {
        Log.d(TAG, "sendDeviceTokenToServer: " + refreshedToken);
    }

    private void storeDeviceTokenInPref(String refreshedToken) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Config.DEVICE_TOKEN, refreshedToken);
        editor.apply();
    }
}
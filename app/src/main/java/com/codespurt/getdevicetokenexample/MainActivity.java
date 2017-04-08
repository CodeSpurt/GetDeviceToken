package com.codespurt.getdevicetokenexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.codespurt.getdevicetokenexample.gcm.Config;
import com.codespurt.getdevicetokenexample.utils.Alerts;
import com.codespurt.getdevicetokenexample.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver receiver;
    private TextView pushNotificationContent, deviceToken;
    private Alerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushNotificationContent = (TextView) findViewById(R.id.txt_push_notification_content);
        deviceToken = (TextView) findViewById(R.id.txt_device_token);

        alerts = new Alerts();
        if (alerts.isGooglePlayServicesAvailable(this)) {
            registerBroadcastReceiver();

            displayDeviceToken();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alerts.isGooglePlayServicesAvailable(this)) {
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Config.PUSH_NOTIFICATION));
            NotificationUtils.clearNotifications(getApplicationContext());
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private void registerBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayDeviceToken();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Message: " + message, Toast.LENGTH_SHORT).show();
                    pushNotificationContent.setText(message);
                }
            }
        };
    }

    private void displayDeviceToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        String token = pref.getString(Config.DEVICE_TOKEN, null);
        Log.d(TAG, "FCM Token: " + token);
        if (!TextUtils.isEmpty(token))
            deviceToken.setText(token);
        else
            deviceToken.setText(R.string.fcm_token_not_received_yet);
    }
}
package com.randmcnally.crashdetection.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.randmcnally.crashdetection.R;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("toto", "BOOT event received");
        if (loadCrashNotificationValue(context)) {
            Intent serviceIntent = new Intent(context, CNService.class);
            context.startService(serviceIntent);
        }
    }

    private boolean loadCrashNotificationValue(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                ctx.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean defaultValue = ctx.getResources().getBoolean(R.bool.crash_notification_enabled_default);
        return sharedPref.getBoolean(ctx.getResources().getString(R.string.saved_crash_notification_enabled), defaultValue);
    }
}

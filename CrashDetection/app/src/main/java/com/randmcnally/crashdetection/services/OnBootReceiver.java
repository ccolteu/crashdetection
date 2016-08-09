package com.randmcnally.crashdetection.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("toto", "BOOT event received");
        Intent serviceIntent = new Intent(context, CNService.class);
        context.startService(serviceIntent);
    }

}

package com.randmcnally.crashdetection.services;

import android.content.Intent;
import android.util.Log;

import com.randmcnally.crashdetection.AccidentActivity;
import com.randmcnally.crashdetection.event.DriveResumeEvent;
import com.randmcnally.crashdetection.event.DriveStartEvent;
import com.randmcnally.crashdetection.event.LocationSettingsChangeEvent;
import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.ZendriveIntentService;
import com.zendrive.sdk.ZendriveLocationSettingsResult;
import org.greenrobot.eventbus.EventBus;

public class CrashDetectionZendriveIntentService extends ZendriveIntentService {

    private static final String TAG = "ZendriveIntentService";

    public CrashDetectionZendriveIntentService() {
        super("MyZendriveIntentService");
    }

    @Override
    public void onDriveStart(DriveStartInfo startInfo) {
        Log.i(TAG, "onDriveStart");
        EventBus.getDefault().post(new DriveStartEvent());
    }

    @Override
    public void onDriveResume(DriveResumeInfo resumeInfo) {
        Log.i(TAG, "onDriveResume");
        EventBus.getDefault().post(new DriveResumeEvent());
    }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {
        Log.i(TAG, "onDriveEnd");
    }

    @Override
    public void onAccident(AccidentInfo accidentInfo) {
        Log.i(TAG, "onAccident");
        Log.i(TAG, "accident id = " + accidentInfo.accidentId);
        Log.i(TAG, "drive id    = " + accidentInfo.driveId);
        Log.i(TAG, "session id  = " + accidentInfo.sessionId);
        Log.i(TAG, "tracking id = " + accidentInfo.trackingId);
        Log.i(TAG, "confidence  = " + accidentInfo.confidence.getValue());
        Log.i(TAG, "lat/lon     = " + accidentInfo.location.latitude + "/" + accidentInfo.location.longitude);
        Log.i(TAG, "timestamp   = " + accidentInfo.timestampMillis);
        Intent intent = new Intent(this, AccidentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onLocationSettingsChange(ZendriveLocationSettingsResult locationSettingsResult) {
        Log.i(TAG, "onLocationSettingsChange");
        EventBus.getDefault().post(new LocationSettingsChangeEvent());
    }

    @Override
    public void onLocationPermissionsChange(boolean granted) {
        Log.i(TAG, "onLocationPermissionsChange");
    }
}

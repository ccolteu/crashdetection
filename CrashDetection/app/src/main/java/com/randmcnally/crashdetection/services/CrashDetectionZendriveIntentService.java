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
    public static final String IS_MOCK_ACCIDENT_KEY = CrashDetectionZendriveIntentService.class.getSimpleName() +
            ".IS_MOCK_ACCIDENT_KEY";

    public CrashDetectionZendriveIntentService() {
        super("MyZendriveIntentService");
    }

    @Override
    public void onDriveStart(DriveStartInfo startInfo) {
        Log.i("toto", "onDriveStart: " + startInfo.trackingId);
        EventBus.getDefault().post(new DriveStartEvent.Builder().trackingId(startInfo.trackingId).build());
    }

    @Override
    public void onDriveResume(DriveResumeInfo resumeInfo) {
        Log.i("toto", "onDriveResume: " + resumeInfo.trackingId);
        EventBus.getDefault().post(new DriveResumeEvent.Builder().trackingId(resumeInfo.trackingId).build());
    }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {
        Log.i("toto", "onDriveEnd: " + driveInfo.driveId);
    }

    @Override
    public void onAccident(AccidentInfo accidentInfo) {
        Log.i("toto", "onAccident: " + accidentInfo.trackingId);
        Intent intent = new Intent(this, AccidentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IS_MOCK_ACCIDENT_KEY, accidentInfo.trackingId.equalsIgnoreCase(CNService.MOCK_TRACKING_ID));
        startActivity(intent);
    }

    @Override
    public void onLocationSettingsChange(ZendriveLocationSettingsResult locationSettingsResult) {
        Log.i("toto", "onLocationSettingsChange");
        EventBus.getDefault().post(new LocationSettingsChangeEvent());
    }

    @Override
    public void onLocationPermissionsChange(boolean granted) {
        Log.i(TAG, "onLocationPermissionsChange");
    }
}

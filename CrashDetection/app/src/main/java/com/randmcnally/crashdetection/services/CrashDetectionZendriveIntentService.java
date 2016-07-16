package com.randmcnally.crashdetection.services;

import android.util.Log;

import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.ZendriveIntentService;
import com.zendrive.sdk.ZendriveLocationSettingsResult;

public class CrashDetectionZendriveIntentService extends ZendriveIntentService {
    public CrashDetectionZendriveIntentService() {
        super("MyZendriveIntentService");
    }

    @Override
    public void onDriveStart(DriveStartInfo startInfo) {  }

    @Override
    public void onDriveResume(DriveResumeInfo resumeInfo) {  }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {  }

    @Override
    public void onAccident(AccidentInfo accidentInfo) { Log.e("toto", "onAccident"); }

    @Override
    public void onLocationSettingsChange(ZendriveLocationSettingsResult locationSettingsResult) {  }

    @Override
    public void onLocationPermissionsChange(boolean granted) {  }

}

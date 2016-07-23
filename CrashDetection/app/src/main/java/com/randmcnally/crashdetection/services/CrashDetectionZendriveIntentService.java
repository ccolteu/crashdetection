package com.randmcnally.crashdetection.services;

import android.content.Intent;
import android.util.Log;

import com.randmcnally.crashdetection.AccidentActivity;
import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveAccidentConfidence;
import com.zendrive.sdk.ZendriveIntentService;
import com.zendrive.sdk.ZendriveLocationSettingsResult;
import com.zendrive.sdk.ZendriveOperationResult;

public class CrashDetectionZendriveIntentService extends ZendriveIntentService {

    public CrashDetectionZendriveIntentService() {
        super("MyZendriveIntentService");
    }

    @Override
    public void onDriveStart(DriveStartInfo startInfo) {
        Log.e("toto", "onDriveStart");

        ZendriveOperationResult result = Zendrive.triggerMockAccident(this, ZendriveAccidentConfidence.HIGH);
        Log.e("toto", "mock accident success ? " + result.isSuccess());
        Log.e("toto", "error code: " + result.getErrorCode());
        Log.e("toto", "error message: " + result.getErrorMessage());
    }

    @Override
    public void onDriveResume(DriveResumeInfo resumeInfo) {
        Log.e("toto", "onDriveResume");

        ZendriveOperationResult result = Zendrive.triggerMockAccident(this, ZendriveAccidentConfidence.HIGH);
        Log.e("toto", "mock accident success ? " + result.isSuccess());
        Log.e("toto", "error code: " + result.getErrorCode());
        Log.e("toto", "error message: " + result.getErrorMessage());
    }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {
        Log.e("toto", "onDriveEnd");
    }

    @Override
    public void onAccident(AccidentInfo accidentInfo) {
        Log.e("toto", "onAccident");

        startActivity(new Intent(this, AccidentActivity.class));
    }

    @Override
    public void onLocationSettingsChange(ZendriveLocationSettingsResult locationSettingsResult) {
        Log.e("toto", "onLocationSettingsChange");
    }

    @Override
    public void onLocationPermissionsChange(boolean granted) {
        Log.e("toto", "onLocationPermissionsChange");
    }

}

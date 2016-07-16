package com.randmcnally.crashdetection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.randmcnally.crashdetection.services.CrashDetectionZendriveIntentService;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveAccidentConfidence;
import com.zendrive.sdk.ZendriveAccidentDetectionMode;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveDriverAttributes;
import com.zendrive.sdk.ZendriveOperationResult;
import com.zendrive.sdk.ZendriveOperationCallback;

public class MainActivity extends AppCompatActivity {

    // SDK Key
    private String SDK_KEY = "U2PHmA2cwLGvt1DqEn1OnB96GMghZAXa";
    // ID for the driver currently using the application. Each driver using the application needs a unique ID.
    private String DRIVER_ID = "abc";
    // The TRACKING_ID can be used to find Zendrive trips with this ID in the Zendrive Analytics API.
    private String TRACKING_ID = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupZendrive();
    }

    private void setupZendrive() {

        ZendriveDriverAttributes driverAttributes = new ZendriveDriverAttributes();
        driverAttributes.setFirstName("Homer");
        driverAttributes.setLastName("Simpson");
        driverAttributes.setEmail("homer@springfield.com");
        driverAttributes.setPhoneNumber("14155557334");

        ZendriveConfiguration zendriveConfiguration =  new ZendriveConfiguration(
                SDK_KEY,
                DRIVER_ID,
                ZendriveDriveDetectionMode.AUTO_ON,
                ZendriveAccidentDetectionMode.ENABLED);
        zendriveConfiguration.setDriverAttributes(driverAttributes);

        Zendrive.setup(
                this.getApplicationContext(),
                zendriveConfiguration,
                CrashDetectionZendriveIntentService.class,
                new ZendriveOperationCallback() {
                    @Override
                    public void onCompletion(ZendriveOperationResult result) {
                        if (result.isSuccess()) {
                            triggerMockAccident();
                        } else {
                        }
                    }
                }
        );

        triggerMockAccident2();
    }

    private void triggerMockAccident() {
        //Zendrive.setZendriveDriveDetectionMode(ZendriveDriveDetectionMode.AUTO_OFF);
        ZendriveOperationResult result = Zendrive.startDrive(TRACKING_ID);
        Log.e("toto", "start drive success ? " + result.isSuccess());
        Log.e("toto", "error code: " + result.getErrorCode());
        Log.e("toto", "error message: " + result.getErrorMessage());
        final Context ctx = this.getApplicationContext();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ZendriveOperationResult result = Zendrive.triggerMockAccident(ctx, ZendriveAccidentConfidence.HIGH);
                Log.e("toto", "mock accident success ? " + result.isSuccess());
                Log.e("toto", "error code: " + result.getErrorCode());
                Log.e("toto", "error message: " + result.getErrorMessage());
            }
        }, 2000);

//        result = Zendrive.stopDrive();
//        Log.e("toto", "stop drive success ? " + result.isSuccess());
//        Log.e("toto", "error code: " + result.getErrorCode());
//        Log.e("toto", "error message: " + result.getErrorMessage());
    }

    private void triggerMockAccident2() {
        startActivity(new Intent(this, AccidentActivity.class));
    }
}

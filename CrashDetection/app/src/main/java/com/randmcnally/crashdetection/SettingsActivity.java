package com.randmcnally.crashdetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.randmcnally.crashdetection.services.CrashDetectionZendriveIntentService;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveAccidentDetectionMode;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveDriverAttributes;
import com.zendrive.sdk.ZendriveOperationCallback;
import com.zendrive.sdk.ZendriveOperationResult;

public class SettingsActivity  extends AppCompatActivity {

    // SDK Key
    private String SDK_KEY = "U2PHmA2cwLGvt1DqEn1OnB96GMghZAXa";
    // ID for the driver currently using the application. Each driver using the application needs a unique ID.
    private String DRIVER_ID = "abc";
    // The TRACKING_ID can be used to find Zendrive trips with this ID in the Zendrive Analytics API.
    private String TRACKING_ID = "123";

    private SettingsActivity activity;
    private TextView mPairedPhoneSelectionTextView, mEmergencyContactSelectionTextView, mAccidentCallSelectionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        setContentView(R.layout.activity_settings);

        mPairedPhoneSelectionTextView = (TextView)findViewById(R.id.paired_phone_selection);
        mEmergencyContactSelectionTextView = (TextView)findViewById(R.id.emergency_contact_selection);
        mAccidentCallSelectionTextView = (TextView)findViewById(R.id.accident_call_selection);

        // TODO persist selected values and load them from Shared Preferences

        findViewById(R.id.test_crash_detection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, AccidentActivity.class));
            }
        });

        findViewById(R.id.paired_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO launch native BT pair/search dialog
            }
        });

        findViewById(R.id.emergency_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO launch contact search page
            }
        });

        findViewById(R.id.accident_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open custom dialog
            }
        });

        setupZendrive();
    }

    private void setupZendrive() {
        Log.e("toto", "Zendrive setup");

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
                            startDrive();
                        } else {

                        }
                    }
                }
        );
    }

    private void startDrive() {
        ZendriveOperationResult result = Zendrive.startDrive(TRACKING_ID);
        Log.e("toto", "start drive success ? " + result.isSuccess());
        Log.e("toto", "error code: " + result.getErrorCode());
        Log.e("toto", "error message: " + result.getErrorMessage());
    }
}

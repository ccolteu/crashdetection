package com.randmcnally.crashdetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.randmcnally.crashdetection.event.DriveResumeEvent;
import com.randmcnally.crashdetection.event.DriveStartEvent;
import com.randmcnally.crashdetection.event.LocationSettingsChangeEvent;
import com.randmcnally.crashdetection.services.CrashDetectionZendriveIntentService;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveAccidentConfidence;
import com.zendrive.sdk.ZendriveAccidentDetectionMode;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveDriverAttributes;
import com.zendrive.sdk.ZendriveErrorCode;
import com.zendrive.sdk.ZendriveOperationCallback;
import com.zendrive.sdk.ZendriveOperationResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    public static final String MOCK_TRACKING_ID = SettingsActivity.class.getSimpleName() + ".MOCK_TRACKING_ID";

    // SDK Key
    private String SDK_KEY = "U2PHmA2cwLGvt1DqEn1OnB96GMghZAXa";
    // ID for the driver currently using the application. Each driver using the application needs a unique ID.
    private String DRIVER_ID = "abc";

    private SettingsActivity activity;
    private TextView mPairedPhoneSelectionTextView, mEmergencyContactSelectionTextView, mAccidentCallSelectionTextView;
    private Switch mCrashNotificationSwitch;

    private boolean setupComplete;
    private boolean isMockDriving;
    private boolean triggerMockAccident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        setContentView(R.layout.activity_settings);

        mPairedPhoneSelectionTextView = (TextView)findViewById(R.id.paired_phone_selection);
        mEmergencyContactSelectionTextView = (TextView)findViewById(R.id.emergency_contact_selection);
        mAccidentCallSelectionTextView = (TextView)findViewById(R.id.accident_call_selection);
        mCrashNotificationSwitch = (Switch) findViewById(R.id.crash_notification_selection);

        findViewById(R.id.test_crash_detection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mCrashNotificationSwitch.isChecked()) {
                    Toast.makeText(activity, "enable Crash Notification first", Toast.LENGTH_LONG).show();
                } else {
                    if (setupComplete) {
                        if (isMockDriving) {
                            Toast.makeText(activity, "mock accident", Toast.LENGTH_LONG).show();
                            triggerAccident();
                        } else {
                            Toast.makeText(activity, "trigger start drive, then mock accident", Toast.LENGTH_LONG).show();
                            triggerMockAccident = true;
                            startMockDrive();
                        }
                    } else {
                        Toast.makeText(activity, "setup in progress, please try again later", Toast.LENGTH_LONG).show();
                    }
                }
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

        mCrashNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveCrashNotificationValue(b);
                if (b) {
                    setupZendrive();
                } else {
                    // TODO stop whatever Zendrive is doing
                }
            }
        });

        mCrashNotificationSwitch.setChecked(loadCrashNotificationValue());

        if (mCrashNotificationSwitch.isChecked()) {
            setupZendrive();
        }
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
                        Log.i(TAG, "Zendrive setup success ? " + result.isSuccess());
                        setupComplete = result.isSuccess();
                        if (!result.isSuccess()) {
                            handleError(result);
                        }
                    }
                }
        );
    }

    private void startMockDrive() {
        ZendriveOperationResult result = Zendrive.startDrive(MOCK_TRACKING_ID);
        Log.i(TAG, "mock startDrive success ? " + result.isSuccess());
        if (!result.isSuccess()) {
            handleError(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Zendrive.stopDrive(MOCK_TRACKING_ID);
        super.onDestroy();
    }

    @Subscribe
    public void onDriveStartEvent(DriveStartEvent event) {
        if (event.trackingId.equalsIgnoreCase(MOCK_TRACKING_ID)) {
            isMockDriving = true;
            if (triggerMockAccident) {
                triggerMockAccident = false;
                triggerAccident();
            }
        }
    }

    @Subscribe
    public void onDriveResumeEvent(DriveResumeEvent event) {
        if (event.trackingId.equalsIgnoreCase(MOCK_TRACKING_ID)) {
            isMockDriving = true;
            if (triggerMockAccident) {
                triggerMockAccident = false;
                triggerAccident();
            }
        }
    }

    @Subscribe
    public void onLocationSettingsChangeEvent(LocationSettingsChangeEvent event) {
    }

    private void triggerAccident() {
        ZendriveOperationResult result = Zendrive.triggerMockAccident(this, ZendriveAccidentConfidence.HIGH);
        if (!result.isSuccess()) {
            handleError(result);
        }
    }

    private void saveCrashNotificationValue(boolean value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.saved_crash_notification_enabled), value);
        editor.commit();
    }

    private boolean loadCrashNotificationValue() {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean defaultValue = getResources().getBoolean(R.bool.crash_notification_enabled_default);
        return sharedPref.getBoolean(getResources().getString(R.string.saved_crash_notification_enabled), defaultValue);
    }

    private void handleError(ZendriveOperationResult result) {
        Log.e(TAG, "error code: " + result.getErrorCode());
        Log.e(TAG, "error message: " + result.getErrorMessage());
        if (result.getErrorCode() == ZendriveErrorCode.NETWORK_NOT_AVAILABLE) {
            Toast.makeText(activity, "Error: Network not available.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.GOOGLE_PLAY_SERVICES_UNAVAILABLE) {
            Toast.makeText(activity, "Error: Google Play services unavailable.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.GOOGLE_PLAY_SERVICES_UPDATE_REQUIRED) {
            Toast.makeText(activity, "Error: Google Play services update required.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.ACCIDENT_DETECTION_NOT_AVAILABLE_FOR_APPLICATION) {
            Toast.makeText(activity, "Error: Accident detection not available for application.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.ACCIDENT_DETECTION_NOT_AVAILABLE_FOR_DEVICE) {
            Toast.makeText(activity, "Error: Accident detection not available for device.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.ANDROID_VERSION_NOT_SUPPORTED) {
            Toast.makeText(activity, "Error: Android version not supported.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.LOCATION_ACCURACY_NOT_AVAILABLE) {
            Toast.makeText(activity, "Error: Location accuracy not available.", Toast.LENGTH_LONG).show();
        }
    }
}

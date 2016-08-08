package com.randmcnally.crashdetection.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.randmcnally.crashdetection.event.DriveResumeEvent;
import com.randmcnally.crashdetection.event.DriveStartEvent;
import com.randmcnally.crashdetection.event.LocationSettingsChangeEvent;
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

public class CNService extends Service {

    private static final String TAG = CNService.class.getSimpleName();

    public static final String MOCK_TRACKING_ID = CNService.class.getSimpleName() + ".MOCK_TRACKING_ID";

    // SDK Key
    private String SDK_KEY = "U2PHmA2cwLGvt1DqEn1OnB96GMghZAXa";

    // ID for the driver currently using the application.
    // Each driver using the application needs a unique ID.
    private String DRIVER_ID = "abc";

    private CNService service;
    final Messenger myMessenger = new Messenger(new IncomingHandler());

    private boolean setupComplete;
    private boolean isMockDriving;
    private boolean triggerMockAccident;

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        stopZendrive();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myMessenger.getBinder();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            // handle message from client
            Bundle data = msg.getData();
            String command = data.getString("command");

            if (command.equalsIgnoreCase("start_zendrive")) {

                Log.d("toto", "CNService: received start_zendrive");

                final Messenger replyMessenger = msg.replyTo;

                ZendriveDriverAttributes driverAttributes = new ZendriveDriverAttributes();
                driverAttributes.setFirstName("Homer");
                driverAttributes.setLastName("Simpson");
                driverAttributes.setEmail("homer@springfield.com");
                driverAttributes.setPhoneNumber("14155557334");

                ZendriveConfiguration zendriveConfiguration = new ZendriveConfiguration(
                        SDK_KEY,
                        DRIVER_ID,
                        ZendriveDriveDetectionMode.AUTO_ON,
                        ZendriveAccidentDetectionMode.ENABLED);
                zendriveConfiguration.setDriverAttributes(driverAttributes);

                Zendrive.setup(
                        service.getApplicationContext(),
                        zendriveConfiguration,
                        CrashDetectionZendriveIntentService.class,
                        new ZendriveOperationCallback() {
                            @Override
                            public void onCompletion(ZendriveOperationResult result) {
                                Log.i(TAG, "Zendrive setup success ? " + result.isSuccess());

                                setupComplete = result.isSuccess();

                                // respond to client
                                try {
                                    android.os.Message responseMessage = android.os.Message.obtain();
                                    Bundle responseBundle = new Bundle();
                                    responseBundle.putBoolean("zendrive_start_success", result.isSuccess());
                                    responseMessage.setData(responseBundle);
                                    replyMessenger.send(responseMessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (!result.isSuccess()) {
                                    handleError(result);
                                }
                            }
                        }
                );

            } else if (command.equalsIgnoreCase("stop_zendrive")) {

                Log.d("toto", "CNService: received stop_zendrive");

                final Messenger replyMessenger = msg.replyTo;

                Zendrive.teardown(new ZendriveOperationCallback() {
                    @Override
                    public void onCompletion(ZendriveOperationResult result) {
                        Log.i(TAG, "Zendrive setup success ? " + result.isSuccess());

                        // respond to client
                        try {
                            android.os.Message responseMessage = android.os.Message.obtain();
                            Bundle responseBundle = new Bundle();
                            responseBundle.putBoolean("zendrive_stop_success", result.isSuccess());
                            responseMessage.setData(responseBundle);
                            replyMessenger.send(responseMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!result.isSuccess()) {
                            handleError(result);
                        }
                    }
                });

            } else if (command.equalsIgnoreCase("mock_accident")) {

                Log.d("toto", "CNService: received mock_accident");

                if (setupComplete) {
                    if (isMockDriving) {
                        triggerMockAccident();
                    } else {
                        triggerMockAccident = true;
                        triggerMockDrive();
                    }
                }
            }
        }
    }

    private void handleError(ZendriveOperationResult result) {
        Log.e(TAG, "error code: " + result.getErrorCode());
        Log.e(TAG, "error message: " + result.getErrorMessage());
        if (result.getErrorCode() == ZendriveErrorCode.NETWORK_NOT_AVAILABLE) {
            Toast.makeText(service, "Error: Network not available.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.GOOGLE_PLAY_SERVICES_UNAVAILABLE) {
            Toast.makeText(service, "Error: Google Play services unavailable.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.GOOGLE_PLAY_SERVICES_UPDATE_REQUIRED) {
            Toast.makeText(service, "Error: Google Play services update required.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.ACCIDENT_DETECTION_NOT_AVAILABLE_FOR_APPLICATION) {
            Toast.makeText(service, "Error: Accident detection not available for application.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.ACCIDENT_DETECTION_NOT_AVAILABLE_FOR_DEVICE) {
            Toast.makeText(service, "Error: Accident detection not available for device.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.ANDROID_VERSION_NOT_SUPPORTED) {
            Toast.makeText(service, "Error: Android version not supported.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.LOCATION_ACCURACY_NOT_AVAILABLE) {
            Toast.makeText(service, "Error: Location accuracy not available.", Toast.LENGTH_LONG).show();
        } else if (result.getErrorCode() == ZendriveErrorCode.MOCK_ACCIDENT_ERROR) {
            Toast.makeText(service, "Error: " + result.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onDriveStartEvent(DriveStartEvent event) {
        Log.d("toto", "CNService: received onDriveStartEvent");
        if (event.trackingId.equalsIgnoreCase(MOCK_TRACKING_ID)) {
            isMockDriving = true;
            if (triggerMockAccident) {
                triggerMockAccident = false;
                triggerMockAccident();
            }
        }
    }

    @Subscribe
    public void onDriveResumeEvent(DriveResumeEvent event) {
        Log.d("toto", "CNService: received DriveResumeEvent");
        if (event.trackingId.equalsIgnoreCase(MOCK_TRACKING_ID)) {
            isMockDriving = true;
            if (triggerMockAccident) {
                triggerMockAccident = false;
                triggerMockAccident();
            }
        }
    }

    @Subscribe
    public void onLocationSettingsChangeEvent(LocationSettingsChangeEvent event) {
        Log.d("toto", "CNService: received LocationSettingsChangeEvent");
    }

    private void triggerMockDrive() {
        Log.d("toto", "CNService:triggerMockDrive");
        ZendriveOperationResult result = Zendrive.startDrive(MOCK_TRACKING_ID);
        Log.i(TAG, "start mock success ? " + result.isSuccess());
        if (!result.isSuccess()) {
            handleError(result);
        }
    }

    private void triggerMockAccident() {
        Log.d("toto", "CNService:triggerMockAccident");
        ZendriveOperationResult result = Zendrive.triggerMockAccident(service, ZendriveAccidentConfidence.HIGH);
        Log.i(TAG, "trigger mock accident success ? " + result.isSuccess());
        if (!result.isSuccess()) {
            handleError(result);
        }

        // TODO this may be a racing condition with triggerMockAccident
        // which would cause MOCK_ACCIDENT_ERROR: no trip in progress
        Zendrive.stopDrive(MOCK_TRACKING_ID);
    }

    private void stopZendrive() {
        Zendrive.teardown(new ZendriveOperationCallback() {
            @Override
            public void onCompletion(ZendriveOperationResult zendriveOperationResult) {

            }
        });
    }
}

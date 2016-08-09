package com.randmcnally.crashdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.randmcnally.crashdetection.apis.CNServiceApi;
import com.randmcnally.crashdetection.model.Contact;

public class SettingsActivity extends AppCompatActivity implements PriorityDialogFragment.PriorityDialogListener {

    private static final String TAG = "SettingsActivity";

    static final int PICK_CONTACT = 1;

    private SettingsActivity activity;
    private TextView mPairedPhoneSelectionTextView, mEmergencyContactSelectionTextView, mAccidentCallSelectionTextView;
    private SwitchCompat mCrashNotificationSwitch;

    private CNServiceApi mCNServiceApi;
    private CNServiceApi.CNServiceListener mCNServiceListener = new CNServiceApi.CNServiceListener() {
        @Override
        public void onConnected() {
            Log.d("toto", "CNService connected");
            if (mCrashNotificationSwitch.isChecked()) {
                if (mCNServiceApi != null) {
                    mCNServiceApi.startZendrive();
                }
            }
        }

        @Override
        public void onFailedToConnect() {
            Log.e("toto", "CNService failed to connect");
        }

        @Override
        public void onReceive(Bundle data) {
            if (data.containsKey("zendrive_start_success")) {
                Log.d("toto", "zendrive start success: " + data.getBoolean("zendrive_start_success"));
            } else if (data.containsKey("zendrive_stop_success")) {
                Log.d("toto", "zendrive stop success: " + data.getBoolean("zendrive_stop_success"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        setContentView(R.layout.activity_settings);

        mPairedPhoneSelectionTextView = (TextView)findViewById(R.id.paired_phone_selection);
        mEmergencyContactSelectionTextView = (TextView)findViewById(R.id.emergency_contact_selection);
        mAccidentCallSelectionTextView = (TextView)findViewById(R.id.accident_call_selection);
        mCrashNotificationSwitch = (SwitchCompat)findViewById(R.id.crash_notification_selection);

        findViewById(R.id.crash_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCrashNotificationSwitch.toggle();
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
                // TODO use custom contact picker
                try {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    // Show user only contacts w/ phone numbers
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContactIntent, PICK_CONTACT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.accident_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccidentCallChoiceDialog();
            }
        });

        findViewById(R.id.test_crash_detection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mCrashNotificationSwitch.isChecked()) {
                    Toast.makeText(activity, "enable Crash Notification first", Toast.LENGTH_LONG).show();
                } else {
                    mCNServiceApi.mockAccident();
                }
            }
        });

        mCrashNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveCrashNotificationValue(b);
                if (b) {
                    if (mCNServiceApi != null) {
                        mCNServiceApi.startZendrive();
                    } else {
                        mCNServiceApi = new CNServiceApi();
                        boolean success = mCNServiceApi.init(activity, mCNServiceListener);
                        if (!success) {
                            // could not bind to the CN remote service
                            Log.e(TAG, "ERROR: cannot bind to remote CNService");
                        }
                    }
                } else {
                    if (mCNServiceApi != null) {
                        mCNServiceApi.stopZendrive();
                    }
                    mCNServiceApi = null;
                }
            }
        });

        // initial values
        mCrashNotificationSwitch.setChecked(loadCrashNotificationValue());
        mAccidentCallSelectionTextView.setText(loadPriorityValue() ? getResources().getString(R.string
                .emergency_contact_first_letters_caps) : getResources().getString(R.string.nine_one_one));
        Contact emergencyContact = loadEmergencyContact();
        mEmergencyContactSelectionTextView.setText(!TextUtils.isEmpty(emergencyContact.name) ? emergencyContact.name : "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loadCrashNotificationValue()) {
            mCNServiceApi = new CNServiceApi();
            boolean success = mCNServiceApi.init(this, mCNServiceListener);
            if (!success) {
                // could not bind to the CN remote service
                Log.e(TAG, "ERROR: cannot bind to remote CNService");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCNServiceApi != null) {
            mCNServiceApi.terminate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Contact contact = new Contact();
                    String[] projection = new String[]{
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    };
                    Cursor c = getContentResolver().query(contactData, projection, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        contact.phone = c.getString(0);
                        contact.name = c.getString(1);
                        c.close();
                    }
                    if (contact.phone == null) {
                        contact.phone = "Unknown";
                    }
                    if (contact.name == null) {
                        contact.name = "Unknown";
                    }
                    Toast.makeText(this, "Emergency contact: " + contact.name + " - " + contact.phone, Toast.LENGTH_LONG).show();
                    saveEmergencyContact(contact);
                    mEmergencyContactSelectionTextView.setText(contact.name);
                }
                break;
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

    private void savePriorityValue(boolean value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.saved_priority_is_contact), value);
        editor.commit();
    }

    private boolean loadPriorityValue() {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean defaultValue = getResources().getBoolean(R.bool.priority_is_contact_default);
        return sharedPref.getBoolean(getResources().getString(R.string.saved_priority_is_contact), defaultValue);
    }

    public static final String PRIORITY_CONTACT_KEY = SettingsActivity.class.getSimpleName() + ".PRIORITY_CONTACT_KEY";

    private void showAccidentCallChoiceDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PriorityDialogFragment dialogFragment = new PriorityDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(PRIORITY_CONTACT_KEY, loadPriorityValue());
        dialogFragment.setArguments(args);
        dialogFragment.show(fm, "accident_call_dialog");
    }

    @Override
    public void onContactSelected(AlertDialog dialog, boolean isContact) {
        savePriorityValue(isContact);
        mAccidentCallSelectionTextView.setText(loadPriorityValue() ? getResources().getString(R.string
                .emergency_contact_first_letters_caps) : getResources().getString(R.string.nine_one_one));
    }

    private void saveEmergencyContact(Contact contact) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_emergency_contact_name), contact.name);
        editor.putString(getString(R.string.saved_emergency_contact_phone), contact.phone);
        editor.commit();
    }

    private Contact loadEmergencyContact() {
        Contact contact = new Contact();
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        contact.name = sharedPref.getString(getResources().getString(R.string.saved_emergency_contact_name), null);
        contact.phone = sharedPref.getString(getResources().getString(R.string.saved_emergency_contact_phone), null);
        return contact;
    }
}

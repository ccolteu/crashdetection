package com.randmcnally.crashdetection;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

public class PriorityDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PriorityDialogListener {
        void onContactSelected(AlertDialog dialog, boolean isContact);
    }

    private boolean isContact;
    private PriorityDialogListener mListener;
    private AlertDialog mDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PriorityDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PriorityDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        isContact = getArguments().getBoolean(SettingsActivity.PRIORITY_CONTACT_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.dialog_priorities, null);

        builder.setView(root)
                .setTitle(R.string.select_priority)
                .setPositiveButton(R.string.ok_all_caps, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onContactSelected(mDialog, isContact);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_all_caps, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        final RadioButton radioEmergencyContact = (RadioButton) root.findViewById(R.id.radio_emergency_contact);
        final RadioButton radioNineOneOne = (RadioButton) root.findViewById(R.id.radio_nine_one_one);

        radioEmergencyContact.setChecked(isContact);
        radioNineOneOne.setChecked(!isContact);

        radioEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isContact = true;
                radioEmergencyContact.setChecked(true);
            }
        });

        radioNineOneOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isContact = false;
                radioNineOneOne.setChecked(true);
            }
        });

        mDialog = builder.create();

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getActivity().getResources().getColor(android.R.color
                        .black));
                mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActivity().getResources().getColor(R.color
                        .colorPrimary));
            }
        });

        return mDialog;
    }
}

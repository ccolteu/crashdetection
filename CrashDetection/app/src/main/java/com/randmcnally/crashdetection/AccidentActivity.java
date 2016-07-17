package com.randmcnally.crashdetection;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.randmcnally.crashdetection.utils.AnimUtils;
import com.randmcnally.crashdetection.widgets.Circle;
import com.randmcnally.crashdetection.widgets.CircleAngleAnimation;

public class AccidentActivity extends AppCompatActivity {

    private Circle mCountdownCircleView;
    private CircleAngleAnimation mCountwodnCircleAnimation;
    private AlertDialog mImOkAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);
        mCountdownCircleView = (Circle) findViewById(R.id.countdown_circle);
        mCountwodnCircleAnimation = AnimUtils.getInstance(this).startCountdownAnimation(mCountdownCircleView);

        findViewById(R.id.cancel_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImOkDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImOkAlertDialog != null) {
            mImOkAlertDialog.dismiss();
            mImOkAlertDialog = null;
        }
        if (mCountwodnCircleAnimation != null
                && mCountwodnCircleAnimation.hasStarted()
                && !mCountwodnCircleAnimation.hasEnded()) {
            mCountwodnCircleAnimation.cancel();
            mCountwodnCircleAnimation = null;
        }
    }

    private void showImOkDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_im_ok, null);
        dialogView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImOkAlertDialog != null) {
                    mImOkAlertDialog.dismiss();
                }
                if (mCountwodnCircleAnimation != null
                        && mCountwodnCircleAnimation.hasStarted()
                        && !mCountwodnCircleAnimation.hasEnded()) {
                    mCountwodnCircleAnimation.cancel();
                }
            }
        });
        dialogBuilder.setView(dialogView);
        mImOkAlertDialog = dialogBuilder.create();
        mImOkAlertDialog.show();
    }

}

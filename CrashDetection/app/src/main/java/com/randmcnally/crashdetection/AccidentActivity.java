package com.randmcnally.crashdetection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.randmcnally.crashdetection.utils.AnimUtils;
import com.randmcnally.crashdetection.widgets.Circle;
import com.randmcnally.crashdetection.widgets.CircleAngleAnimation;

public class AccidentActivity extends AppCompatActivity {

    private final static int COUNTDOWN_SECONDS = 30;
    private final static int UPDATE_COUNTDOWN = 1;
    private final static int CALL_911 = 1;
    private final static int CALL_CONTACT = 2;
    private Circle mCountdownCircleView;
    private CircleAngleAnimation mCountwodnCircleAnimation;
    private AlertDialog mImOkAlertDialog;
    private CountdownHandler mCountdownHandler;
    private int mCountdownSeconds;
    private TextView mCountdownSecondsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);
        mCountdownCircleView = (Circle) findViewById(R.id.countdown_circle);
        mCountdownSecondsTextView = (TextView) findViewById(R.id.seconds);

        mCountdownHandler = new CountdownHandler();
        mCountdownSeconds = COUNTDOWN_SECONDS;
        updateCountdown();
        mCountdownHandler.sendEmptyMessageDelayed(UPDATE_COUNTDOWN, 1000);
        mCountwodnCircleAnimation = AnimUtils.getInstance(this).startCountdownAnimation(mCountdownCircleView, COUNTDOWN_SECONDS);

        findViewById(R.id.cancel_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCountdown();
                showImOkDialog();
            }
        });

        findViewById(R.id.call_911).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCountdown();
                call(CALL_911);
            }
        });

        findViewById(R.id.call_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCountdown();
                call(CALL_CONTACT);
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
        if (mCountdownHandler != null) {
            mCountdownHandler.removeMessages(UPDATE_COUNTDOWN);
            mCountdownHandler = null;
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
                    finish();
                }
            }
        });
        dialogBuilder.setView(dialogView);
        mImOkAlertDialog = dialogBuilder.create();
        mImOkAlertDialog.setCancelable(false);
        mImOkAlertDialog.show();
    }

    private class CountdownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_COUNTDOWN) {
                updateCountdown();
                if (mCountdownSeconds > 0) {
                    mCountdownHandler.sendEmptyMessageDelayed(UPDATE_COUNTDOWN, 1000);
                } else {
                    call(CALL_CONTACT);
                }
            }
        }
    }

    private void updateCountdown() {
        mCountdownSeconds--;
        mCountdownSecondsTextView.setText(Integer.toString(mCountdownSeconds));
    }

    private void stopCountdown() {
        if (mCountwodnCircleAnimation != null
                && mCountwodnCircleAnimation.hasStarted()
                && !mCountwodnCircleAnimation.hasEnded()) {
            mCountwodnCircleAnimation.cancel();
        }
        if (mCountdownHandler != null) {
            mCountdownHandler.removeMessages(UPDATE_COUNTDOWN);
        }
    }

    private void call(int recipient) {
        if (recipient == CALL_911) {
            // TODO call 911
        } else if (recipient == CALL_CONTACT) {
            // TODO call emergency contact
        }
    }

}

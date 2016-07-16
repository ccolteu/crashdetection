package com.randmcnally.crashdetection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.randmcnally.crashdetection.utils.AnimUtils;
import com.randmcnally.crashdetection.widgets.Circle;

public class AccidentActivity extends AppCompatActivity {

    private Circle countdownCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);
        countdownCircleView = (Circle) findViewById(R.id.countdown_circle);
        AnimUtils.getInstance(this).startCountdownAnimation(countdownCircleView);
    }
}

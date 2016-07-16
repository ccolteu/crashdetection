package com.randmcnally.crashdetection.utils;

import android.content.Context;
import android.view.animation.Animation;
import com.randmcnally.crashdetection.widgets.Circle;
import com.randmcnally.crashdetection.widgets.CircleAngleAnimation;

public class AnimUtils {

    // volatile ensures that multiple threads
    // handle the singleton instance correctly
    private static volatile AnimUtils instance;
    private static Context mContext;

    public static AnimUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new AnimUtils();
        }
        mContext = ctx;
        return instance;
    }

    protected AnimUtils() {

    }

    public void startCountdownAnimation(final Circle countdownCircleView) {

        CircleAngleAnimation animation = new CircleAngleAnimation(countdownCircleView, 360);
        animation.setDuration(5000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                countdownCircleView.clear();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        countdownCircleView.startAnimation(animation);
    }
}

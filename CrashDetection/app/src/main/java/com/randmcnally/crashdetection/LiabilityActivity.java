package com.randmcnally.crashdetection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.randmcnally.crashdetection.interfaces.ScrollViewListener;
import com.randmcnally.crashdetection.widgets.ScrollViewExt;

public class LiabilityActivity extends AppCompatActivity implements ScrollViewListener {

    private View mButtonsView;
    private LiabilityActivity activity;
    private boolean mDontShowAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getSupportActionBar().setTitle(getResources().getString(R.string.liability_waiver));
        setContentView(R.layout.activity_liability);
        mButtonsView = findViewById(R.id.buttons);
        final ScrollViewExt scrollView = (ScrollViewExt) findViewById(R.id.scroll_view);
        scrollView.setScrollViewListener(this);
        findViewById(R.id.reject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, SettingsActivity.class));
                finish();
            }
        });

        ((CheckBox)findViewById(R.id.do_not_show_again_checkbox)).setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveDontShowAgainValue(b);
            }
        });

        if (loadDontShowAgainValue()) {
            startActivity(new Intent(activity, SettingsActivity.class));
            finish();
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        if (diff == 0) {
            if (mButtonsView.getVisibility() == View.GONE) {
                fadeInButtons();
            }
        }
    }

    private void fadeInButtons() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mButtonsView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mButtonsView.startAnimation(fadeInAnimation);
    }

    private void saveDontShowAgainValue(boolean value) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.saved_liability_dont_show), value);
        editor.commit();
    }

    private boolean loadDontShowAgainValue() {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean defaultValue = getResources().getBoolean(R.bool.liability_dont_show_default);
        return sharedPref.getBoolean(getResources().getString(R.string.saved_liability_dont_show), defaultValue);
    }
}

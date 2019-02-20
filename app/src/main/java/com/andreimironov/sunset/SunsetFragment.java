package com.andreimironov.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SunsetFragment extends Fragment {
    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private boolean mIsSunDown = false;

    private ValueAnimator mWidthAnimator;
    private ValueAnimator mHeightAnimator;
    private AnimatorSet mPulsateAnimatorSet;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);
        mSceneView = view;
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsSunDown) {
                    setSunUp();
                } else {
                    setSunDown();
                }
            }
        });
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);


        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);
        mSunView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSunView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mHeightAnimator = ValueAnimator
                        .ofInt(mSunView.getMeasuredHeight(), mSunView.getMeasuredHeight() * 2);
                mHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = mSunView.getLayoutParams();
                        params.height = value;
                        mSunView.setLayoutParams(params);
                    }
                });
                mHeightAnimator.setDuration(1000);
                mHeightAnimator.setRepeatCount(ValueAnimator.INFINITE);
                mWidthAnimator = ValueAnimator
                        .ofInt(mSunView.getMeasuredWidth(), mSunView.getMeasuredWidth() * 2);
                mWidthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = mSunView.getLayoutParams();
                        params.width = value;
                        mSunView.setLayoutParams(params);
                    }
                });
                mWidthAnimator.setDuration(1000);
                mWidthAnimator.setRepeatCount(ValueAnimator.INFINITE);
                mPulsateAnimatorSet = new AnimatorSet();
                mPulsateAnimatorSet
                        .play(mHeightAnimator)
                        .with(mWidthAnimator);
                mPulsateAnimatorSet.start();
            }
        });
        return view;
    }

    private void setSunUp() {
        mIsSunDown = false;
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", mSkyView.getHeight(), mSunView.getTop())
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator(1));
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());
        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(nightSkyAnimator)
                .before(sunsetSkyAnimator)
                .before(heightAnimator);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPulsateAnimatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void setSunDown() {
        mIsSunDown = true;
        mPulsateAnimatorSet.cancel();
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", mSunView.getTop(), mSkyView.getHeight())
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator(1));
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());
        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }
}

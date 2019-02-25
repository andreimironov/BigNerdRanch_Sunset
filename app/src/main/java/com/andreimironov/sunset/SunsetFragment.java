package com.andreimironov.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SunsetFragment extends Fragment
        implements View.OnClickListener, Animator.AnimatorListener {
    private static final String TAG = "SunsetFragment";

    private View mSunView;
    private View mSkyView;

    private SunsetState mSunsetState;

    private Animator mAnimator;
    private AnimatorTypes mAnimatorType;
    private boolean mIsCancelled = false;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);
        view.setOnClickListener(this);
        mSkyView = view.findViewById(R.id.sky);
        mSunView = view.findViewById(R.id.sun);
        mSunView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSunView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Resources resources = getResources();
                mSunsetState = new SunsetState(
                        mSunView.getLayoutParams().height,
                        mSunView.getTop(),
                        mSkyView.getHeight(),
                        resources.getColor(R.color.blue_sky),
                        resources.getColor(R.color.sunset_sky),
                        resources.getColor(R.color.night_sky)
                );
                setAnimatorType(AnimatorTypes.SUN_GOES_UP, false);
                setAnimator(mAnimatorType);
                mAnimator.start();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (mAnimator.isStarted()) {
            mAnimator.cancel();
        } else {
            mAnimator.start();
        }
    }

    private void setAnimatorType(AnimatorTypes previousAnimatorType, boolean isCancelled) {
        switch (previousAnimatorType) {
            case PULSATING:
                mAnimatorType = isCancelled ? AnimatorTypes.SUN_GOES_DOWN : AnimatorTypes.PULSATING;
                break;
            case SUN_GOES_DOWN:
                mAnimatorType = isCancelled ? AnimatorTypes.SUN_GOES_UP : AnimatorTypes.SKY_BECOMES_NIGHT;
                break;
            case SKY_BECOMES_NIGHT:
                mAnimatorType = AnimatorTypes.SKY_BECOMES_SUNSET;
                break;
            case SKY_BECOMES_SUNSET:
                mAnimatorType = isCancelled ? AnimatorTypes.SKY_BECOMES_NIGHT : AnimatorTypes.SUN_GOES_UP;
                break;
            case SUN_GOES_UP:
                mAnimatorType = isCancelled ? AnimatorTypes.SUN_GOES_DOWN : AnimatorTypes.PULSATING;
                break;
            default:
                break;
        }
    }

    private void setAnimator(AnimatorTypes animatorType) {
        switch (animatorType) {
            case PULSATING:
                mAnimator = newPulsateAnimator();
                break;
            case SUN_GOES_DOWN:
                mAnimator = newSunGoesDownAnimator();
                break;
            case SKY_BECOMES_NIGHT:
                mAnimator = newSkyBecomesNightAnimator();
                break;
            case SKY_BECOMES_SUNSET:
                mAnimator = newSkyBecomesSunsetAnimator();
                break;
            case SUN_GOES_UP:
                mAnimator = newSunGoesUpAnimator();
                break;
            default:
                break;
        }
        mAnimator.addListener(this);
    }

    private Animator newPulsateAnimator() {
        int actualRadius = mSunsetState.getSunActualRadius();
        int currentRadius = mSunsetState.getSunCurrentRadius();
        ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mSunView.getLayoutParams().width = value;
                mSunView.getLayoutParams().height = value;
                mSunsetState.setSunCurrentPosition(mSunView.getTop());
                mSunsetState.setSunTopPosition(mSunView.getTop());
                mSunsetState.setSunCurrentRadius(value);
                mSunView.requestLayout();
            }
        };
        if (currentRadius > actualRadius) {
            ValueAnimator animatorToActual = ValueAnimator
                    .ofInt(currentRadius, actualRadius);
            animatorToActual.setDuration(
                    (SunsetState.SUN_PULSATING_PERIOD / 2) * (currentRadius - actualRadius) / actualRadius
            );
            animatorToActual.addUpdateListener(listener);
            return animatorToActual;
        } else {
            ValueAnimator animator = ValueAnimator.ofInt(actualRadius, 2 * actualRadius, actualRadius);
            animator.setDuration(SunsetState.SUN_PULSATING_PERIOD);
            animator.addUpdateListener(listener);
            return animator;
        }
    }

    private Animator newSunGoesDownAnimator() {
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(
                mSunView,
                "y",
                mSunsetState.getSunCurrentPosition(),
                mSunsetState.getSunBottomPosition()
        );
        heightAnimator.setDuration(mSunsetState.getSunMotionDuration());
        heightAnimator.setInterpolator(new AccelerateInterpolator(1));
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetState.setSunCurrentPosition((float) animation.getAnimatedValue());
                mSunsetState.setSunMotionDuration(
                        (int) (SunsetState.SUN_MOTION_DURATION * animation.getAnimatedFraction())
                );
            }
        });
        ObjectAnimator skyAnimator = ObjectAnimator.ofInt(
                mSkyView,
                "backgroundColor",
                mSunsetState.getCurrentSkyColor(),
                mSunsetState.getSunsetSkyColor()
        );
        skyAnimator.setDuration(mSunsetState.getSunMotionDuration());
        skyAnimator.setEvaluator(new ArgbEvaluator());
        skyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetState.setCurrentSkyColor((int) animation.getAnimatedValue());
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator).with(skyAnimator);
        return animatorSet;
    }

    private Animator newSunGoesUpAnimator() {
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(
                mSunView,
                "y",
                mSunsetState.getSunCurrentPosition(),
                mSunsetState.getSunTopPosition()
        );
        heightAnimator.setDuration(mSunsetState.getSunMotionDuration());
        heightAnimator.setInterpolator(new AccelerateInterpolator(1));
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetState.setSunCurrentPosition((float) animation.getAnimatedValue());
                mSunsetState.setSunMotionDuration(
                        (int) (SunsetState.SUN_MOTION_DURATION * animation.getAnimatedFraction())
                );
            }
        });
        ObjectAnimator skyAnimator = ObjectAnimator.ofInt(
                mSkyView,
                "backgroundColor",
                mSunsetState.getCurrentSkyColor(),
                mSunsetState.getDaySkyColor()
        );
        skyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetState.setCurrentSkyColor((int) animation.getAnimatedValue());
            }
        });
        skyAnimator.setDuration(mSunsetState.getSunMotionDuration());
        skyAnimator.setEvaluator(new ArgbEvaluator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator).with(skyAnimator);
        return animatorSet;
    }

    private Animator newSkyBecomesNightAnimator() {
        ObjectAnimator skyAnimator = ObjectAnimator.ofInt(
                mSkyView,
                "backgroundColor",
                mSunsetState.getCurrentSkyColor(),
                mSunsetState.getNightSkyColor()
        );
        skyAnimator.setDuration(mSunsetState.getSkyBecomesNightDuration());
        skyAnimator.setEvaluator(new ArgbEvaluator());
        skyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetState.setCurrentSkyColor((int) animation.getAnimatedValue());
                mSunsetState.setSkyBecomesNightDuration(
                        (int) ((SunsetState.SUNSET_DURATION - SunsetState.SUN_MOTION_DURATION) * animation.getAnimatedFraction())
                );
            }
        });
        return skyAnimator;
    }

    private Animator newSkyBecomesSunsetAnimator() {
        ObjectAnimator skyAnimator = ObjectAnimator.ofInt(
                mSkyView,
                "backgroundColor",
                mSunsetState.getCurrentSkyColor(),
                mSunsetState.getSunsetSkyColor()
        );
        skyAnimator.setDuration(mSunsetState.getSkyBecomesNightDuration());
        skyAnimator.setEvaluator(new ArgbEvaluator());
        skyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetState.setCurrentSkyColor((int) animation.getAnimatedValue());
                mSunsetState.setSkyBecomesNightDuration(
                        (int) ((SunsetState.SUNSET_DURATION - SunsetState.SUN_MOTION_DURATION) * animation.getAnimatedFraction())
                );
            }
        });
        return skyAnimator;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        Log.d(TAG, "onAnimationStart, " + "mAnimatorType: " + mAnimatorType);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Log.d(TAG, "onAnimationEnd, " + "mAnimatorType: " + mAnimatorType);
        setAnimatorType(mAnimatorType, mIsCancelled);
        setAnimator(mAnimatorType);
        if (mIsCancelled || mAnimatorType != AnimatorTypes.SKY_BECOMES_SUNSET) {
            mAnimator.start();
        }
        mIsCancelled = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        Log.d(TAG, "onAnimationCancel, " + "mAnimatorType: " + mAnimatorType);
        mIsCancelled = true;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}

enum AnimatorTypes {
    PULSATING,
    SUN_GOES_DOWN,
    SKY_BECOMES_NIGHT,
    SKY_BECOMES_SUNSET,
    SUN_GOES_UP
}
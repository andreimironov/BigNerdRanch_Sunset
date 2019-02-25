package com.andreimironov.sunset;

import androidx.annotation.NonNull;

public class SunsetState {
    public static final int SUN_PULSATING_PERIOD = 2000;
    public static final int SUN_MOTION_DURATION = 3000;
    public static final int SUNSET_DURATION = 4000;

    private int mSunMotionDuration = SUN_MOTION_DURATION;
    private int mSkyBecomesNightDuration = SUNSET_DURATION - SUN_MOTION_DURATION;

    private int mSunActualRadius;
    private int mSunCurrentRadius;

    private float mSunTopPosition;
    private float mSunBottomPosition;
    private float mSunCurrentPosition;

    private int mDaySkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private int mCurrentSkyColor;

    public SunsetState(
            int sunActualRadius,
            float sunTopPosition,
            float sunBottomPosition,
            int daySkyColor,
            int sunsetSkyColor,
            int nightSkyColor
    ) {
        mSunActualRadius = sunActualRadius;
        mSunCurrentRadius = mSunActualRadius;
        mSunTopPosition = sunTopPosition;
        mSunBottomPosition = sunBottomPosition;
        mSunCurrentPosition = mSunTopPosition;
        mDaySkyColor = daySkyColor;
        mSunsetSkyColor = sunsetSkyColor;
        mNightSkyColor = nightSkyColor;
        mCurrentSkyColor = mDaySkyColor;
    }

    public int getSunActualRadius() {
        return mSunActualRadius;
    }

    public int getSunCurrentRadius() {
        return mSunCurrentRadius;
    }

    public void setSunCurrentRadius(int sunCurrentRadius) {
        mSunCurrentRadius = sunCurrentRadius;
    }

    public void setSunTopPosition(float sunTopPosition) {
        mSunTopPosition = sunTopPosition;
    }

    public void setSunCurrentPosition(float sunCurrentPosition) {
        mSunCurrentPosition = sunCurrentPosition;
    }

    public float getSunCurrentPosition() {
        return mSunCurrentPosition;
    }

    public float getSunTopPosition() {
        return mSunTopPosition;
    }

    public float getSunBottomPosition() {
        return mSunBottomPosition;
    }

    public void setSunMotionDuration(int sunMotionDuration) {
        mSunMotionDuration = sunMotionDuration;
    }

    public int getSkyBecomesNightDuration() {
        return mSkyBecomesNightDuration;
    }

    public void setSkyBecomesNightDuration(int skyBecomesNightDuration) {
        mSkyBecomesNightDuration = skyBecomesNightDuration;
    }

    public int getSunMotionDuration() {
        return mSunMotionDuration;
    }

    public void setCurrentSkyColor(int currentSkyColor) {
        mCurrentSkyColor = currentSkyColor;
    }

    public int getCurrentSkyColor() {
        return mCurrentSkyColor;
    }

    public int getDaySkyColor() {
        return mDaySkyColor;
    }

    public int getSunsetSkyColor() {
        return mSunsetSkyColor;
    }

    public int getNightSkyColor() {
        return mNightSkyColor;
    }

    @NonNull
    @Override
    public String toString() {
        return getClass().getName() + "[" +
                "mSunMotionDuration:" + mSunMotionDuration + ", " +
                "mSkyBecomesNightDuration:" + mSkyBecomesNightDuration + ", " +
                "mSunCurrentRadius:" + mSunCurrentRadius + ", " +
                "mSunCurrentPosition:" + mSunCurrentPosition + ", " +
                "mCurrentSkyColor:" + mCurrentSkyColor +
                "]";
    }
}
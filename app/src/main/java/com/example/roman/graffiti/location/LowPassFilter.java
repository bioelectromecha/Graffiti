package com.example.roman.graffiti.location;

/**
 * Created by roman on 8/18/17.
 */

public class LowPassFilter {
    private final static float mSmoothFactor =  0.5f;
    private final static float mSmoothTreshold = 30f;
    private float mOldAzimuth = 0;

    public LowPassFilter() {
    }

    public float getAzimuth(float newAzimuth) {
        if (Math.abs(newAzimuth - mOldAzimuth) < 180) {
            if (Math.abs(newAzimuth - mOldAzimuth) > mSmoothTreshold) {
                mOldAzimuth = newAzimuth;
            } else {
                mOldAzimuth = mOldAzimuth + mSmoothFactor * (newAzimuth - mOldAzimuth);
            }
        } else {
            if (360.0 - Math.abs(newAzimuth - mOldAzimuth) > mSmoothTreshold) {
                mOldAzimuth = newAzimuth;
            } else {
                if (mOldAzimuth > newAzimuth) {
                    mOldAzimuth = (mOldAzimuth + mSmoothFactor * ((360 + newAzimuth - mOldAzimuth) % 360) + 360) % 360;
                } else {
                    mOldAzimuth = (mOldAzimuth - mSmoothFactor * ((360 - newAzimuth + mOldAzimuth) % 360) + 360) % 360;
                }
            }
        }
        return mOldAzimuth;
    }
}

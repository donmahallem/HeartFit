package com.github.donmahallem.heartfit.pagination;

/**
 * Created on 10.11.2018.
 */
public class WeightTimestamp {
    private final float mWeight;
    private final long mTimestamp;

    public WeightTimestamp(long timestamp,float weight) {
        mWeight = weight;
        mTimestamp = timestamp;
    }

    public float getWeight() {
        return mWeight;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
}

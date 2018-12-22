package com.github.donmahallem.heartfit.viewmodel;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.List;

public class DataSummary{
    private final int mCalories;
    private final int mSteps;
    private final int mMoveMinutes;
    private final float mHeartRateMin;
    private final float mHeartRateMax;
    private final float mHeartRateAverage;
    private final boolean mHasHeartRate;
    private final boolean mHasSteps;

    private DataSummary(Builder builder) {
        this.mCalories=builder.mCalories;
        this.mHasHeartRate=builder.mHasHeartRate;
        this.mHeartRateMin=builder.mHeartRateMin;
        this.mHeartRateMax=builder.mHeartRateMax;
        this.mHeartRateAverage=builder.mHeartRateAverage;
        this.mMoveMinutes=builder.mMoveMinutes;
        this.mSteps=builder.mSteps;
        this.mHasSteps= builder.mHasSteps;
    }

    public int getCalories() {
        return mCalories;
    }

    public int getSteps() {
        return mSteps;
    }

    public int getMoveMinutes() {
        return mMoveMinutes;
    }

    public float getHeartRateMin() {
        return mHeartRateMin;
    }

    public float getHeartRateMax() {
        return mHeartRateMax;
    }

    public float getHeartRateAverage() {
        return mHeartRateAverage;
    }

    public boolean isHasHeartRate() {
        return mHasHeartRate;
    }

    public boolean isHasStepCount() {
        return true;
    }

    public static class Builder{
        public int mMoveMinutes;
        public int mSteps;
        public int mCalories;
        private float mHeartRateAverage;
        private float mHeartRateMin;
        private float mHeartRateMax;

        private boolean mHasHeartRate=false;
        private boolean mHasSteps=false;

        public Builder(){

        }

        public DataSummary build(){
            return new DataSummary(this);
        }

        public void put(DataSet dataSet){
            if(dataSet.getDataType().equals(DataType.AGGREGATE_HEART_RATE_SUMMARY)){
                this.mHeartRateMin=dataSet.getDataPoints().get(0).getValue(Field.FIELD_MIN).asFloat();
                this.mHeartRateMax=dataSet.getDataPoints().get(0).getValue(Field.FIELD_MAX).asFloat();
                this.mHeartRateAverage=dataSet.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat();
                this.mHasHeartRate=true;
            }else if(dataSet.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)){
                this.mSteps=dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                this.mHasSteps=true;
            }
        }
        public void put(List<DataSet> dataSetList){
            for(DataSet dataSet:dataSetList)
                this.put(dataSet);
        }
    }

    @Override
    public String toString() {
        return "DataSummary{" +
                "mCalories=" + mCalories +
                ", mSteps=" + mSteps +
                ", mMoveMinutes=" + mMoveMinutes +
                ", mHeartRateMin=" + mHeartRateMin +
                ", mHeartRateMax=" + mHeartRateMax +
                ", mHeartRateAverage=" + mHeartRateAverage +
                ", mHasHeartRate=" + mHasHeartRate +
                '}';
    }
}
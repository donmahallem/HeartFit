package com.github.donmahallem.sessionviewer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.donmahallem.common.recycler.model.OffsetDateTimeConverter;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.concurrent.TimeUnit;

public class WorkoutExercise implements Parcelable {
    private final OffsetDateTime mStartTime;
    private final OffsetDateTime mEndTime;
    private final int mRepetitions;
    private final float mResistance;
    private final long mDuration;
    private final int mResistanceType;
    private final String mExercise;

    private WorkoutExercise(Builder builder) {
        this.mStartTime=builder.mStartTime;
        this.mEndTime=builder.mEndTime;
        this.mRepetitions=builder.mRepetitions;
        this.mResistance=builder.mResistance;
        this.mDuration=builder.mDuration;
        this.mResistanceType=builder.mResistanceType;
        this.mExercise=builder.mExercise;
    }

    private WorkoutExercise(Parcel parcel){
        this.mStartTime=OffsetDateTimeConverter.toDate(parcel.readString());
        this.mEndTime=OffsetDateTimeConverter.toDate(parcel.readString());
        this.mRepetitions=parcel.readInt();
        this.mResistance=parcel.readFloat();
        this.mDuration=parcel.readLong();
        this.mResistanceType=parcel.readInt();
        this.mExercise=parcel.readString();
    }

    public static WorkoutExercise create(DataPoint dataPoint){
        final WorkoutExercise.Builder builder=new Builder();
        builder.setStartTime(dataPoint.getStartTime(TimeUnit.MILLISECONDS));
        builder.setEndTime(dataPoint.getEndTime(TimeUnit.MILLISECONDS));
        builder.setExercise(dataPoint.getValue(Field.FIELD_EXERCISE).asString());
        builder.setRepetitions(dataPoint.getValue(Field.FIELD_REPETITIONS).asInt());
        builder.setResistanceType(dataPoint.getValue(Field.FIELD_RESISTANCE_TYPE).asInt());
        builder.setResistance(dataPoint.getValue(Field.FIELD_RESISTANCE).asFloat());
        builder.setDuration(dataPoint.getValue(Field.FIELD_DURATION).asInt());
        return builder.build();
    }

    public OffsetDateTime getStartTime() {
        return mStartTime;
    }

    public OffsetDateTime getEndTime() {
        return mEndTime;
    }

    public int getRepetitions() {
        return mRepetitions;
    }

    public float getResistance() {
        return mResistance;
    }

    public long getDuration() {
        return mDuration;
    }

    public int getResistanceType() {
        return mResistanceType;
    }

    public String getExercise() {
        return mExercise;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(OffsetDateTimeConverter.toLong(this.mStartTime));
        parcel.writeString(OffsetDateTimeConverter.toLong(this.mEndTime));
        parcel.writeInt(this.mRepetitions);
        parcel.writeFloat(this.mResistance);
        parcel.writeLong(this.mDuration);
        parcel.writeInt(this.mResistanceType);
        parcel.writeString(this.mExercise);
    }
    public static final Parcelable.Creator<WorkoutExercise> CREATOR
            = new Parcelable.Creator<WorkoutExercise>() {
        public WorkoutExercise createFromParcel(Parcel in) {
            return new WorkoutExercise(in);
        }

        public WorkoutExercise[] newArray(int size) {
            return new WorkoutExercise[size];
        }
    };

    public static class Builder{


        private OffsetDateTime mStartTime;
        private OffsetDateTime mEndTime;
        private int mRepetitions;
        private float mResistance;
        private long mDuration;
        private int mResistanceType;
        private String mExercise;

        public WorkoutExercise build(){
            return new WorkoutExercise(this);
        }

        public Builder setStartTime(long startTime) {
            this.mStartTime=OffsetDateTime.ofInstant(Instant.ofEpochMilli(startTime),ZoneId.systemDefault());
            return this;
        }
        public Builder setEndTime(long endTime) {
            this.mEndTime=OffsetDateTime.ofInstant(Instant.ofEpochMilli(endTime),ZoneId.systemDefault());
            return this;
        }
        public Builder setRepetitions(int repetitions){
            this.mRepetitions=repetitions;
            return this;
        }

        public Builder setResistance(float resistance){
            this.mResistance=resistance;
            return this;
        }

        public Builder setDuration(long duration){
            this.mDuration=duration;
            return this;
        }

        public Builder setResistanceType(int resistanceType){
            this.mResistanceType=resistanceType;
            return this;
        }
        public Builder setExercise(String exercise){
            this.mExercise=exercise;
            return this;
        }
    }
}

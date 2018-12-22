package com.github.donmahallem.heartfit.db;

import com.google.android.gms.fitness.data.Field;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetDateTime;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "workout_exercise_table")
public class WorkoutExercise {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private long mId=0;
    @ColumnInfo(name = "exercise")
    private String mExercise;
    @ColumnInfo(name = "repetitions")
    private int mRepetitions;
    @ColumnInfo(name = "resistance")
    private float mResistance;
    @ColumnInfo(name = "resistance_type")
    private int mResistanceType;
    @ColumnInfo(name = "start_time")
    @TypeConverters({OffsetDateTimeConverter.class})
    private OffsetDateTime mStartTime;
    @ColumnInfo(name = "end_time")
    @TypeConverters({OffsetDateTimeConverter.class})
    private OffsetDateTime mEndTime;
    @ColumnInfo(name="submitted")
    private boolean mSubmitted=false;
/*
    @Ignore
    public WorkoutExercise(){

    }
    public WorkoutExercise(long id,String exercise,int repetitions,float resistance,int resistanceType,OffsetDateTime startTime,OffsetDateTime endTime,boolean submitted){
        this.mId=id;
        this.mExercise=exercise;
        this.mRepetitions=repetitions;
        this.mResistance=resistance;
        this.mResistanceType=resistanceType;
        this.mStartTime=startTime;
        this.mEndTime=endTime;
        this.mSubmitted=submitted;
    }*/
    @NonNull
    public long getId() {
        return mId;
    }

    public void setId(@NonNull long id) {
        mId = id;
    }

    public String getExercise() {
        return mExercise;
    }

    public void setExercise(String exercise) {
        mExercise = exercise;
    }

    public int getRepetitions() {
        return mRepetitions;
    }

    public void setRepetitions(int repetitions) {
        mRepetitions = repetitions;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public int getResistanceType() {
        return mResistanceType;
    }

    public void setResistanceType(int resistanceType) {
        mResistanceType = resistanceType;
    }

    public OffsetDateTime getStartTime() {
        return mStartTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        mStartTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return mEndTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        mEndTime = endTime;
    }

    public boolean isSubmitted() {
        return mSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        mSubmitted = submitted;
    }
}
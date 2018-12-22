package com.github.donmahallem.heartfit.db;

import com.google.android.gms.fitness.data.DataType;

import androidx.room.Entity;

@Entity(tableName = "float_datapoint_table")
public class FloatDataPoint {
    private DataType mDataType;
    private float mValue;

    public void setDataType(DataType dataType) {
        this.mDataType = dataType;
    }

    public float getValue() {
        return this.mValue;
    }

    public DataType getDataType() {
        return mDataType;
    }

    public void setValue(float value) {
        mValue = value;
    }
}

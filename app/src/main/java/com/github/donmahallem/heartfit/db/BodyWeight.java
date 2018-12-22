package com.github.donmahallem.heartfit.db;

import org.threeten.bp.OffsetDateTime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "body_weight_table")
public class BodyWeight {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "timestamp")
    @TypeConverters({OffsetDateTimeConverter.class})
    private OffsetDateTime mTimestamp;

    @ColumnInfo(name="weight")
    private float mWeight;

    public OffsetDateTime getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        mTimestamp = timestamp;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }
}

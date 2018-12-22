package com.github.donmahallem.heartfit.db;

import org.threeten.bp.OffsetDateTime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "body_fat_percentage_table")
public class BodyFatPercentage {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "timestamp")
    @TypeConverters({OffsetDateTimeConverter.class})
    private OffsetDateTime mTimestamp;

    @ColumnInfo(name="percentage")
    private float mPercentage;

    public OffsetDateTime getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        mTimestamp = timestamp;
    }

    public float getPercentage() {
        return mPercentage;
    }

    public void setPercentage(float percentage) {
        mPercentage = percentage;
    }
}

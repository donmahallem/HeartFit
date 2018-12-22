package com.github.donmahallem.heartfit.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {WorkoutExercise.class,BodyWeight.class,BodyFatPercentage.class},
        version = 3)
@TypeConverters({OffsetDateTimeConverter.class})
public abstract class CacheDatabase extends RoomDatabase {
    public abstract WorkoutExerciseDao workoutDao();
    private static volatile CacheDatabase INSTANCE;
    public abstract BodyWeightDao bodyWeightDao();

    public static CacheDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CacheDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CacheDatabase.class, "workout_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract BodyFatPercentageDao bodyFatDao();
}
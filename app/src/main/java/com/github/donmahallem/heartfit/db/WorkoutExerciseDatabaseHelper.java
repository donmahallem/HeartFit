package com.github.donmahallem.heartfit.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 17.11.2018.
 */
public class WorkoutExerciseDatabaseHelper extends SQLiteOpenHelper {
    public final static String COLUMN_ID="_id";
    public final static String COLUMN_REPITITIONS="_repititions";
    public final static String COLUMN_RESISTANCE="_resistance";
    public final static String COLUMN_RESISTANCE_TYPE="_resistancetype";
    public final static String COLUMN_WORKOUT_EXERCISE="_workoutexercise";
    public final static String COLUMN_START_TIME="_starttime";
    public final static String COLUMN_END_TIME="_endtime";
    public WorkoutExerciseDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

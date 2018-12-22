package com.github.donmahallem.heartfit.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.sql.Timestamp;

/**
 * Created on 17.11.2018.
 */
public class WorkoutExerciseDatabase {
    public final static int DATABASE_VERSION = 0;
    private final HeartDbHelper mDbHelper;

    private WorkoutExerciseDatabase(Context context) {

        this.mDbHelper = new HeartDbHelper(context, "heartrate.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
                return null;
            }
        }, DATABASE_VERSION);
    }

    public void insertWorkoutExercise(final String workoutExercise,
                                      final int repititions,
                                      final float resistance,
                                      final LocalDateTime startTime,
                                      final LocalDateTime endTime,
                                      final int resistanceType){
        this.insertWorkoutExercise(workoutExercise,repititions,resistance,startTime.atZone(ZoneId.systemDefault()),endTime.atZone(ZoneId.systemDefault()),resistanceType);
    }
    public void insertWorkoutExercise(final String workoutExercise,
                                      final int repititions,
                                      final float resistance,
                                      final ZonedDateTime startTime,
                                      final ZonedDateTime endTime,
                                      final int resistanceType){
        ContentValues insertValues = new ContentValues();
        insertValues.put(WorkoutExerciseDatabaseHelper.COLUMN_WORKOUT_EXERCISE, "Electricity");
        insertValues.put(WorkoutExerciseDatabaseHelper.COLUMN_REPITITIONS,repititions);
        insertValues.put(WorkoutExerciseDatabaseHelper.COLUMN_RESISTANCE,resistance);
        insertValues.put(WorkoutExerciseDatabaseHelper.COLUMN_START_TIME,startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        insertValues.put(WorkoutExerciseDatabaseHelper.COLUMN_END_TIME,startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        insertValues.put(WorkoutExerciseDatabaseHelper.COLUMN_RESISTANCE_TYPE,resistanceType);
    }

    public void a(Cursor cursor){
        String b="";
        ZonedDateTime.parse(b,DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

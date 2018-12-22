package com.github.donmahallem.heartfit.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.SkipQueryVerification;
import androidx.room.Update;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao()
public interface WorkoutExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(final WorkoutExercise workoutExercise);

    @Query("SELECT * from workout_exercise_table ORDER BY  datetime(start_time) ASC")
    Maybe<List<WorkoutExercise>> getAllWords();
    @Query("SELECT * from workout_exercise_table ORDER BY  datetime(start_time) ASC")
    Flowable<List<WorkoutExercise>> getAllWordsFlowable();

    @Query("SELECT * from workout_exercise_table WHERE submitted = 0")
    Maybe<List<WorkoutExercise>> getAllNonSubmitted();

    @Query("SELECT * FROM workout_exercise_table WHERE id = :editId")
    Single<WorkoutExercise> getWorkout(final long editId);

    @Update()
    Single<Integer> updateWorkout(final WorkoutExercise... workoutExercises);


    @Delete
    Single<Integer> deleteWorkouts(final WorkoutExercise... user);

    @SkipQueryVerification
    @Query("DELETE FROM workout_exercise_table WHERE id  IN(:exerciseIds)")
    int deleteWorkouts(final long... exerciseIds);
}
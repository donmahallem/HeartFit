package com.github.donmahallem.heartfit.db;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

@Dao()
public abstract class BodyFatPercentageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insert(final BodyFatPercentage workoutExercise);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Single<Long> insertSingle(final BodyFatPercentage workoutExercise);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(final List<BodyFatPercentage> workoutExercise);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Completable insertSingle(final List<BodyFatPercentage> workoutExercise);

    @Query("SELECT * from body_fat_percentage_table ORDER BY  datetime(timestamp) ASC")
    public abstract Maybe<List<BodyFatPercentage>> getAllWords();

    @Query("SELECT * from body_fat_percentage_table ORDER BY  datetime(timestamp) ASC")
    public abstract Flowable<List<BodyFatPercentage>> getAllWordsFlowable();

    @Query("SELECT * from body_fat_percentage_table WHERE datetime(:start)<=datetime(timestamp) AND datetime(:end)>=datetime(timestamp) ORDER BY datetime(timestamp) ASC")
    public abstract Flowable<List<BodyFatPercentage>> getArea(OffsetDateTime start, OffsetDateTime end);

    @Update()
    public abstract Single<Integer> updateWorkout(final BodyFatPercentage... workoutExercises);


    @Delete
    public abstract Single<Integer> deleteWorkouts(final BodyFatPercentage... user);

    @Query("DELETE FROM body_fat_percentage_table WHERE datetime(timestamp)>=datetime(:from) AND datetime(timestamp)<=datetime(:to)")
    public abstract int deleteRange(OffsetDateTime from, OffsetDateTime to);

    public Single<Integer> deleteRangeSingle(final OffsetDateTime from, final OffsetDateTime to){
        return Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                emitter.onSuccess(deleteRange(from,to));
            }
        });
    }
    @Transaction
    public void updateRange(OffsetDateTime from, OffsetDateTime to,  List<BodyFatPercentage> bodyWeightList){
        deleteRange(from,to);
        insert(bodyWeightList);
    }
    @Transaction
    public Completable updateRangeCompletable(final OffsetDateTime from, final OffsetDateTime to, final List<BodyFatPercentage> bodyWeightList){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                updateRange(from,to,bodyWeightList);
                emitter.onComplete();
            }
        });
    }

}

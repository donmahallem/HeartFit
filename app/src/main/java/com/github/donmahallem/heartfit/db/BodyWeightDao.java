package com.github.donmahallem.heartfit.db;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.SkipQueryVerification;
import androidx.room.Transaction;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao()
public abstract class BodyWeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Single<Long> insert(final BodyWeight workoutExercise);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(final List<BodyWeight> workoutExercise);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Completable insertCompletable(final List<BodyWeight> workoutExercise);

    @Query("SELECT * from body_weight_table ORDER BY  datetime(timestamp) ASC")
    public abstract Maybe<List<BodyWeight>> getAllWords();
    @Query("SELECT * from body_weight_table ORDER BY  datetime(timestamp) ASC")
    public abstract Flowable<List<BodyWeight>> getAllWordsFlowable();

    @Query("SELECT * from body_weight_table WHERE datetime(:start)<=datetime(timestamp) AND datetime(:end)>=datetime(timestamp) ORDER BY datetime(timestamp) ASC")
    public abstract Flowable<List<BodyWeight>> getArea(OffsetDateTime start,OffsetDateTime end);

    @Update()
    public abstract Single<Integer> updateWorkout(final BodyWeight... workoutExercises);


    @Delete
    public abstract Single<Integer> deleteWorkouts(final BodyWeight... user);

    @Query("DELETE FROM body_weight_table WHERE datetime(timestamp)>=datetime(:from) AND datetime(timestamp)<=datetime(:to)")
    public abstract int deleteBodyWeightRange(OffsetDateTime from,OffsetDateTime to);

    @Transaction
    public void updateRange(OffsetDateTime from, OffsetDateTime to,  List<BodyWeight> bodyWeightList){
        deleteBodyWeightRange(from,to);
        insert(bodyWeightList);
    }
    @Transaction
    public Completable updateRangeCompletable(final OffsetDateTime from, final OffsetDateTime to, final List<BodyWeight> bodyWeightList){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                updateRange(from, to, bodyWeightList);
                emitter.onComplete();
            }
        });
    }
}

package com.github.donmahallem.heartfit.viewmodel;

import android.content.Context;
import android.os.Bundle;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.db.OffsetDateTimeConverter;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.fit.FitResourceUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.WorkoutExercises;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.concurrent.TimeUnit;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class InsertActivityViewModel extends ViewModel {

    private static final String KEY_EXERCISE_START_TIME = "key_exercise_start_time";
    private static final String KEY_EXERCISE_END_TIME = "key_exercise_end_time";

    private final BehaviorSubject<Boolean> mUseTimerSubject = BehaviorSubject.createDefault(true);
    private final BehaviorSubject<OffsetDateTime> mStartTimeSubject = BehaviorSubject.createDefault(OffsetDateTime.now());
    private final Flowable<OffsetDateTime> mStartTimeFlowable = this.mStartTimeSubject.toFlowable(BackpressureStrategy.LATEST).share();
    private final BehaviorSubject<OffsetDateTime> mEndTimeSubject = BehaviorSubject.createDefault(OffsetDateTime.now().plusMinutes(1));
    private final Flowable<OffsetDateTime> mEndTimeFlowable = this.mEndTimeSubject.toFlowable(BackpressureStrategy.LATEST).share();
    private final BehaviorSubject<Integer> mResistanceTypeSubject = BehaviorSubject.createDefault(Field.RESISTANCE_TYPE_UNKNOWN);
    private final BehaviorSubject<String> mWorkoutExerciseSubject = BehaviorSubject.createDefault(WorkoutExercises.ARNOLD_PRESS);
    private final BehaviorSubject<Integer> mRepititionsSubject = BehaviorSubject.createDefault(0);
    private final BehaviorSubject<Float> mResistanceSubject = BehaviorSubject.createDefault(0f);
    private final Flowable<Long> mTimeDifferenceFlowable = Flowable
            .combineLatest(this.mStartTimeFlowable, this.mEndTimeFlowable,
                    new BiFunction<OffsetDateTime, OffsetDateTime, Long>() {
                        @Override
                        public Long apply(OffsetDateTime OffsetDateTime, OffsetDateTime OffsetDateTime2) throws Exception {
                            return ChronoUnit.MILLIS.between(OffsetDateTime, OffsetDateTime2);
                        }
                    }).share();

    public InsertActivityViewModel() {
    }

    public Flowable<Long> getTimeDifferenceFlowable() {
        return this.mTimeDifferenceFlowable;
    }

    public String getWorkoutExercise() {
        return this.mWorkoutExerciseSubject.getValue();
    }

    public void setWorkoutExercise(String workoutExercise) {
        Timber.d("setWorkoutExercise(%s)", workoutExercise);
        this.mWorkoutExerciseSubject.onNext(workoutExercise);
    }

    public void setStartTime(int hour, int minute) {
        final OffsetDateTime newOffsetDateTime = this.mStartTimeSubject
                .getValue()
                .withHour(hour)
                .withMinute(minute);
        Timber.d("TIME SET");
        this.mStartTimeSubject.onNext(newOffsetDateTime);
    }

    public void setEndTime(int hour, int minute) {
        final OffsetDateTime newOffsetDateTime = this.mEndTimeSubject
                .getValue()
                .withHour(hour)
                .withMinute(minute);
        this.mEndTimeSubject.onNext(newOffsetDateTime);
    }

    public void setStartDate(int year, int month, int day) {
        final OffsetDateTime newOffsetDateTime = this.mStartTimeSubject
                .getValue()
                .withYear(year)
                .withMonth(month)
                .withDayOfMonth(day);
        this.mStartTimeSubject.onNext(newOffsetDateTime);
    }

    @Nullable
    public OffsetDateTime getStartTime() {
        return this.mStartTimeSubject.getValue();
    }

    public void setStartTime(OffsetDateTime time) {
        this.mStartTimeSubject.onNext(time);
    }

    @Nullable
    public OffsetDateTime getEndTime() {
        return this.mEndTimeSubject.getValue();
    }

    public void setEndTime(OffsetDateTime time) {
        this.mEndTimeSubject.onNext(time);
    }

    public float getResistance() {
        if (this.mResistanceSubject.getValue() == null)
            return 0f;
        return this.mResistanceSubject.getValue();
    }

    public void setResistance(@FloatRange(from = 0) float resistance) {
        this.mResistanceSubject.onNext(resistance);
    }

    @FitResourceUtil.ResistanceType
    public int getResistanceType() {
        if (this.mResistanceTypeSubject.getValue() == null)
            return Field.RESISTANCE_TYPE_UNKNOWN;
        return this.mResistanceTypeSubject.getValue();
    }

    public void setResistanceType(@FitResourceUtil.ResistanceType int resistanceType) {
        this.mResistanceTypeSubject.onNext(resistanceType);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_EXERCISE_START_TIME, OffsetDateTimeConverter.toLong(this.getStartTime()));
        outState.putString(KEY_EXERCISE_END_TIME, OffsetDateTimeConverter.toLong(this.getEndTime()));
    }

    public void onRestoreInstanceState(Bundle inState) {
        if (inState == null) {
            Timber.d("instate is null");
            this.mStartTimeSubject.onNext(OffsetDateTime.now());
            this.mEndTimeSubject.onNext(OffsetDateTime.now().plusSeconds(30));
            return;
        }
        this.setStartTime(OffsetDateTimeConverter.toDate(inState.getString(KEY_EXERCISE_START_TIME)));
        this.setEndTime(OffsetDateTimeConverter.toDate(inState.getString(KEY_EXERCISE_END_TIME)));
    }

    public Flowable<OffsetDateTime> getStartTimeFlowable() {
        return this.mStartTimeSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<OffsetDateTime> getEndTimeFlowable() {
        return this.mEndTimeSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public void setEndDate(int year, int month, int day) {
        final OffsetDateTime newOffsetDateTime = this.mEndTimeSubject
                .getValue()
                .withYear(year)
                .withMonth(month)
                .withDayOfMonth(day);
        this.mEndTimeSubject.onNext(newOffsetDateTime);
    }

    public void toggleTimerMode() {
        if (this.mUseTimerSubject.getValue() == null)
            return;
        this.mUseTimerSubject.onNext(!this.mUseTimerSubject.getValue());
    }

    public void setTimerMode(boolean useTimer) {
        this.mUseTimerSubject.onNext(useTimer);
    }

    public Flowable<Boolean> getUseTimerFlowable() {
        return this.mUseTimerSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<String> getWorkoutExerciseFlowable() {
        return this.mWorkoutExerciseSubject.toFlowable(BackpressureStrategy.LATEST);
    }


    public Single<Integer> update(CacheDatabase cacheDatabase,long id) {
        final WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setId(id);
        workoutExercise.setStartTime(this.mStartTimeSubject.getValue());
        workoutExercise.setEndTime(this.mEndTimeSubject.getValue());
        workoutExercise.setExercise(this.mWorkoutExerciseSubject.getValue());
        workoutExercise.setRepetitions(this.mRepititionsSubject.getValue());
        workoutExercise.setResistanceType(this.mResistanceTypeSubject.getValue());
        workoutExercise.setResistance(this.mResistanceSubject.getValue());
        return cacheDatabase
                .workoutDao()
                .updateWorkout(workoutExercise);
    }
    public Single<Long> submit(CacheDatabase cacheDatabase) {
        final WorkoutExercise workoutExercise = new WorkoutExercise();

        workoutExercise.setStartTime(this.mStartTimeSubject.getValue());
        workoutExercise.setEndTime(this.mEndTimeSubject.getValue());
        workoutExercise.setExercise(this.mWorkoutExerciseSubject.getValue());
        workoutExercise.setRepetitions(this.mRepititionsSubject.getValue());
        workoutExercise.setResistanceType(this.mResistanceTypeSubject.getValue());
        workoutExercise.setResistance(this.mResistanceSubject.getValue());
        return cacheDatabase
                .workoutDao()
                .insert(workoutExercise);
    }

    public Single<Void> submit(final Context context) {
        DataSource exerciseSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setName(context.getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .build();

        final long startTimeInMillis = this.mStartTimeSubject.getValue().toInstant().toEpochMilli();
        final long endTimeInMillis = this.mEndTimeSubject.getValue().toInstant().toEpochMilli();
        DataPoint curls = DataPoint.create(exerciseSource);
        curls.setTimeInterval(startTimeInMillis, endTimeInMillis, TimeUnit.MILLISECONDS);
        curls.getValue(Field.FIELD_EXERCISE).setString(this.mWorkoutExerciseSubject.getValue());
        curls.getValue(Field.FIELD_REPETITIONS).setInt(this.mRepititionsSubject.getValue());
        curls.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(this.mResistanceTypeSubject.getValue());
        curls.getValue(Field.FIELD_RESISTANCE).setFloat(this.mResistanceSubject.getValue());
        //In miliseconds
        curls.getValue(Field.FIELD_DURATION).setInt((int) (endTimeInMillis - startTimeInMillis));
        final DataSet weightDataSet = DataSet.create(exerciseSource);
        weightDataSet.add(curls);
        final HistoryClient historyClient = Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context));
        Timber.d("TEST: %s", curls.toString());

        return Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> emitter) throws Exception {
                final Task<Void> submitTask = historyClient.insertData(weightDataSet);
                Void result = Tasks.await(submitTask, 10, TimeUnit.SECONDS);
                if (submitTask.isSuccessful() && submitTask.isComplete()) {
                    emitter.onSuccess(result);
                } else {
                    emitter.onError(submitTask.getException());
                }
            }
        });
    }

    public void setRepititions(int repititions) {
        this.mRepititionsSubject.onNext(repititions);
    }

    public void setWorkoutExercise(WorkoutExercise workoutExercise) {
        this.mStartTimeSubject.onNext(workoutExercise.getStartTime());
        this.mEndTimeSubject.onNext(workoutExercise.getEndTime());
        this.mResistanceSubject.onNext(workoutExercise.getResistance());
        this.mRepititionsSubject.onNext(workoutExercise.getRepetitions());
        this.mWorkoutExerciseSubject.onNext(workoutExercise.getExercise());
        this.mResistanceTypeSubject.onNext(workoutExercise.getResistanceType());
    }

    public Flowable<Integer> getResistanceTypeFlowable() {
        return this.mResistanceTypeSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<Float> getResistanceFlowable() {
        return this.mResistanceSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<Integer> getRepetitionsFlowable() {
        return this.mRepititionsSubject.toFlowable(BackpressureStrategy.LATEST);
    }
}

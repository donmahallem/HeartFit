package com.github.donmahallem.heartfit.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;

import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SynchronizeWorkoutExerciseJob extends JobService {
    private static final String TAG = "SyncService";
    private Disposable mDisposable;

    public Maybe<Boolean> createObservable(final Context context, final GoogleSignInAccount googleSignInAccount){
        return CacheDatabase.getDatabase(getApplicationContext())
                .getDatabase(context)
                .workoutDao()
                .getAllWords()
                .map(new Function<List<WorkoutExercise>, DataSet>() {
                    @Override
                    public DataSet apply(List<WorkoutExercise> workoutExercises) throws Exception {

                        DataSource exerciseSource = new DataSource.Builder()
                                .setAppPackageName(context)
                                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                                .setName(context.getResources().getString(R.string.app_name))
                                .setType(DataSource.TYPE_RAW)
                                .build();

                        final DataSet weightDataSet = DataSet.create(exerciseSource);
                        for (WorkoutExercise exercise : workoutExercises) {
                            DataPoint workoutExercise = DataPoint.create(exerciseSource);
                            workoutExercise.setTimeInterval(exercise.getStartTime().toInstant().toEpochMilli(), exercise.getEndTime().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
                            workoutExercise.getValue(Field.FIELD_EXERCISE).setString(exercise.getExercise());
                            workoutExercise.getValue(Field.FIELD_REPETITIONS).setInt(exercise.getRepetitions());
                            workoutExercise.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(exercise.getResistanceType());
                            workoutExercise.getValue(Field.FIELD_RESISTANCE).setFloat(exercise.getResistance());
                            //In miliseconds
                            workoutExercise.getValue(Field.FIELD_DURATION).setInt((int)ChronoUnit.MILLIS.between(exercise.getStartTime(),exercise.getEndTime()));
                            weightDataSet.add(workoutExercise);
                        }
                        return weightDataSet;
                    }
                })
                .map(new Function<DataSet, Boolean>() {
                    @Override
                    public Boolean apply(DataSet dataSet) throws Exception {
                        final HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
                        Task<Void> task = historyClient.insertData(dataSet);
                        Tasks.await(task, 10, TimeUnit.SECONDS);
                        if (task.isSuccessful()) {
                            return true;
                        } else {
                            throw task.getException();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
    @Override
    public boolean onStartJob(final JobParameters params) {
        final GoogleSignInAccount googleSignIn=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(googleSignIn==null){
            this.jobFinished(params,true);
            return true;
        }
        this.mDisposable=createObservable(getApplicationContext(),googleSignIn)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean dataSet) throws Exception {
                        jobFinished(params,false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        jobFinished(params,true);
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(this.mDisposable!=null)
            this.mDisposable.dispose();
        return true;
    }

}
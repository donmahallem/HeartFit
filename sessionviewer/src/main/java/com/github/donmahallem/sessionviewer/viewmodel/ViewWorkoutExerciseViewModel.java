package com.github.donmahallem.sessionviewer.viewmodel;

import com.github.donmahallem.sessionviewer.model.WorkoutExercise;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class ViewWorkoutExerciseViewModel extends ViewModel {

    private final BehaviorSubject<Session> mSessionBehaviorSubject = BehaviorSubject.create();
    private final Flowable<Session> mSessionFlowable=this.mSessionBehaviorSubject.toFlowable(BackpressureStrategy.LATEST);
    public void setSession(@NonNull Session session) {
        this.mSessionBehaviorSubject.onNext(session);
    }

    public Flowable<Session> getSessionFlowable(){
        return this.mSessionFlowable;
    }

    public Session getSession(){
        return this.mSessionBehaviorSubject.getValue();
    }

    public Single<List<WorkoutExercise>> getWorkoutExercises(final HistoryClient historyClient){
        return Single.create(new SingleOnSubscribe<List<WorkoutExercise>>() {
            @Override
            public void subscribe(SingleEmitter<List<WorkoutExercise>> emitter) throws Exception {
                Timber.d("start acce");
                DataReadRequest dataReadRequest=new DataReadRequest.Builder()
                        .setTimeRange(getSession().getStartTime(TimeUnit.MILLISECONDS),getSession().getEndTime(TimeUnit.MILLISECONDS),TimeUnit.MILLISECONDS)
                        .read(DataType.TYPE_WORKOUT_EXERCISE)
                        .build();

                Task<DataReadResponse> task=historyClient.readData(dataReadRequest);
                Timber.d("DOAODAO");
                DataReadResponse result= Tasks.await(task,20,TimeUnit.SECONDS);
                Timber.d("DOAODAO");
                if(task.isSuccessful()){
                    emitter.onSuccess(convertItems(result));
                }else{
                    emitter.onError(task.getException());
                }
            }
        });
    }

    private List<WorkoutExercise> convertItems(DataReadResponse result) {
        final List<WorkoutExercise> workoutExercises=new ArrayList<>();
        for(DataSet dataSet:result.getDataSets()){
            for(DataPoint dataPoint:dataSet.getDataPoints()) {
                workoutExercises.add(WorkoutExercise.create(dataPoint));
            }
        }
        return workoutExercises;
    }
}

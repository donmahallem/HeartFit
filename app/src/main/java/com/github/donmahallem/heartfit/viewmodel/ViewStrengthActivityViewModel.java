package com.github.donmahallem.heartfit.viewmodel;

import com.github.donmahallem.heartfit.fit.StatusException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class ViewStrengthActivityViewModel extends ViewModel {

    private BehaviorSubject<Session> mSessionSubject = BehaviorSubject.create();

    @Nullable
    public Session getSession() {
        return this.mSessionSubject.getValue();
    }

    public void setSession(Session session) {
        this.mSessionSubject.onNext(session);
    }

    public Flowable<Session> getSessionObservable() {
        return this.mSessionSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<DataSummary> getCombinedInfo(final HistoryClient historyClient) {

        final DataReadRequest request = new DataReadRequest.Builder()
                .enableServerQueries()
                .aggregate(DataType.TYPE_HEART_RATE_BPM,DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_MOVE_MINUTES,DataType.AGGREGATE_MOVE_MINUTES)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED,DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA,DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketBySession(1,TimeUnit.SECONDS)
                .setTimeRange(this.getSession().getStartTime(TimeUnit.MILLISECONDS), this.getSession().getEndTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                .build();

        return Single.create(new SingleOnSubscribe<DataReadResponse>() {
            @Override
            public void subscribe(SingleEmitter<DataReadResponse> emitter) throws Exception {
                Task<DataReadResponse> task = historyClient.readData(request);
                final Trace myTrace = FirebasePerformance.getInstance().newTrace("fit_api_trace");
                myTrace.start();
                myTrace.putAttribute("datatype", DataType.TYPE_HEART_RATE_BPM.getName());
                DataReadResponse response = Tasks.await(task, 10, TimeUnit.SECONDS);
                myTrace.stop();
                if (emitter.isDisposed())
                    return;
                if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
                    emitter.onSuccess(response);
                } else {
                    emitter.onError(new StatusException(response.getStatus()));
                }
            }
        }).map(new Function<DataReadResponse, DataSummary>() {
            @Override
            public DataSummary apply(DataReadResponse dataReadResponse) throws Exception {
                DataSummary.Builder summary=new DataSummary.Builder();
                for(Bucket bucket:dataReadResponse.getBuckets()){

                    summary.put(bucket.getDataSets());
                    Timber.d("JJJ %s",bucket);
                }
                return summary.build();
            }
        });
    }


    public Single<DataReadResponse> getHeartRateData(final HistoryClient historyClient) {

        final DataReadRequest request = new DataReadRequest.Builder()
                .enableServerQueries()
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(this.getSession().getStartTime(TimeUnit.MILLISECONDS), this.getSession().getEndTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                .build();

        return Single.create(new SingleOnSubscribe<DataReadResponse>() {
            @Override
            public void subscribe(SingleEmitter<DataReadResponse> emitter) throws Exception {
                Task<DataReadResponse> task = historyClient.readData(request);
                final Trace myTrace = FirebasePerformance.getInstance().newTrace("fit_api_trace");
                myTrace.start();
                myTrace.putAttribute("datatype", DataType.TYPE_HEART_RATE_BPM.getName());
                DataReadResponse response = Tasks.await(task, 10, TimeUnit.SECONDS);
                myTrace.stop();
                if (emitter.isDisposed())
                    return;
                if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
                    emitter.onSuccess(response);
                } else {
                    emitter.onError(new StatusException(response.getStatus()));
                }
            }
        });
    }
}

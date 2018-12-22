package com.github.donmahallem.heartfit.fragments;

import com.github.donmahallem.heartfit.db.BodyFatPercentage;
import com.github.donmahallem.heartfit.db.BodyWeight;
import com.github.donmahallem.heartfit.viewmodel.TimeFrame;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created on 05.12.2018.
 */
public class GoogleFitWeightRequest2 implements Function<TimeFrame, DataReadResponse> {
    private final HistoryClient mHistoryClient;
    public GoogleFitWeightRequest2(HistoryClient historyClient) {
        this.mHistoryClient=historyClient;
    }
    @Override
    public DataReadResponse apply(TimeFrame timeFrame) throws Exception {
        DataReadRequest.Builder readRequestBuilder = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setTimeRange(timeFrame.getStartTime().toEpochSecond(), timeFrame.getEndTime().toEpochSecond(), TimeUnit.SECONDS);
        Task<DataReadResponse> task = mHistoryClient.readData(readRequestBuilder.build());
        DataReadResponse response = Tasks.await(task, 10, TimeUnit.SECONDS);
        Timber.d("DataReadResponse successful: %s", Thread.currentThread().getName());
        if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
            Timber.d("Success request");
            return response;
        } else {
            throw new IOException("Error Status " + response.getStatus());
        }
    }
}

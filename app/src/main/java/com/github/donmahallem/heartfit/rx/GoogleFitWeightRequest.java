package com.github.donmahallem.heartfit.rx;

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
import org.threeten.bp.ZoneOffset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import timber.log.Timber;

public class GoogleFitWeightRequest implements Function<TimeFrame, GoogleFitWeightRequest.Data> {
    private final HistoryClient mHistoryClient;
    public static class Data{
        List<BodyWeight> mBodyWeights=new ArrayList<>();
        List<BodyFatPercentage> mBodyFatPercentages=new ArrayList<>();
        TimeFrame mTimeFrame;
    }

    public GoogleFitWeightRequest(HistoryClient historyClient) {
        this.mHistoryClient=historyClient;
    }
    @Override
    public Data apply(TimeFrame timeFrame) throws Exception {
        DataReadRequest.Builder readRequestBuilder = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setTimeRange(timeFrame.getStartTime().toEpochSecond(), timeFrame.getEndTime().toEpochSecond(), TimeUnit.SECONDS);
        Task<DataReadResponse> task = mHistoryClient.readData(readRequestBuilder.build());
        DataReadResponse response = Tasks.await(task, 10, TimeUnit.SECONDS);
        Timber.d("DataReadResponse successful: %s", Thread.currentThread().getName());
        if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
            final Data data= convertResponse(response);
            data.mTimeFrame=timeFrame;
            return data;
        } else {
            throw new IOException("Error Status " + response.getStatus());
        }
    }

    private Data convertResponse(DataReadResponse response) {
        final Data data=new Data();
        for(DataSet dataSet:response.getDataSets()){
            for(DataPoint dataPoint:dataSet.getDataPoints()){
                if(dataPoint.getDataType().equals(DataType.TYPE_WEIGHT)){
                    data.mBodyWeights.add(convertBodyWeight(dataPoint));
                }else if(dataPoint.getDataType().equals(DataType.TYPE_BODY_FAT_PERCENTAGE)){
                    data.mBodyFatPercentages.add(convertBodyFatPercentage(dataPoint));
                }
            }
        }
        return data;
    }

    private BodyWeight convertBodyWeight(DataPoint dataPoint) {
        final BodyWeight bodyWeight=new BodyWeight();
        bodyWeight.setTimestamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(dataPoint.getTimestamp(TimeUnit.MILLISECONDS)),ZoneOffset.systemDefault()));
        bodyWeight.setWeight(dataPoint.getValue(Field.FIELD_WEIGHT).asFloat());
        return bodyWeight;
    }

    private BodyFatPercentage convertBodyFatPercentage(DataPoint dataPoint) {
        final BodyFatPercentage bodyFatPercentage=new BodyFatPercentage();
        bodyFatPercentage.setTimestamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(dataPoint.getTimestamp(TimeUnit.MILLISECONDS)),ZoneOffset.systemDefault()));
        bodyFatPercentage.setPercentage(dataPoint.getValue(Field.FIELD_PERCENTAGE).asFloat());
        return bodyFatPercentage;
    }
}

package com.github.donmahallem.heartfit.fragments;

import android.graphics.Color;

import com.github.donmahallem.heartfit.chart.MinMaxEntry;
import com.github.donmahallem.heartfit.viewmodel.TimeFrame;
import com.github.donmahallem.heartfit.viewmodel.WeightGraphFragmentViewModel;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created on 21.11.2018.
 */
public class DataHistoryRequest implements Function<TimeFrame, LineData> {
    private final HistoryClient mHistoryClient;

    public DataHistoryRequest(HistoryClient historyClient) {
        this.mHistoryClient = historyClient;
    }

    @Override
    public LineData apply(TimeFrame timeFrame) throws Exception {
        DataReadRequest.Builder readRequestBuilder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY)
                .aggregate(DataType.TYPE_BODY_FAT_PERCENTAGE, DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY)
                .setTimeRange(timeFrame.getStartTime().toEpochSecond(), timeFrame.getEndTime().toEpochSecond(), TimeUnit.SECONDS);
        switch (timeFrame.getWindowSize()){
            case WEEK:
                readRequestBuilder.bucketByTime(1,TimeUnit.HOURS);
                break;
            case MONTH:
                readRequestBuilder.bucketByTime(12,TimeUnit.HOURS);
                break;
            case YEAR:
                readRequestBuilder.bucketByTime(1,TimeUnit.DAYS);
                break;
        }
        Task<DataReadResponse> task = mHistoryClient.readData(readRequestBuilder.build());
        DataReadResponse response = Tasks.await(task, 10, TimeUnit.SECONDS);
        Timber.d("DataReadResponse successful: %s", Thread.currentThread().getName());
        if (response.getStatus().equals(Status.RESULT_SUCCESS)) {
            final LineData lineData= convertResponse(response);
            Timber.d("JJJAJFJAJF");
            return lineData;
        } else {
            throw new IOException("Error Status " + response.getStatus());
        }
    }
    public MinMaxEntry createEntry(DataPoint dataPoint){
        final float min=dataPoint.getValue(Field.FIELD_MIN).asFloat();
        final float max=dataPoint.getValue(Field.FIELD_MAX).asFloat();
        final float avg=dataPoint.getValue(Field.FIELD_AVERAGE).asFloat();
        final Instant i = Instant.ofEpochSecond(dataPoint.getTimestamp(TimeUnit.SECONDS));
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(i,Clock.systemDefaultZone().getZone());
        return new MinMaxEntry(zonedDateTime.toLocalDate().toEpochDay(),avg,min,max);
    }

    public LineData convertResponse(DataReadResponse dataReadResponse) throws Exception {
        List<Entry> weightEntries = new ArrayList<Entry>();
        List<Entry> fatEntries = new ArrayList<Entry>();

        for (Bucket bucket : dataReadResponse.getBuckets()) {
            //Timber.d("Bucket from %s to %s", start.format(DateTimeFormatter.ISO_DATE_TIME), end.format(DateTimeFormatter.ISO_DATE_TIME));
            for (DataSet dataSet : bucket.getDataSets()) {
                //Timber.d("dataset %s", dataSet.getDataType().getName());
                if (dataSet.isEmpty())
                    continue;
                if (dataSet.getDataType().equals(DataType.AGGREGATE_WEIGHT_SUMMARY)) {
                    for (DataPoint dataPoint : dataSet.getDataPoints()) {
                        weightEntries.add(createEntry(dataPoint));
                        //Timber.d("Point: %s %s %s", dataPoint.getValue(Field.FIELD_MAX).asFloat(), dataPoint.getValue(Field.FIELD_AVERAGE).asFloat(), dataPoint.getValue(Field.FIELD_MIN).asFloat());
                    }
                } else if (dataSet.getDataType().equals(DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY)) {
                    for (DataPoint dataPoint : dataSet.getDataPoints()) {
                        fatEntries.add(createEntry(dataPoint));
                    }
                }
            }

        }
        Timber.d("Entries %s %s",weightEntries.size(),fatEntries.size());
        LineDataSet weightDataSet = new LineDataSet(weightEntries, "Weight");
        weightDataSet.setDrawCircles(true);
        weightDataSet.setLineWidth(3f);
        weightDataSet.setCircleRadius(8f);
        weightDataSet.setDrawFilled(true);
        weightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        weightDataSet.setFillAlpha(128);
        weightDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        weightDataSet.setDrawCircleHole(true);
        weightDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        weightDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return (float) (98 + (Math.random() * 4 - 2));
            }
        });

        LineDataSet fatDataSet = new LineDataSet(fatEntries, "Body Fat Percentage");
        fatDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return new LineData(weightDataSet, fatDataSet);
    }
}

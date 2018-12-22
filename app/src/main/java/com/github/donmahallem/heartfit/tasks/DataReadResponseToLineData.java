package com.github.donmahallem.heartfit.tasks;

import android.app.Activity;
import android.graphics.Color;

import com.github.donmahallem.heartfit.chart.LongEntry;
import com.github.donmahallem.heartfit.viewmodel.TimeFrame;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created on 14.12.2018.
 */
public class DataReadResponseToLineData implements SuccessContinuation<DataReadResponse,LineData> {


    public DataReadResponseToLineData(TimeFrame timeFrame) {
    }

    public LongEntry createEntryType(DataPoint dataPoint, Field field) {
        final Instant i = Instant.ofEpochSecond(dataPoint.getTimestamp(TimeUnit.SECONDS));
        final OffsetDateTime zonedDateTime = OffsetDateTime.ofInstant(i, Clock.systemDefaultZone().getZone());
        return new LongEntry(zonedDateTime.toEpochSecond(), dataPoint.getValue(field).asFloat());
    }

    @NonNull
    @Override
    public Task<LineData> then(@Nullable final DataReadResponse dataReadResponse) throws Exception {
        return Tasks.call(new Callable<LineData>() {
            @Override
            public LineData call() throws Exception {
                List<Entry> weightEntries = new ArrayList<Entry>();
                List<Entry> fatEntries = new ArrayList<Entry>();
                for (DataSet dataSet : dataReadResponse.getDataSets()) {
                    Timber.d("dataset contains %s - %s", dataSet.getDataType().getName(), dataSet.getDataPoints().size());
                    if (dataSet.isEmpty())
                        continue;
                    if (dataSet.getDataType().equals(DataType.TYPE_WEIGHT)) {
                        for (DataPoint dataPoint : dataSet.getDataPoints()) {
                            weightEntries.add(createEntryType(dataPoint,Field.FIELD_WEIGHT));
                            //Timber.d("Point: %s %s %s", dataPoint.getValue(Field.FIELD_MAX).asFloat(), dataPoint.getValue(Field.FIELD_AVERAGE).asFloat(), dataPoint.getValue(Field.FIELD_MIN).asFloat());
                        }
                    } else if (dataSet.getDataType().equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
                        for (DataPoint dataPoint : dataSet.getDataPoints()) {
                            fatEntries.add(createEntryType(dataPoint,Field.FIELD_PERCENTAGE));
                        }
                    }
                }
                Timber.d("Entries %s %s", weightEntries.size(), fatEntries.size());
                LineDataSet weightDataSet = new LineDataSet(weightEntries, "Weight");
                weightDataSet.setDrawCircles(true);
                weightDataSet.setLineWidth(3f);
                weightDataSet.setCircleRadius(8f);
                weightDataSet.setDrawFilled(true);
                weightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                weightDataSet.setFillAlpha(128);
                weightDataSet.setHighLightColor(Color.rgb(2, 117, 117));
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
                fatDataSet.setHighLightColor(Color.rgb(117, 2, 117));
                LineData lineData = new LineData(weightDataSet, fatDataSet);
                return lineData;
            }
        });
    }
}

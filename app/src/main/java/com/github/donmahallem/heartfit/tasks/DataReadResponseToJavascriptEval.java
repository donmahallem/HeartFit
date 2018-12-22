package com.github.donmahallem.heartfit.tasks;

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
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created on 14.12.2018.
 */
public class DataReadResponseToJavascriptEval implements SuccessContinuation<DataReadResponse,String> {


    public DataReadResponseToJavascriptEval(TimeFrame timeFrame) {
    }

    public String createEntryType(DataPoint dataPoint, Field field) {
        final Instant i = Instant.ofEpochSecond(dataPoint.getTimestamp(TimeUnit.SECONDS));
        final OffsetDateTime zonedDateTime = OffsetDateTime.ofInstant(i, Clock.systemDefaultZone().getZone());
        if(field.equals(Field.FIELD_WEIGHT)){
            return "["+zonedDateTime.toEpochSecond()+","+dataPoint.getValue(field).asFloat()+",null]";
        }
        return "["+zonedDateTime.toEpochSecond()+",null,"+(dataPoint.getValue(field).asFloat()/100)+"]";
    }

    @NonNull
    @Override
    public Task<String> then(@Nullable final DataReadResponse dataReadResponse) throws Exception {
        return Tasks.call(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder data=new StringBuilder();
                data.append("updateData([");
                boolean primarySet=false;
                for (DataSet dataSet : dataReadResponse.getDataSets()) {
                    Timber.d("dataset contains %s - %s", dataSet.getDataType().getName(), dataSet.getDataPoints().size());
                    if (dataSet.isEmpty())
                        continue;
                    if (dataSet.getDataType().equals(DataType.TYPE_WEIGHT)) {
                        for (DataPoint dataPoint : dataSet.getDataPoints()) {
                            if(!primarySet){
                                primarySet=true;
                            }else{
                                data.append(",");
                            }
                            data.append(createEntryType(dataPoint,Field.FIELD_WEIGHT));
                            //Timber.d("Point: %s %s %s", dataPoint.getValue(Field.FIELD_MAX).asFloat(), dataPoint.getValue(Field.FIELD_AVERAGE).asFloat(), dataPoint.getValue(Field.FIELD_MIN).asFloat());
                        }
                    } else if (dataSet.getDataType().equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
                        for (DataPoint dataPoint : dataSet.getDataPoints()) {
                            if(!primarySet){
                                primarySet=true;
                            }else{
                                data.append(",");
                            }
                            data.append(createEntryType(dataPoint,Field.FIELD_PERCENTAGE));
                        }
                    }
                }
                data.append("]);");
                return data.toString();
            }
        });
    }
}

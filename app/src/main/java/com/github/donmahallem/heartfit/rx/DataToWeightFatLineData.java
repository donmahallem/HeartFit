package com.github.donmahallem.heartfit.rx;

import android.graphics.Color;

import com.github.donmahallem.heartfit.db.BodyWeight;
import com.github.donmahallem.heartfit.pagination.WeightTimestamp;
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

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import timber.log.Timber;

public class DataToWeightFatLineData implements Function<GoogleFitWeightRequest.Data, LineData> {

    private final boolean mIncludeFatData;
    private final boolean mIncludeWeightData;

    public DataToWeightFatLineData(boolean includeWeight, boolean includeFat) {
        this.mIncludeWeightData = includeWeight;
        this.mIncludeFatData = includeFat;
    }

    public Entry createEntryType(DataPoint dataPoint,Field field) {
        final Instant i = Instant.ofEpochSecond(dataPoint.getTimestamp(TimeUnit.SECONDS));
        final OffsetDateTime zonedDateTime = OffsetDateTime.ofInstant(i, Clock.systemDefaultZone().getZone());
        return new Entry(zonedDateTime.toEpochSecond(), dataPoint.getValue(field).asFloat());
    }

    @Override
    public LineData apply(GoogleFitWeightRequest.Data dataReadResponse) throws Exception {
        List<Entry> weightEntries = new ArrayList<Entry>();
        List<Entry> fatEntries = new ArrayList<Entry>();
        for(BodyWeight weightTimestamp:dataReadResponse.mBodyWeights){
            weightEntries.add(new Entry(weightTimestamp.getTimestamp().toEpochSecond(),weightTimestamp.getWeight()));
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

}

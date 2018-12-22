package com.github.donmahallem.sessionviewer.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.common.recycler.fit.FitResourceUtil;
import com.github.donmahallem.sessionviewer.R;
import com.github.donmahallem.sessionviewer.chart.BpmMarkerView;
import com.github.donmahallem.sessionviewer.chart.SlidingAverageLineChartRenderer;
import com.github.donmahallem.sessionviewer.model.WorkoutExercise;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class WorkoutExerciseDetailDialogFragment extends DialogFragment {

    private static final String KEY_WORKOUT_EXERCISE = "key_workout_exercise";
    private TextView mTxtRepetitions;
    private TextView mTxtResistanceType;
    private TextView mTxtDuration;
    private TextView mTxtResistance;
    private TextView mTxtTitle;
    private WorkoutExercise mWorkoutExercise;
    private LineChart mLineChart;
    private Disposable mHeartRateDisposable;
    private SlidingAverageLineChartRenderer mSlidingWindowRenderer;

    public static WorkoutExerciseDetailDialogFragment newInstance(WorkoutExercise workoutExercise) {
        final WorkoutExerciseDetailDialogFragment fragment = new WorkoutExerciseDetailDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_WORKOUT_EXERCISE, workoutExercise);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mWorkoutExercise = (WorkoutExercise) getArguments().getParcelable(KEY_WORKOUT_EXERCISE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_exercise_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mTxtDuration = view.findViewById(R.id.txtDurationValue);
        this.mTxtResistanceType = view.findViewById(R.id.txtResistanceTypeValue);
        this.mTxtResistance = view.findViewById(R.id.txtResistanceValue);
        this.mTxtRepetitions = view.findViewById(R.id.txtRepetitionValue);
        this.mTxtTitle = view.findViewById(R.id.txtTitle);
        this.mLineChart = view.findViewById(R.id.lineChart);
        this.initChart();
    }

    private void initChart() {
        this.mLineChart.getDescription().setText(getString(R.string.seconds_since_start));
        final XAxis xAxis=this.mLineChart.getXAxis();
        final YAxis yAxisLeft=this.mLineChart.getAxisLeft();
        final YAxis yAxisRight=this.mLineChart.getAxisRight();
        this.mLineChart.setMarker(new BpmMarkerView(getContext()));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%ds",(int)value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(false);
        xAxis.setDrawGridLines(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(false);
        this.mLineChart.setDoubleTapToZoomEnabled(false);
        this.mLineChart.setPinchZoom(false);
        this.mSlidingWindowRenderer=new SlidingAverageLineChartRenderer(this.mLineChart,this.mLineChart.getAnimator(),this.mLineChart.getViewPortHandler());
        this.mSlidingWindowRenderer.setBarColor(Color.BLACK);
        this.mLineChart.setRenderer(this.mSlidingWindowRenderer);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mTxtTitle.setText(FitResourceUtil.get(this.mWorkoutExercise.getExercise()));
        this.mTxtResistanceType.setText(FitResourceUtil.get(this.mWorkoutExercise.getResistanceType()));
        this.mTxtRepetitions.setText(String.format("%d",this.mWorkoutExercise.getRepetitions()));
        this.mTxtResistance.setText(String.format("%.3fkg",this.mWorkoutExercise.getResistance()));
        this.mTxtDuration.setText(String.format("%.3fs",this.mWorkoutExercise.getDuration()/1000f));
        this.mSlidingWindowRenderer.setRightBar(this.mWorkoutExercise.getDuration()/1000f);
        this.mHeartRateDisposable = getHeartRateData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LineData>() {
                    @Override
                    public void accept(LineData lineData) throws Exception {
                        mLineChart.setData(lineData);
                        mLineChart.invalidate();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }

    public WorkoutExercise getWorkoutExercise() {
        return this.mWorkoutExercise;
    }

    public int getColor(){
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    public Single<LineData> getHeartRateData() {
        return Single.create(new SingleOnSubscribe<LineData>() {
            @Override
            public void subscribe(SingleEmitter<LineData> emitter) throws Exception {
                HistoryClient historyClient = Fitness.getHistoryClient(getContext(), GoogleSignIn.getLastSignedInAccount(getContext()));
                final long start = getWorkoutExercise().getStartTime().toEpochSecond();
                final long end = getWorkoutExercise().getEndTime().toEpochSecond();
                Timber.d("Start: %s End: %s", getWorkoutExercise().getStartTime(), getWorkoutExercise().getEndTime());
                Timber.d("Seconds in between %s", end - start);
                DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                        .setTimeRange(start-10, end+10, TimeUnit.SECONDS)
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        .build();
                Task<DataReadResponse> task = historyClient.readData(dataReadRequest);
                DataReadResponse res = Tasks.await(task, 10, TimeUnit.SECONDS);
                if (task.isSuccessful()) {
                    emitter.onSuccess(convertData(start, res));
                } else {
                    emitter.onError(task.getException());
                }
            }

            private LineData convertData(long startTime, DataReadResponse res) {
                final List<Entry> entry = new ArrayList<>();
                Timber.d("Buckets: %s", res.getBuckets().size());
                for (DataSet dataSet : res.getDataSets()) {
                    Timber.d("DataPoint: %s", dataSet.getDataPoints().size());
                    for (DataPoint dataPoint : dataSet.getDataPoints()) {
                        entry.add(new Entry(dataPoint.getTimestamp(TimeUnit.SECONDS) - startTime, dataPoint.getValue(Field.FIELD_BPM).asFloat()));
                    }
                }/*
                for(Bucket bucket:res.getBuckets()){
                    Timber.d("DataSet: %s",bucket.getDataSets().size());
                    for(DataSet dataSet:bucket.getDataSets()){
                        Timber.d("DataPoint: %s",dataSet.getDataPoints().size());
                        for(DataPoint dataPoint:dataSet.getDataPoints()){
                            entry.add(new Entry(dataPoint.getTimestamp(TimeUnit.SECONDS)-startTime,dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()));
                        }
                    }
                }*/
                Timber.d("Dataset Size: %s", entry.size());
                LineDataSet weightDataSet = new LineDataSet(entry, "BPM");
                weightDataSet.setDrawCircles(true);
                weightDataSet.setLineWidth(2f);
                weightDataSet.setDrawValues(false);
                weightDataSet.setCircleRadius(3f);
                weightDataSet.setDrawFilled(false);
                //weightDataSet.setCubicIntensity(2);
                weightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                weightDataSet.setFillAlpha(128);
                //weightDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                weightDataSet.setColor(getColor(),128);
                weightDataSet.setCircleColor(weightDataSet.getColor());
                weightDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                weightDataSet.setDrawCircleHole(false);

                LineData lineData = new LineData(weightDataSet);
                return lineData;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mHeartRateDisposable.dispose();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}

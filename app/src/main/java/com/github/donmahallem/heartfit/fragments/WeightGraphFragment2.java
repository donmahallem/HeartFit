package com.github.donmahallem.heartfit.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.chart.LongLineChartRenderer;
import com.github.donmahallem.heartfit.tasks.DataReadResponseToLineData;
import com.github.donmahallem.heartfit.viewmodel.TimeFrame;
import com.github.donmahallem.heartfit.viewmodel.WeightGraphFragmentViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class WeightGraphFragment2 extends Fragment implements View.OnClickListener {

    private HistoryClient mHistoryClient;
    private LineChart mChart;
    private TabLayout mTabLayout;
    private ImageButton mBtnChevronRight;
    private ImageButton mBtnChevronLeft;
    private WeightGraphFragmentViewModel mViewModel;
    private Disposable mDisposableTimeFrame;
    private TextView mTxtTimeFrame;
    private Disposable mDisposableLoadData;
    private XAxis mXAxis;
    private Disposable mDisposableWatchData;

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    private static int getThemePrimaryColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.weight_history_graph, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_weight:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weight_graph, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mChart = (LineChart) view.findViewById(R.id.chart);
        this.mViewModel = ViewModelProviders.of(this).get(WeightGraphFragmentViewModel.class);
        this.mTabLayout = view.findViewById(R.id.tabLayout);
        this.mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (mViewModel.getWindowSize() != WeightGraphFragmentViewModel.WindowSize.WEEK)
                            mViewModel.setWindowSize(WeightGraphFragmentViewModel.WindowSize.WEEK);
                        break;
                    case 1:
                        if (mViewModel.getWindowSize() != WeightGraphFragmentViewModel.WindowSize.MONTH)
                            mViewModel.setWindowSize(WeightGraphFragmentViewModel.WindowSize.MONTH);
                        break;
                    case 2:
                        if (mViewModel.getWindowSize() != WeightGraphFragmentViewModel.WindowSize.YEAR)
                            mViewModel.setWindowSize(WeightGraphFragmentViewModel.WindowSize.YEAR);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        this.mBtnChevronLeft = view.findViewById(R.id.btnChevronLeft);
        this.mBtnChevronRight = view.findViewById(R.id.btnChevronRight);
        this.mBtnChevronLeft.setOnClickListener(this);
        this.mBtnChevronRight.setOnClickListener(this);
        this.mTxtTimeFrame = view.findViewById(R.id.txtTimeframe);
        this.initChart();
    }

    public void initChart() {
        this.mXAxis = mChart.getXAxis();
        this.mChart.setRenderer(new LongLineChartRenderer(this.mChart));
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setDrawGridLines(false);
        //mXAxis.setGranularity(1f); // only intervals of 1 day
        mXAxis.setTextSize(8);
        mXAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return OffsetDateTime.ofInstant(Instant.ofEpochSecond((long) value), ZoneOffset.systemDefault()).format(DateTimeFormatter.ISO_DATE);
            }
        });
        YAxis yAxisRight = mChart.getAxisRight();
        yAxisRight.setValueFormatter(new PercentFormatter());
        yAxisRight.setAxisMinimum(0);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setAxisMaximum(100);
        final YAxis yAxisLeft = this.mChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getResources().getString(R.string.x_kg, value);
            }
        });
        LineData lineData=new LineData();
        this.mChart.getDescription().setText(getString(com.github.donmahallem.common.R.string.date));
        this.mChart.setData(lineData);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mDisposableTimeFrame = this.mViewModel
                .getTimeFrameFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TimeFrame>() {
                    @Override
                    public void accept(TimeFrame timeFrame) throws Exception {
                        Timber.d("Got timeframe %s", timeFrame.toString());
                        mChart.setVisibleXRangeMinimum(timeFrame.getStartTime().toEpochSecond());
                        mChart.setVisibleXRangeMaximum(timeFrame.getEndTime().toEpochSecond());
                        switch (timeFrame.getWindowSize()) {
                            case WEEK:
                                mTxtTimeFrame.setText(timeFrame.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE) + " - " + timeFrame.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
                                //mChart.setVisibleXRangeMaximum(14);
                                break;
                            case MONTH:
                                mTxtTimeFrame.setText(timeFrame.getStartTime().format(DateTimeFormatter.ofPattern("MMMM YYYY")));
                                //mChart.setVisibleXRangeMaximum(31);
                                break;
                            case YEAR:
                                mTxtTimeFrame.setText(timeFrame.getStartTime().format(DateTimeFormatter.ofPattern("YYYY")));
                                //mChart.setVisibleXRangeMaximum(365);
                                break;
                        }
                        mXAxis.setValueFormatter(new DateValueFormatter(timeFrame));
                        Timber.d("End update timeframe");
                        getFitnessData(timeFrame);
                    }
                });
    }

    private void getFitnessData(TimeFrame timeFrame) {
        final HistoryClient historyClient=Fitness.getHistoryClient(this.getActivity(),GoogleSignIn.getLastSignedInAccount(this.getContext()));
        DataReadRequest dataReadRequest=new DataReadRequest.Builder()
                .setTimeRange(timeFrame.getStartTime().toEpochSecond(),timeFrame.getEndTime().toEpochSecond(),TimeUnit.SECONDS)
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .build();
        historyClient.readData(dataReadRequest)
                .onSuccessTask(new DataReadResponseToLineData(timeFrame))
                .addOnSuccessListener(new OnSuccessListener<LineData>() {
                    @Override
                    public void onSuccess(LineData lineData) {
                        mChart.setData(lineData);
                        mChart.invalidate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e(e);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        this.mDisposableTimeFrame.dispose();
        if (this.mDisposableLoadData != null)
            this.mDisposableLoadData.dispose();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChevronLeft:
                this.mViewModel.moveWindowLeft();
                break;
            case R.id.btnChevronRight:
                this.mViewModel.moveWindowRight();
                break;
        }
    }
}

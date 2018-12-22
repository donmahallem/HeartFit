package com.github.donmahallem.sessionviewer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.sessionviewer.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WorkoutExerciseSummaryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTxtTitle;
    private TextView mTxtTitleCaption;
    private TextView mTxtCaloriesExpendedValue;
    private TextView mTxtHeartRateMaxValue;
    private TextView mTxtHeartRateAvgValue;
    private TextView mTxtHeartRateMinValue;
    private Group mGroupHeartRateData;
    private TextView mTxtStepCountValue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.weight_history_graph, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_exercise_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mTxtTitle = view.findViewById(R.id.txtTitle);
        this.mTxtTitleCaption = view.findViewById(R.id.txtTitleCaption);
        this.mTxtCaloriesExpendedValue = view.findViewById(R.id.txtCaloriesExpendedValue);
        this.mTxtHeartRateMaxValue = view.findViewById(R.id.txtHeartRateMaxValue);
        this.mTxtHeartRateMinValue = view.findViewById(R.id.txtHeartRateMinValue);
        this.mTxtHeartRateAvgValue = view.findViewById(R.id.txtHeartRateAvgValue);
        this.mGroupHeartRateData = view.findViewById(R.id.group_heart_rate);
        this.mTxtStepCountValue = view.findViewById(R.id.txtStepCountValue);
    }
}

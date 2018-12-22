package com.github.donmahallem.heartfit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.recycler.adapter.ResistanceTypesAdapter;
import com.github.donmahallem.heartfit.recycler.adapter.WorkoutExercisesAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SelectResistanceTypeBottomSheetDialogFragment extends BottomSheetDialogFragment {


    public final static String KEY_SELECTED_RESISTANCE_TYPE = "selected_resistance_type";
    private final Consumer<Integer> mSelectConsumer = new Consumer<Integer>() {
        @Override
        public void accept(Integer resistanceType) throws Exception {
            Fragment targetFragment = getTargetFragment(); // fragment1 in our case
            if (targetFragment != null) {
                final Intent intent = new Intent();
                intent.putExtra(KEY_SELECTED_RESISTANCE_TYPE, resistanceType);
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }
            SelectResistanceTypeBottomSheetDialogFragment
                    .this
                    .dismiss();
        }
    };
    private RecyclerView mRecyclerView;
    private ResistanceTypesAdapter mAdapter;
    private Disposable mSelectDisposable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_workout_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRecyclerView = view.findViewById(R.id.recyclerView);
        this.mAdapter = new ResistanceTypesAdapter();
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRecyclerView.setAdapter(this.mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mSelectDisposable = this.mAdapter.getOnResistanceTypeSelectedFlowable()
                .subscribe(this.mSelectConsumer);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mSelectDisposable.dispose();
    }
}

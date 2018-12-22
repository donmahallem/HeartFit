package com.github.donmahallem.heartfit.recycler.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.recycler.viewholder.WorkoutExerciseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created on 21.11.2018.
 */
public class WorkoutExerciseOfflineAdapter extends RecyclerView.Adapter<WorkoutExerciseViewHolder> {

    private final PublishSubject<WorkoutExercise> mWorkoutExerciseClickSubject = PublishSubject.create();
    private List<WorkoutExercise> mWorkoutExerciseList = new ArrayList<>();
    private SelectionTracker mSelectionTracker;

    public WorkoutExerciseOfflineAdapter() {
        super();
        this.setHasStableIds(true);
    }

    @Override
    public WorkoutExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final WorkoutExerciseViewHolder holder = new WorkoutExerciseViewHolder(parent);
        return holder;
    }

    @Override
    public long getItemId(int position) {
        return this.mWorkoutExerciseList.get(position).getId();
    }

    public Flowable<WorkoutExercise> getWorkoutExerciseClickFlowable() {
        return this.mWorkoutExerciseClickSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public void onBindViewHolder(WorkoutExerciseViewHolder holder, int position) {
        if (position < this.mWorkoutExerciseList.size()) {
            holder.setWorkoutExercise(this.mWorkoutExerciseList.get(position));
        } else {
            holder.setWorkoutExercise(null);
        }
        if(this.mSelectionTracker!=null){
            if(this.mSelectionTracker.isSelected(this.getItemId(position))){
                holder.itemView.setBackgroundResource(R.drawable.selectable_item_background_selected);
            }else{
                holder.itemView.setBackgroundResource(R.drawable.selectable_item_background);
            }
        }
    }

    public SelectionTracker getSelectionTracker() {
        return mSelectionTracker;
    }

    public void setWorkoutExercises(final List<WorkoutExercise> exercises) {
        final DiffUtil.Callback callback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mWorkoutExerciseList.size();
            }

            @Override
            public int getNewListSize() {
                return exercises.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return exercises.get(newItemPosition).getId() == mWorkoutExerciseList.get(oldItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }
        };
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);
        this.mWorkoutExerciseList.clear();
        this.mWorkoutExerciseList.addAll(exercises);
        result.dispatchUpdatesTo(this);

    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        return this.mWorkoutExerciseList.size();
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.mSelectionTracker=selectionTracker;
    }
}
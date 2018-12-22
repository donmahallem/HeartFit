package com.github.donmahallem.sessionviewer.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.sessionviewer.model.WorkoutExercise;
import com.github.donmahallem.sessionviewer.recycler.viewholder.WorkoutExerciseViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created on 29.11.2018.
 */
public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseViewHolder> {

    private final PublishSubject<WorkoutExercise> mWorkoutExerciseClickSubject=PublishSubject.create();

    public Flowable<WorkoutExercise> getOnWorkoutExerciseClickFlowable(){
        return this.mWorkoutExerciseClickSubject.toFlowable(BackpressureStrategy.LATEST);
    }
    @NonNull
    @Override
    public WorkoutExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final WorkoutExerciseViewHolder viewHolder= new WorkoutExerciseViewHolder(parent);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWorkoutExerciseClickSubject.onNext(viewHolder.getWorkoutExercise());
            }
        });
        return viewHolder;
    }
    private final List<WorkoutExercise> mItems=new ArrayList<>();

    @Override
    public void onBindViewHolder(@NonNull WorkoutExerciseViewHolder holder, int position) {
        holder.setWorkoutExercise(this.mItems.get(position));

    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    public void setWorkoutExercises(List<WorkoutExercise> workoutExercises) {
        this.mItems.clear();
        this.mItems.addAll(workoutExercises);
        this.notifyDataSetChanged();
    }
}

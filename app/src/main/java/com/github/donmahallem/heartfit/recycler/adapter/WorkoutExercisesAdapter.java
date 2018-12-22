package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.fit.FitResourceUtil;
import com.github.donmahallem.heartfit.recycler.viewholder.BottomSheetListItemViewHolder;
import com.google.android.gms.fitness.data.WorkoutExercises;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class WorkoutExercisesAdapter extends RecyclerView.Adapter<BottomSheetListItemViewHolder> {
    private final static List<String> sItems=new ArrayList<>();
    
    static{
        sItems.add(WorkoutExercises.PUSHUP);
        sItems.add(WorkoutExercises.CLOSE_GRIP_PUSHUP);
        sItems.add(WorkoutExercises.PIKE_PUSHUP);
        sItems.add(WorkoutExercises.BENCH_PRESS);
        sItems.add(WorkoutExercises.INCLINE_BENCH_PRESS);
        sItems.add(WorkoutExercises.DECLINE_BENCH_PRESS);
        sItems.add(WorkoutExercises.CLOSE_GRIP_BENCH_PRESS);
        sItems.add(WorkoutExercises.FLY);
        sItems.add(WorkoutExercises.PULLOVER);
        sItems.add(WorkoutExercises.DIP);
        sItems.add(WorkoutExercises.TRICEPS_DIP);
        sItems.add(WorkoutExercises.CHEST_DIP);
        sItems.add(WorkoutExercises.SHOULDER_PRESS);
        sItems.add(WorkoutExercises.PIKE_PRESS);
        sItems.add(WorkoutExercises.ARNOLD_PRESS);
        sItems.add(WorkoutExercises.MILITARY_PRESS);
        sItems.add(WorkoutExercises.LATERAL_RAISE);
        sItems.add(WorkoutExercises.FRONT_RAISE);
        sItems.add(WorkoutExercises.REAR_LATERAL_RAISE);
        sItems.add(WorkoutExercises.CLEAN);
        sItems.add(WorkoutExercises.CLEAN_JERK);
        sItems.add(WorkoutExercises.HANG_CLEAN);
        sItems.add(WorkoutExercises.POWER_CLEAN);
        sItems.add(WorkoutExercises.HANG_POWER_CLEAN);
        sItems.add(WorkoutExercises.ROW);
        sItems.add(WorkoutExercises.UPRIGHT_ROW);
        sItems.add(WorkoutExercises.HIGH_ROW);
        sItems.add(WorkoutExercises.PULLUP);
        sItems.add(WorkoutExercises.CHINUP);
        sItems.add(WorkoutExercises.PULLDOWN);
        sItems.add(WorkoutExercises.SHRUG);
        sItems.add(WorkoutExercises.BACK_EXTENSION);
        sItems.add(WorkoutExercises.GOOD_MORNING);
        sItems.add(WorkoutExercises.BICEP_CURL);
        sItems.add(WorkoutExercises.TRICEPS_EXTENSION);
        sItems.add(WorkoutExercises.JM_PRESS);
        sItems.add(WorkoutExercises.SQUAT);
        sItems.add(WorkoutExercises.LEG_PRESS);
        sItems.add(WorkoutExercises.LEG_CURL);
        sItems.add(WorkoutExercises.LEG_EXTENSION);
        sItems.add(WorkoutExercises.WALL_SIT);
        sItems.add(WorkoutExercises.STEP_UP);
        sItems.add(WorkoutExercises.DEADLIFT);
        sItems.add(WorkoutExercises.SINGLE_LEG_DEADLIFT);
        sItems.add(WorkoutExercises.STRAIGHT_LEG_DEADLIFT);
        sItems.add(WorkoutExercises.RDL_DEADLIFT);
        sItems.add(WorkoutExercises.LUNGE);
        sItems.add(WorkoutExercises.REAR_LUNGE);
        sItems.add(WorkoutExercises.SIDE_LUNGE);
        sItems.add(WorkoutExercises.SITUP);
        sItems.add(WorkoutExercises.CRUNCH);
        sItems.add(WorkoutExercises.LEG_RAISE);
        sItems.add(WorkoutExercises.HIP_RAISE);
        sItems.add(WorkoutExercises.V_UPS);
        sItems.add(WorkoutExercises.TWISTING_SITUP);
        sItems.add(WorkoutExercises.TWISTING_CRUNCH);
        sItems.add(WorkoutExercises.PLANK);
        sItems.add(WorkoutExercises.SIDE_PLANK);
        sItems.add(WorkoutExercises.HIP_THRUST);
        sItems.add(WorkoutExercises.SINGLE_LEG_HIP_BRIDGE);
        sItems.add(WorkoutExercises.HIP_EXTENSION);
        sItems.add(WorkoutExercises.RUSSIAN_TWIST);
        sItems.add(WorkoutExercises.SWING);
        sItems.add(WorkoutExercises.CALF_RAISE);
        sItems.add(WorkoutExercises.STANDING_CALF_RAISE);
        sItems.add(WorkoutExercises.SEATED_CALF_RAISE);
        sItems.add(WorkoutExercises.CALF_PRESS);
        sItems.add(WorkoutExercises.THRUSTER);
        sItems.add(WorkoutExercises.JUMPING_JACK);
        sItems.add(WorkoutExercises.BURPEE);
        sItems.add(WorkoutExercises.HIGH_KNEE_RUN);
    }

    private final PublishSubject<String> mWorkoutExerciseClickSubject=PublishSubject.create();

    public Flowable<String> getOnWorkoutSelectedFlowable(){
        return this.mWorkoutExerciseClickSubject.toFlowable(BackpressureStrategy.DROP);
    }

    @NonNull
    @Override
    public BottomSheetListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final BottomSheetListItemViewHolder viewHolder= new BottomSheetListItemViewHolder(parent);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetListItemViewHolder holder, int position) {
        holder.setText(FitResourceUtil.get(sItems.get(position)));
        holder.itemView.setOnClickListener(createClickListener(sItems.get(position)));
    }

    private View.OnClickListener createClickListener(final String workoutExercise) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkoutExercisesAdapter
                        .this
                        .mWorkoutExerciseClickSubject.onNext(workoutExercise);
            }
        };
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return sItems.size();
    }
}

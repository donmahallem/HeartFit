package com.github.donmahallem.heartfit.recycler.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.fit.FitResourceUtil;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 21.11.2018.
 */
public class WorkoutExerciseViewHolder extends LayoutViewHolder {
    private final TextView mTxtTitle;
    private final TextView mTxtDateTime;
    private final TextView mTxtDuration;
    private final ImageView mIvIcon;
    private WorkoutExercise mWorkoutExercise;

    public WorkoutExerciseViewHolder(ViewGroup parent) {
        super(parent, R.layout.vh_workout_exercise);
        this.mTxtTitle=this.itemView.findViewById(R.id.txtTitle);
        this.mTxtDateTime=this.itemView.findViewById(R.id.txtDateTime);
        this.mTxtDuration=this.itemView.findViewById(R.id.txtDuration);
        this.mIvIcon=this.itemView.findViewById(R.id.ivIcon);
    }

    public void setWorkoutExercise(@Nullable WorkoutExercise workoutExercise) {
        this.mWorkoutExercise=workoutExercise;
        this.mTxtTitle.setText(FitResourceUtil.get(workoutExercise.getExercise()));
        final long hours=ChronoUnit.HOURS.between(workoutExercise.getStartTime(), workoutExercise.getEndTime());
        final long minutes=ChronoUnit.MINUTES.between(workoutExercise.getStartTime(), workoutExercise.getEndTime())%60;
        final long seconds=ChronoUnit.SECONDS.between(workoutExercise.getStartTime(), workoutExercise.getEndTime())%60;
        final long millis=ChronoUnit.MILLIS.between(workoutExercise.getStartTime(), workoutExercise.getEndTime())%1000;
        if(hours>0){
            this.mTxtDuration.setText(String.format("%d:%02d:%02d",hours,minutes,seconds));
        }else{
            this.mTxtDuration.setText(String.format("%d:%02d.%02d",minutes,seconds,millis));
        }
        final LocalDateTime localDateTime=workoutExercise.getStartTime().toLocalDateTime();
        final boolean isToday=localDateTime.toLocalDate().equals(LocalDate.now());
        if(isToday){
            this.mTxtDateTime.setText(localDateTime.format(DateTimeFormatter.ISO_TIME));
        }else{
            this.mTxtDateTime.setText(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
        }
        this.mIvIcon.setImageResource(R.drawable.ic_fitness_center_black_24dp);
    }

    public WorkoutExercise getWorkoutExercise() {
        return this.mWorkoutExercise;
    }
}

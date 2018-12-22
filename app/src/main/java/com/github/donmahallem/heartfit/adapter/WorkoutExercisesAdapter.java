package com.github.donmahallem.heartfit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.donmahallem.heartfit.fit.FitResourceUtil;
import com.google.android.gms.fitness.data.WorkoutExercises;

import java.util.ArrayList;
import java.util.List;

public class WorkoutExercisesAdapter extends BaseAdapter {
    private final static List<String> mItems=new ArrayList<>();
    
    static{
        mItems.add(WorkoutExercises.PUSHUP);
        mItems.add(WorkoutExercises.CLOSE_GRIP_PUSHUP);
        mItems.add(WorkoutExercises.PIKE_PUSHUP);
        mItems.add(WorkoutExercises.BENCH_PRESS);
        mItems.add(WorkoutExercises.INCLINE_BENCH_PRESS);
        mItems.add(WorkoutExercises.DECLINE_BENCH_PRESS);
        mItems.add(WorkoutExercises.CLOSE_GRIP_BENCH_PRESS);
        mItems.add(WorkoutExercises.FLY);
        mItems.add(WorkoutExercises.PULLOVER);
        mItems.add(WorkoutExercises.DIP);
        mItems.add(WorkoutExercises.TRICEPS_DIP);
        mItems.add(WorkoutExercises.CHEST_DIP);
        mItems.add(WorkoutExercises.SHOULDER_PRESS);
        mItems.add(WorkoutExercises.PIKE_PRESS);
        mItems.add(WorkoutExercises.ARNOLD_PRESS);
        mItems.add(WorkoutExercises.MILITARY_PRESS);
        mItems.add(WorkoutExercises.LATERAL_RAISE);
        mItems.add(WorkoutExercises.FRONT_RAISE);
        mItems.add(WorkoutExercises.REAR_LATERAL_RAISE);
        mItems.add(WorkoutExercises.CLEAN);
        mItems.add(WorkoutExercises.CLEAN_JERK);
        mItems.add(WorkoutExercises.HANG_CLEAN);
        mItems.add(WorkoutExercises.POWER_CLEAN);
        mItems.add(WorkoutExercises.HANG_POWER_CLEAN);
        mItems.add(WorkoutExercises.ROW);
        mItems.add(WorkoutExercises.UPRIGHT_ROW);
        mItems.add(WorkoutExercises.HIGH_ROW);
        mItems.add(WorkoutExercises.PULLUP);
        mItems.add(WorkoutExercises.CHINUP);
        mItems.add(WorkoutExercises.PULLDOWN);
        mItems.add(WorkoutExercises.SHRUG);
        mItems.add(WorkoutExercises.BACK_EXTENSION);
        mItems.add(WorkoutExercises.GOOD_MORNING);
        mItems.add(WorkoutExercises.BICEP_CURL);
        mItems.add(WorkoutExercises.TRICEPS_EXTENSION);
        mItems.add(WorkoutExercises.JM_PRESS);
        mItems.add(WorkoutExercises.SQUAT);
        mItems.add(WorkoutExercises.LEG_PRESS);
        mItems.add(WorkoutExercises.LEG_CURL);
        mItems.add(WorkoutExercises.LEG_EXTENSION);
        mItems.add(WorkoutExercises.WALL_SIT);
        mItems.add(WorkoutExercises.STEP_UP);
        mItems.add(WorkoutExercises.DEADLIFT);
        mItems.add(WorkoutExercises.SINGLE_LEG_DEADLIFT);
        mItems.add(WorkoutExercises.STRAIGHT_LEG_DEADLIFT);
        mItems.add(WorkoutExercises.RDL_DEADLIFT);
        mItems.add(WorkoutExercises.LUNGE);
        mItems.add(WorkoutExercises.REAR_LUNGE);
        mItems.add(WorkoutExercises.SIDE_LUNGE);
        mItems.add(WorkoutExercises.SITUP);
        mItems.add(WorkoutExercises.CRUNCH);
        mItems.add(WorkoutExercises.LEG_RAISE);
        mItems.add(WorkoutExercises.HIP_RAISE);
        mItems.add(WorkoutExercises.V_UPS);
        mItems.add(WorkoutExercises.TWISTING_SITUP);
        mItems.add(WorkoutExercises.TWISTING_CRUNCH);
        mItems.add(WorkoutExercises.PLANK);
        mItems.add(WorkoutExercises.SIDE_PLANK);
        mItems.add(WorkoutExercises.HIP_THRUST);
        mItems.add(WorkoutExercises.SINGLE_LEG_HIP_BRIDGE);
        mItems.add(WorkoutExercises.HIP_EXTENSION);
        mItems.add(WorkoutExercises.RUSSIAN_TWIST);
        mItems.add(WorkoutExercises.SWING);
        mItems.add(WorkoutExercises.CALF_RAISE);
        mItems.add(WorkoutExercises.STANDING_CALF_RAISE);
        mItems.add(WorkoutExercises.SEATED_CALF_RAISE);
        mItems.add(WorkoutExercises.CALF_PRESS);
        mItems.add(WorkoutExercises.THRUSTER);
        mItems.add(WorkoutExercises.JUMPING_JACK);
        mItems.add(WorkoutExercises.BURPEE);
        mItems.add(WorkoutExercises.HIGH_KNEE_RUN);
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView= (TextView) convertView;
        textView.setText(FitResourceUtil.get(mItems.get(position)));
        return convertView;
    }
}

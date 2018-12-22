package com.github.donmahallem.heartfit.fit;

import com.github.donmahallem.heartfit.R;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.WorkoutExercises;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import java.lang.annotation.Retention;

import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.StringRes;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FitResourceUtil {
    public static long localDateTimeToMillis(LocalDateTime time) {
        return (time.toEpochSecond(OffsetDateTime.now().getOffset())*1000)+(time.getNano()/1000000L);
    }

    @Retention(SOURCE)
    @IntDef({Field.RESISTANCE_TYPE_UNKNOWN,
            Field.RESISTANCE_TYPE_BARBELL,
            Field.RESISTANCE_TYPE_CABLE,
    Field.RESISTANCE_TYPE_DUMBBELL,
    Field.RESISTANCE_TYPE_KETTLEBELL,
    Field.RESISTANCE_TYPE_MACHINE,
    Field.RESISTANCE_TYPE_BODY})
    public @interface ResistanceType {}
    @StringRes
    public static int get(@ResistanceType int resistanceType){
        switch (resistanceType){
            case 1:
                return R.string.resistance_type_barbell;
            case 2:
                return R.string.resistance_type_cable;
            case 3:
                return R.string.resistance_type_dumbbell;
            case 4:
                return R.string.resistance_type_kettlebell;
            case 5:
                return R.string.resistance_type_machine;
            case 6:
                return R.string.resistance_type_body;
            default:
                return R.string.resistance_type_unknown;
        }
    }

    @StringRes
    public static int get(final String workoutExercises){
        switch (workoutExercises){
            case WorkoutExercises.PUSHUP:
                return R.string.workout_exercises_pushup;
            case WorkoutExercises.CLOSE_GRIP_PUSHUP:
                return R.string.workout_exercises_close_grip_pushup;
            case WorkoutExercises.PIKE_PUSHUP:
                return R.string.workout_exercises_pike_pushup;
            case WorkoutExercises.BENCH_PRESS:
                return R.string.workout_exercises_bench_press;
            case WorkoutExercises.INCLINE_BENCH_PRESS:
                return R.string.workout_exercises_incline_bench_press;
            case WorkoutExercises.DECLINE_BENCH_PRESS:
                return R.string.workout_exercises_decline_bench_press;
            case WorkoutExercises.CLOSE_GRIP_BENCH_PRESS:
                return R.string.workout_exercises_close_grip_bench_press;
            case WorkoutExercises.FLY:
                return R.string.workout_exercises_fly;
            case "fly.reverse":
                return R.string.workout_exercises_fly_reverse;
            case WorkoutExercises.PULLOVER:
                return R.string.workout_exercises_pullover;
            case WorkoutExercises.DIP:
                return R.string.workout_exercises_dip;
            case WorkoutExercises.TRICEPS_DIP:
                return R.string.workout_exercises_triceps_dip;
            case WorkoutExercises.CHEST_DIP:
                return R.string.workout_exercises_chest_dip;
            case WorkoutExercises.SHOULDER_PRESS:
                return R.string.workout_exercises_shoulder_press;
            case WorkoutExercises.PIKE_PRESS:
                return R.string.workout_exercises_pike_press;
            case WorkoutExercises.ARNOLD_PRESS:
                return R.string.workout_exercises_arnold_press;
            case WorkoutExercises.MILITARY_PRESS:
                return R.string.workout_exercises_military_press;
            case WorkoutExercises.LATERAL_RAISE:
                return R.string.workout_exercises_lateral_raise;
            case WorkoutExercises.FRONT_RAISE:
                return R.string.workout_exercises_front_raise;
            case WorkoutExercises.REAR_LATERAL_RAISE:
                return R.string.workout_exercises_rear_lateral_raise;
            case WorkoutExercises.CLEAN:
                return R.string.workout_exercises_clean;
            case WorkoutExercises.CLEAN_JERK:
                return R.string.workout_exercises_clean_jerk;
            case WorkoutExercises.HANG_CLEAN:
                return R.string.workout_exercises_hang_clean;
            case WorkoutExercises.POWER_CLEAN:
                return R.string.workout_exercises_power_clean;
            case WorkoutExercises.HANG_POWER_CLEAN:
                return R.string.workout_exercises_hang_power_clean;
            case WorkoutExercises.ROW:
                return R.string.workout_exercises_row;
            case WorkoutExercises.UPRIGHT_ROW:
                return R.string.workout_exercises_upright_row;
            case WorkoutExercises.HIGH_ROW:
                return R.string.workout_exercises_high_row;
            case WorkoutExercises.PULLUP:
                return R.string.workout_exercises_pullup;
            case WorkoutExercises.CHINUP:
                return R.string.workout_exercises_chinup;
            case WorkoutExercises.PULLDOWN:
                return R.string.workout_exercises_pulldown;
            case WorkoutExercises.SHRUG:
                return R.string.workout_exercises_shrug;
            case WorkoutExercises.BACK_EXTENSION:
                return R.string.workout_exercises_back_extension;
            case WorkoutExercises.GOOD_MORNING:
                return R.string.workout_exercises_good_morning;
            case WorkoutExercises.BICEP_CURL:
                return R.string.workout_exercises_bicep_curl;
            case WorkoutExercises.TRICEPS_EXTENSION:
                return R.string.workout_exercises_triceps_extension;
            case WorkoutExercises.JM_PRESS:
                return R.string.workout_exercises_jm_press;
            case WorkoutExercises.SQUAT:
                return R.string.workout_exercises_squat;
            case WorkoutExercises.LEG_PRESS:
                return R.string.workout_exercises_leg_press;
            case WorkoutExercises.LEG_CURL:
                return R.string.workout_exercises_leg_curl;
            case WorkoutExercises.LEG_EXTENSION:
                return R.string.workout_exercises_leg_extension;
            case WorkoutExercises.WALL_SIT:
                return R.string.workout_exercises_wall_sit;
            case WorkoutExercises.STEP_UP:
                return R.string.workout_exercises_step_up;
            case WorkoutExercises.DEADLIFT:
                return R.string.workout_exercises_deadlift;
            case WorkoutExercises.SINGLE_LEG_DEADLIFT:
                return R.string.workout_exercises_single_leg_deadlift;
            case WorkoutExercises.STRAIGHT_LEG_DEADLIFT:
                return R.string.workout_exercises_straight_leg_deadlift;
            case WorkoutExercises.RDL_DEADLIFT:
                return R.string.workout_exercises_rdl_deadlift;
            case WorkoutExercises.LUNGE:
                return R.string.workout_exercises_lunge;
            case WorkoutExercises.REAR_LUNGE:
                return R.string.workout_exercises_rear_lunge;
            case WorkoutExercises.SIDE_LUNGE:
                return R.string.workout_exercises_side_lunge;
            case WorkoutExercises.SITUP:
                return R.string.workout_exercises_situp;
            case WorkoutExercises.CRUNCH:
                return R.string.workout_exercises_crunch;
            case WorkoutExercises.LEG_RAISE:
                return R.string.workout_exercises_leg_raise;
            case WorkoutExercises.HIP_RAISE:
                return R.string.workout_exercises_hip_raise;
            case WorkoutExercises.V_UPS:
                return R.string.workout_exercises_v_ups;
            case WorkoutExercises.TWISTING_SITUP:
                return R.string.workout_exercises_twisting_situp;
            case WorkoutExercises.TWISTING_CRUNCH:
                return R.string.workout_exercises_twisting_crunch;
            case WorkoutExercises.PLANK:
                return R.string.workout_exercises_plank;
            case WorkoutExercises.SIDE_PLANK:
                return R.string.workout_exercises_side_plank;
            case WorkoutExercises.HIP_THRUST:
                return R.string.workout_exercises_hip_thrust;
            case WorkoutExercises.SINGLE_LEG_HIP_BRIDGE:
                return R.string.workout_exercises_single_leg_hip_bridge;
            case WorkoutExercises.HIP_EXTENSION:
                return R.string.workout_exercises_hip_extension;
            case WorkoutExercises.RUSSIAN_TWIST:
                return R.string.workout_exercises_russian_twist;
            case WorkoutExercises.SWING:
                return R.string.workout_exercises_swing;
            case WorkoutExercises.CALF_RAISE:
                return R.string.workout_exercises_calf_raise;
            case WorkoutExercises.STANDING_CALF_RAISE:
                return R.string.workout_exercises_standing_calf_raise;
            case WorkoutExercises.SEATED_CALF_RAISE:
                return R.string.workout_exercises_seated_calf_raise;
            case WorkoutExercises.CALF_PRESS:
                return R.string.workout_exercises_calf_press;
            case WorkoutExercises.THRUSTER:
                return R.string.workout_exercises_thruster;
            case WorkoutExercises.JUMPING_JACK:
                return R.string.workout_exercises_jumping_jack;
            case WorkoutExercises.BURPEE:
                return R.string.workout_exercises_burpee;
            case WorkoutExercises.HIGH_KNEE_RUN:
                return R.string.workout_exercises_high_knee_run;
                default:
                return R.string.workout_exercises_unknown;
        }
    }
}

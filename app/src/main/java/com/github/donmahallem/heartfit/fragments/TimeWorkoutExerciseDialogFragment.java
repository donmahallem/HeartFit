package com.github.donmahallem.heartfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.OffsetDateTimeConverter;
import com.github.donmahallem.heartfit.fit.FitResourceUtil;
import com.github.donmahallem.heartfit.viewmodel.InsertActivityViewModel;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TimeWorkoutExerciseDialogFragment extends DialogFragment implements View.OnClickListener {
    private final static String KEY_WORKOUT_TYPE = "key_workout_type";
    private static final String KEY_START_TIME = "key_start_time";
    private static final String KEY_TIMER_IS_RUNNING = "key_timer_is_running";
    private TextView mTxtTitle;
    private Button mBtnCancel;
    private Button mBtnToggleTimer;
    private Disposable mTimerDisposable;
    private boolean mTimerIsRunning = false;
    private TextView mTxtTimerOutput;
    private final Consumer<? super Long> mNextConsumer = new Consumer<Long>() {
        @Override
        public void accept(Long aLong) throws Exception {
            final long millis=aLong%1000;
            final long seconds=(aLong/1000)%60;
            if(aLong>=60000){
                final long minutes=aLong/60000;
                mTxtTimerOutput.setText(String.format("%02d:%02d:%02d",minutes,seconds,millis));
            }else{
                mTxtTimerOutput.setText(String.format("%02d:%02d",seconds,millis));
            }
        }
    };
    private OffsetDateTime mTimestampStart;
    private WeakReference<OnDurationSelectedListener> mOnDurationSelectedListener;
    private InsertActivityViewModel mViewModel;
    private Disposable mWorkoutExerciseDisposable;

    public static TimeWorkoutExerciseDialogFragment newInstance(String workoutType) {
        TimeWorkoutExerciseDialogFragment f = new TimeWorkoutExerciseDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(KEY_WORKOUT_TYPE, workoutType);
        f.setArguments(args);
        return f;
    }

    public void setOnDurationSelectedListener(OnDurationSelectedListener onDurationSelectedListener) {
        mOnDurationSelectedListener = new WeakReference<>(onDurationSelectedListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        if (savedInstanceState != null) {
            this.mTimerIsRunning = savedInstanceState.getBoolean(KEY_TIMER_IS_RUNNING, false);
            if(savedInstanceState.containsKey(KEY_START_TIME)){
                this.mTimestampStart = OffsetDateTimeConverter.toDate(savedInstanceState.getString(KEY_START_TIME));
            }else{
                this.mTimestampStart=OffsetDateTime.now();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_exercise, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity()!=null) {
            this.mViewModel = ViewModelProviders.of(getActivity()).get(InsertActivityViewModel.class);
        }
        this.mTxtTitle = view.findViewById(R.id.txtTitle);
        this.mTxtTimerOutput = view.findViewById(R.id.txtTimer);
        this.mBtnCancel = view.findViewById(R.id.btnCancel);
        this.mBtnToggleTimer = view.findViewById(R.id.btnToggleTimer);
        this.mBtnCancel.setOnClickListener(this);
        this.mBtnToggleTimer.setOnClickListener(this);
        this.updateLayoutStates();
        if(this.mViewModel.getWorkoutExercise()!=null)
            this.mTxtTitle.setText(FitResourceUtil.get(this.mViewModel.getWorkoutExercise()));
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putBoolean(KEY_TIMER_IS_RUNNING, this.mTimerIsRunning);
        if (this.mTimestampStart != null) {
            savedState.putString(KEY_START_TIME, OffsetDateTimeConverter.toLong(this.mTimestampStart));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnToggleTimer:
                this.toggleTimer();
                break;
            case R.id.btnCancel:
                this.stopTimerObservable();
                this.dismiss();
                break;
        }
    }

    private void toggleTimer() {
        if (this.mTimerIsRunning) {
            this.stopTimer();
        } else {
            this.mTimestampStart = OffsetDateTime.now();
            this.startOrResumeTimer();
        }
        this.mTimerIsRunning = !this.mTimerIsRunning;
        this.updateLayoutStates();
    }

    public void stopTimerObservable() {
        if (this.mTimerDisposable != null)
            this.mTimerDisposable.dispose();
    }

    private void stopTimer() {
        this.stopTimerObservable();
        if (this.mOnDurationSelectedListener != null && this.mOnDurationSelectedListener.get() != null) {
            Timber.d("Stoped timer send");
            //this.mOnDurationSelectedListener.get().onDurationSelected(this.mTimestampStart, OffsetDateTime.now());
        }
        this.mViewModel.setStartTime(this.mTimestampStart);
        this.mViewModel.setEndTime(OffsetDateTime.now());
        this.dismiss();
    }

    public Flowable<Long> createTimerFlowable(final OffsetDateTime startTime) {
        return Flowable.interval(0, 100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return ChronoUnit.MILLIS.between(startTime, OffsetDateTime.now());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void startOrResumeTimer() {
        this.mTimerDisposable = createTimerFlowable(this.mTimestampStart)
                .subscribe(this.mNextConsumer);
    }

    public void updateLayoutStates() {
        this.mBtnToggleTimer.setCompoundDrawablesRelativeWithIntrinsicBounds(!this.mTimerIsRunning ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_stop_black_24dp, 0, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateLayoutStates();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.stopTimerObservable();
        if(this.mWorkoutExerciseDisposable!=null)
            this.mWorkoutExerciseDisposable.dispose();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.eventuallyStartTimerObservable();
        this.mWorkoutExerciseDisposable = this.mViewModel.getWorkoutExerciseFlowable()
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String workoutExercise) throws Exception {
                        final TextView txtTitle = TimeWorkoutExerciseDialogFragment.this.mTxtTitle;
                        if (txtTitle != null)
                            txtTitle.setText(FitResourceUtil.get(workoutExercise));
                    }
                });
    }

    private void eventuallyStartTimerObservable() {
        if (this.mTimerIsRunning) {
            this.mTimerDisposable = createTimerFlowable(this.mTimestampStart)
                    .subscribe(this.mNextConsumer);
        }
    }

    public interface OnDurationSelectedListener {
        void onDurationSelected(OffsetDateTime startTime, OffsetDateTime endTime);
    }
}
package com.github.donmahallem.heartfit.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.adapter.WeightUnitAdapter;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.fit.FitResourceUtil;
import com.github.donmahallem.heartfit.viewmodel.InsertActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class InsertWorkoutExerciseFragment extends DialogFragment implements View.OnClickListener {

    private static final String KEY_EDIT_WORKOUT = "key_edit_workout";
    private static final String KEY_WORKOUT_ID = "key_workout_id";
    private static final int REQUEST_CODE_SELECT_WORKOUT_EXERCISE = 823;
    private static final int REQUEST_CODE_SELECT_RESISTANCE_TYPE = 824;
    private Button mBtnStartTime, mBtnEndTime;
    private Button mBtnStartDate, mBtnEndDate;
    private Spinner mSpinnerWeightUnit;
    private EditText mInputDuration;
    private Button mBtnTimeExercise;
    private TextInputEditText mInputRepititions;
    private TextInputEditText mInputResistance;
    private TextInputLayout mInputLayoutRepititions;
    private TextInputLayout mInputLayoutResistance;
    private Switch mSwitchTimeWorkout;
    private List<Disposable> mDisposableList = new ArrayList<>();
    private InsertActivityViewModel mViewModel;
    private final CompoundButton.OnCheckedChangeListener mOnCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            InsertWorkoutExerciseFragment.this
                    .mViewModel
                    .setTimerMode(b);
        }
    };
    private Group mGroupTimer, mGroupDuration;
    private boolean mEditMode;
    private long mEditId;
    private Button mBtnSelectWorkoutExercise;
    private final Consumer<String> mWorkoutExerciseConsumer = new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
            mBtnSelectWorkoutExercise.setText(FitResourceUtil.get(s));
        }
    };
    private Button mBtnSelectResistanceType;
    private final Consumer<Integer> mResistanceTypeConsumer=new Consumer<Integer>() {
        @Override
        public void accept(Integer integer) throws Exception {
            mBtnSelectResistanceType.setText(FitResourceUtil.get(integer));
        }
    };
    private final Consumer<Float> mResistanceConsumer=new Consumer<Float>() {
        @Override
        public void accept(Float aFloat) throws Exception {
            final String newText=Float.toString(aFloat);
            if(mInputResistance.getText().toString().equals(newText))
                return;
            mInputResistance.setText(newText);
        }
    };
    private final Consumer<Integer> mRepetitionsConsumer=new Consumer<Integer>() {
        @Override
        public void accept(Integer integer) throws Exception {
            final String newText=Integer.toString(integer);
            if(mInputRepititions.getText().toString().equals(newText))
                return;
            mInputRepititions.setText(newText);
        }
    };

    public static InsertWorkoutExerciseFragment createEditInstance(@NonNull WorkoutExercise workoutExercise) {
        return createEditInstance(workoutExercise.getId());
    }

    public static InsertWorkoutExerciseFragment createEditInstance(@NonNull long editId) {
        InsertWorkoutExerciseFragment insertWorkoutExerciseFragment = new InsertWorkoutExerciseFragment();
        final Bundle args = new Bundle();
        args.putLong(KEY_WORKOUT_ID, editId);
        args.putBoolean(KEY_EDIT_WORKOUT, true);
        insertWorkoutExerciseFragment.setArguments(args);
        return insertWorkoutExerciseFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        final Bundle args = this.getArguments();
        if (args != null) {
            this.mEditMode = args.getBoolean(KEY_EDIT_WORKOUT, false);
            this.mEditId = args.getLong(KEY_WORKOUT_ID, -1);
            CacheDatabase.getDatabase(this.getContext())
                    .workoutDao()
                    .getWorkout(this.mEditId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<WorkoutExercise>() {
                        @Override
                        public void accept(WorkoutExercise workoutExercise) throws Exception {
                            mViewModel.setWorkoutExercise(workoutExercise);
                        }
                    });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insert_workout_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mViewModel = ViewModelProviders.of(getActivity()).get(InsertActivityViewModel.class);
        this.mInputResistance = view.findViewById(R.id.editResistance);
        this.mInputRepititions = view.findViewById(R.id.editRepititions);
        this.mInputLayoutResistance = view.findViewById(R.id.textInputLayout);
        this.mInputLayoutRepititions = view.findViewById(R.id.textInputLayout2);
        this.mBtnSelectResistanceType = view.findViewById(R.id.btnSelectResistanceType);
        this.mBtnTimeExercise = view.findViewById(R.id.btnTimeExercise);
        this.mInputDuration = view.findViewById(R.id.editDuration);
        this.mBtnSelectWorkoutExercise = view.findViewById(R.id.btnSelectWorkoutExercise);
        this.mBtnSelectWorkoutExercise.setOnClickListener(this);
        this.mSwitchTimeWorkout = view.findViewById(R.id.switchTimeWorkout);
        this.mSwitchTimeWorkout.setOnCheckedChangeListener(this.mOnCheckChangeListener);
        // Specify the layout to use when the list of choices appears
        // Apply the adapter to the spinner
        this.mBtnSelectResistanceType.setOnClickListener(this);
        this.mSpinnerWeightUnit = view.findViewById(R.id.spinnerWeightUnit);
        this.mSpinnerWeightUnit.setAdapter(new WeightUnitAdapter());
        this.mBtnStartDate = view.findViewById(R.id.btnSelectStartDate);
        this.mBtnStartTime = view.findViewById(R.id.btnSelectStartTime);
        this.mBtnEndDate = view.findViewById(R.id.btnSelectEndDate);
        this.mBtnEndTime = view.findViewById(R.id.btnSelectEndTime);
        this.mGroupDuration = view.findViewById(R.id.groupDuration);
        this.mGroupTimer = view.findViewById(R.id.groupTimer);
        this.mBtnStartDate.setOnClickListener(this);
        this.mBtnStartTime.setOnClickListener(this);
        this.mBtnEndDate.setOnClickListener(this);
        this.mBtnEndTime.setOnClickListener(this);
        this.mBtnTimeExercise.setOnClickListener(this);
        this.mViewModel.onRestoreInstanceState(savedInstanceState);
        this.mInputRepititions.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Timber.d("afterTextChanged %s", s.toString());
                if (s.length() == 0) {
                    mViewModel.setRepititions(0);
                    return;
                }
                mViewModel.setRepititions(Integer.parseInt(s.toString()));
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });
        this.mInputResistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Timber.d("afterTextChanged %s", editable.toString());
                if (editable.length() == 0) {
                    mViewModel.setResistance(0);
                    return;
                }
                mViewModel.setResistance(Float.parseFloat(editable.toString()));
            }
        });
        //////////////////////////////
        // INIT Recyclers
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSelectEndTime:
                this.selectTime(false);
                break;
            case R.id.btnSelectEndDate:
                this.selectDate(false);
                break;
            case R.id.btnSelectStartDate:
                this.selectDate(true);
                break;
            case R.id.btnSelectStartTime:
                this.selectTime(true);
                break;
            case R.id.btnTimeExercise:
                this.timeExercise();
                break;
            case R.id.btnSelectWorkoutExercise:
                this.selectWorkoutExercise();
                break;
            case R.id.btnSelectResistanceType:
                this.selectResistanceType();
                break;
        }
    }

    private void selectResistanceType() {
        SelectResistanceTypeBottomSheetDialogFragment selectWorkoutExerciseBottomSheetDialogFragment = new SelectResistanceTypeBottomSheetDialogFragment();
        selectWorkoutExerciseBottomSheetDialogFragment.setTargetFragment(this, REQUEST_CODE_SELECT_RESISTANCE_TYPE);
        selectWorkoutExerciseBottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "select_resistance_type");
    }

    private void selectWorkoutExercise() {
        SelectWorkoutExerciseBottomSheetDialogFragment selectWorkoutExerciseBottomSheetDialogFragment = new SelectWorkoutExerciseBottomSheetDialogFragment();
        selectWorkoutExerciseBottomSheetDialogFragment.setTargetFragment(this, REQUEST_CODE_SELECT_WORKOUT_EXERCISE);
        selectWorkoutExerciseBottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "select_workout_exercise");
    }

    private void timeExercise() {
        TimeWorkoutExerciseDialogFragment fragment = TimeWorkoutExerciseDialogFragment.newInstance(this.mViewModel.getWorkoutExercise());
        fragment.setOnDurationSelectedListener(new TimeWorkoutExerciseDialogFragment.OnDurationSelectedListener() {
            @Override
            public void onDurationSelected(OffsetDateTime startTime, OffsetDateTime endTime) {
                mViewModel.setStartTime(startTime);
                mViewModel.setEndTime(endTime);
            }
        });
        fragment.show(this.getChildFragmentManager(), "timer_fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_WORKOUT_EXERCISE) {
            if (resultCode == Activity.RESULT_OK) {
                this.mViewModel.setWorkoutExercise(data.getStringExtra(SelectWorkoutExerciseBottomSheetDialogFragment.KEY_SELECTED_WORKOUT_EXERCISE));
                return;
            }
        } else if (requestCode == REQUEST_CODE_SELECT_RESISTANCE_TYPE) {
            if (resultCode == Activity.RESULT_OK) {
                this.mViewModel.setResistanceType(data.getIntExtra(SelectResistanceTypeBottomSheetDialogFragment.KEY_SELECTED_RESISTANCE_TYPE, 0));
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void selectDate(boolean start) {
        DatePickerDialogFragment datePickerDialogFragment;
        if (start) {
            final OffsetDateTime localDateTime = this.mViewModel.getStartTime();
            datePickerDialogFragment = DatePickerDialogFragment.newInstance(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth());
            datePickerDialogFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mViewModel.setStartDate(year, month + 1, day);
                }
            });
        } else {
            final OffsetDateTime localDateTime = this.mViewModel.getEndTime();
            datePickerDialogFragment = DatePickerDialogFragment.newInstance(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth());
            datePickerDialogFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mViewModel.setEndDate(year, month + 1, day);
                }
            });
        }
        datePickerDialogFragment.show(getChildFragmentManager(), "datePicker");
    }

    private void selectTime(boolean start) {
        TimePickerDialogFragment newFragment;
        if (start) {
            final OffsetDateTime startTime = this.mViewModel.getStartTime();
            newFragment = TimePickerDialogFragment.newInstance(startTime.getHour(), startTime.getMinute());
            newFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    mViewModel.setStartTime(hour, minute);
                }
            });
        } else {
            final OffsetDateTime endTime = this.mViewModel.getEndTime();
            newFragment = TimePickerDialogFragment.newInstance(endTime.getHour(), endTime.getMinute());
            newFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    mViewModel.setEndTime(hour, minute);
                }
            });
        }
        newFragment.show(this.getChildFragmentManager(), "timePicker");
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mDisposableList.add(this.mViewModel.getStartTimeFlowable()
                .subscribe(new SelectTimeObserver(this.mBtnStartDate, this.mBtnStartTime)));
        this.mDisposableList.add(this.mViewModel.getEndTimeFlowable()
                .subscribe(new SelectTimeObserver(this.mBtnEndDate, this.mBtnEndTime)));
        this.mDisposableList.add(this.mViewModel.getUseTimerFlowable()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean useTimer) throws Exception {
                        mGroupDuration.setVisibility(useTimer ? View.GONE : View.VISIBLE);
                        mGroupTimer.setVisibility(useTimer ? View.VISIBLE : View.GONE);
                    }
                }));
        this.mDisposableList.add(this.mViewModel.getWorkoutExerciseFlowable()
                .subscribe(this.mWorkoutExerciseConsumer));
        this.mDisposableList.add(this.mViewModel.getResistanceTypeFlowable()
                .subscribe(this.mResistanceTypeConsumer));
        this.mDisposableList.add(this.mViewModel.getResistanceFlowable()
            .subscribe(this.mResistanceConsumer));
        this.mDisposableList.add(this.mViewModel.getRepetitionsFlowable()
            .subscribe(this.mRepetitionsConsumer));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_insert_workout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_save:
                saveWorkoutExercise();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void saveWorkoutExercise() {
        Completable single=null;
        if(this.isEditModeEnabled()){
        single=this.mViewModel.update(CacheDatabase.getDatabase(this.getContext()),getEditId()).ignoreElement();
        }else{
            single=this.mViewModel.submit(CacheDatabase.getDatabase(this.getContext())).ignoreElement();
        }
        final Disposable a=single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Snackbar.make(getView(), R.string.submitted, Snackbar.LENGTH_SHORT).show();
                        if(getShowsDialog())
                            dismiss();
                        else
                            getActivity().onBackPressed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        Snackbar.make(getView(), R.string.not_submitted, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private long getEditId() {
        if(this.getArguments()==null)
            return -1;
        return this.getArguments().getLong(KEY_WORKOUT_ID,-1);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (Disposable disposable : this.mDisposableList) {
            disposable.dispose();
        }
        this.mDisposableList.clear();
    }

    public boolean isEditModeEnabled() {
        if(this.getArguments()==null)
            return false;
        return this.getArguments().getBoolean(KEY_EDIT_WORKOUT,false);
    }

    private final class SelectTimeObserver implements Consumer<OffsetDateTime> {

        private final Button mTimeButton;
        private final Button mDateButton;

        public SelectTimeObserver(Button dateButton, Button timeButton) {
            this.mDateButton = dateButton;
            this.mTimeButton = timeButton;
        }

        @Override
        public void accept(OffsetDateTime localDateTime) {
            //Timber.d("Data picked");
            this.mTimeButton.setText(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            this.mDateButton.setText(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));

        }
    }
}

package com.github.donmahallem.heartfit.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.OffsetDateTimeConverter;
import com.github.donmahallem.heartfit.textwatcher.ValidFloatTextWatcher;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import timber.log.Timber;

public abstract class InsertValueDialogFragment extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String KEY_SELECTED_TIME = "key_selected_time";
    private HistoryClient mHistoryClient;
    private EditText mInputWeight;
    private OffsetDateTime mLocalDateTime;
    private Button mBtnSelectTime;
    private Button mBtnSelectDate;
    private TextInputLayout mInputLayoutWeight;
    private Toolbar mToolbar;

    public OffsetDateTime getTimestamp(){
        return this.mLocalDateTime;
    }

    public float getValue(){
        return Float.parseFloat(this.mInputWeight.getText().toString());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mInputLayoutWeight = view.findViewById(R.id.textInputLayoutWeight);
        this.mInputWeight = view.findViewById(R.id.editWeight);
        this.mBtnSelectDate = view.findViewById(R.id.btnSelectDate);
        this.mBtnSelectTime = view.findViewById(R.id.btnSelectTime);
        this.mBtnSelectDate.setOnClickListener(this);
        this.mBtnSelectTime.setOnClickListener(this);
        this.mLocalDateTime = OffsetDateTime.now();
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SELECTED_TIME)) {
            this.mLocalDateTime = OffsetDateTimeConverter.toDate(savedInstanceState.getString(KEY_SELECTED_TIME));
        }
        this.mInputWeight.addTextChangedListener(new ValidFloatTextWatcher(this.mInputWeight, this.mInputLayoutWeight));
        this.mToolbar = view.findViewById(R.id.toolbar);
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        this.mToolbar.inflateMenu(R.menu.fragment_dialog_insert_value);
        this.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_save:
                        storeDataInt();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    protected void storeDataInt(){
        this.setControlsEnabled(false);
        this.storeData().addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    InsertValueDialogFragment.this.dismiss();
                    Timber.d("Data Saved");
                }else{
                    InsertValueDialogFragment.this.setControlsEnabled(true);
                    Snackbar.make(getView(),R.string.error_occured,Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setControlsEnabled(boolean enabled){
        this.mBtnSelectDate.setEnabled(enabled);
        this.mBtnSelectTime.setEnabled(enabled);
        this.mInputLayoutWeight.setEnabled(enabled);
        this.getDialog().setCanceledOnTouchOutside(enabled);
        this.getDialog().setCancelable(enabled);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateShownTime();
    }

    private void updateShownTime() {
        this.mBtnSelectTime.setText(this.mLocalDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        this.mBtnSelectDate.setText(this.mLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        savedInstance.putString(KEY_SELECTED_TIME, OffsetDateTimeConverter.toLong(this.mLocalDateTime));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSelectDate:
                this.selectDate();
                break;
            case R.id.btnSelectTime:
                this.selectTime();
                break;
        }
    }

    public void selectTime() {
        TimePickerDialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.setOnTimeSetListener(this);
        newFragment.show(getChildFragmentManager(), "timePicker");
    }

    public void selectDate() {
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setOnDateSetListener(this);
        newFragment.show(getChildFragmentManager(), "datePicker");
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
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.mLocalDateTime = this.mLocalDateTime
                .withDayOfYear(day)
                .withYear(year)
                .withMonth(month + 1);
        this.updateShownTime();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Timber.d("onTimeSet(%s,%s)", hourOfDay, minute);
        this.mLocalDateTime = this.mLocalDateTime
                .withHour(hourOfDay)
                .withMinute(minute);
        this.updateShownTime();
    }

    @NonNull
    public abstract Task<Void> storeData();
}

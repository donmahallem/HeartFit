package com.github.donmahallem.heartfit.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.OffsetDateTimeConverter;
import com.github.donmahallem.heartfit.fragments.DatePickerDialogFragment;
import com.github.donmahallem.heartfit.fragments.TimePickerDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class InsertWeightActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String KEY_SELECTED_TIME = "key_selected_time";
    private HistoryClient mHistoryClient;
    private EditText mInputWeight, mInputFat;
    private OffsetDateTime mLocalDateTime;
    private Button mBtnSelectTime;
    private Button mBtnSelectDate;
    private TextInputLayout mInputLayoutWeight;
    private TextInputLayout mInputLayoutFat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_weight);
        this.mInputFat = this.findViewById(R.id.editFat);
        this.mInputLayoutFat=this.findViewById(R.id.textInputLayoutFat);
        this.mInputLayoutWeight=this.findViewById(R.id.textInputLayoutWeight);
        this.mInputWeight = this.findViewById(R.id.editWeight);
        this.mBtnSelectDate = this.findViewById(R.id.btnSelectDate);
        this.mBtnSelectTime = this.findViewById(R.id.btnSelectTime);
        this.mBtnSelectDate.setOnClickListener(this);
        this.mBtnSelectTime.setOnClickListener(this);
        this.mLocalDateTime = OffsetDateTime.now();
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SELECTED_TIME)) {
            this.mLocalDateTime = OffsetDateTimeConverter.toDate(savedInstanceState.getString(KEY_SELECTED_TIME));
        }
        this.mInputFat.addTextChangedListener(new ValidValueTextWatcher(this.mInputFat,this.mInputLayoutFat));
        this.mInputWeight.addTextChangedListener(new ValidValueTextWatcher(this.mInputWeight,this.mInputLayoutWeight));
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

    @Override
    public void onResume() {
        super.onResume();
        this.updateShownTime();
    }

    private void updateShownTime() {
        Timber.d("Update time");
        this.mBtnSelectTime.setText(this.mLocalDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        this.mBtnSelectDate.setText(this.mLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        savedInstance.putString(KEY_SELECTED_TIME, OffsetDateTimeConverter.toLong(this.mLocalDateTime));
    }

    public void selectTime() {

        TimePickerDialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.setOnTimeSetListener(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void selectDate() {
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setOnDateSetListener(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_dialog_insert_value, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                storeData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void storeData() {
        float weight = Float.parseFloat(this.mInputWeight.getText().toString());
        float fat = Float.parseFloat(this.mInputFat.getText().toString());
        Timber.d("Store weight: %s | fat: %s | on: %s", weight, fat, mLocalDateTime.format(DateTimeFormatter.ISO_DATE_TIME));

        DataSource weightDataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_WEIGHT)
                .setName(getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .setDevice(Device.getLocalDevice(this))
                .build();
        DataSource fatDataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setName(getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .setDevice(Device.getLocalDevice(this))
                .build();

        DataSet weightDataSet = DataSet.create(weightDataSource);
        final DataSet fatDataSet = DataSet.create(fatDataSource);
        DataPoint weightDatapoint = weightDataSet.createDataPoint();
        weightDatapoint.setTimestamp(this.mLocalDateTime.toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
        weightDatapoint.getValue(Field.FIELD_WEIGHT).setFloat(weight);
        weightDataSet.add(weightDatapoint);
        ////
        DataPoint fatDatapoint = fatDataSet.createDataPoint();
        fatDatapoint.setTimestamp(this.mLocalDateTime.toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
        fatDatapoint.getValue(Field.FIELD_PERCENTAGE).setFloat(fat);
        fatDataSet.add(fatDatapoint);
        Timber.d("WEIGHT %s",weightDataSet);
        Timber.d("FAT %s",fatDataSet);
        final HistoryClient historyClient = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this));
        historyClient.insertData(weightDataSet)
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        return historyClient.insertData(fatDataSet);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             Snackbar.make(mBtnSelectDate,"Success",Snackbar.LENGTH_SHORT).show();
                         }else {
                             Snackbar.make(mBtnSelectDate, "Failed", Snackbar.LENGTH_SHORT).show();
                         }
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Timber.d("onDateSet(%s,%s,%s)", year, month, day);
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

    private class ValidValueTextWatcher implements TextWatcher {
        private final EditText mEditText;
        private final Pattern mPattern=Pattern.compile("[0-9]+((\\.|,)[0-9]*)?");
        private final TextInputLayout mTextInputLayout;

        public ValidValueTextWatcher(EditText inputFat,TextInputLayout textInputLayout) {
            this.mEditText=inputFat;
            this.mTextInputLayout=textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            final Matcher matcher=mPattern.matcher(charSequence);
            if(matcher.matches()) {
                this.mTextInputLayout.setErrorEnabled(false);
            }else {
                this.mTextInputLayout.setErrorEnabled(true);
                this.mTextInputLayout.setError("Invalid");
            }
            Timber.d("CHANGE: %s",charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}


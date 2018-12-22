package com.github.donmahallem.heartfit.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class TimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private static final String KEY_TIME_HOUR = "key_time_hour";
    private static final String KEY_TIME_MINUTE = "key_time_minute";
    private WeakReference<TimePickerDialog.OnTimeSetListener> mListener;

    public static TimePickerDialogFragment newInstance(int hour,int minute){
        final TimePickerDialogFragment timePickerDialogFragment=new TimePickerDialogFragment();
        final Bundle args=new Bundle();
        args.putInt(KEY_TIME_HOUR,hour);
        args.putInt(KEY_TIME_MINUTE,minute);
        timePickerDialogFragment.setArguments(args);
        return timePickerDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if(this.getArguments()!=null){
            hour=this.getArguments().getInt(KEY_TIME_HOUR,hour);
            minute=this.getArguments().getInt(KEY_TIME_MINUTE,minute);
        }
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(mListener!=null&&this.mListener.get()!=null){
            this.mListener.get().onTimeSet(view,hourOfDay,minute);
        }
    }
    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener){
        this.mListener=new WeakReference<>(listener);
    }

}
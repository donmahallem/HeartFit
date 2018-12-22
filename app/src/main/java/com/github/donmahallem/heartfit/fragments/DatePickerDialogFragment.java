package com.github.donmahallem.heartfit.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String KEY_DATE_YEAR = "key_date_year";
    private static final String KEY_DATE_DAY = "key_date_day";
    private static final String KEY_DATE_MONTH = "key_date_month";
    private WeakReference<DatePickerDialog.OnDateSetListener> mListener;

    public static DatePickerDialogFragment newInstance(int year,int month,int day){
        final Bundle args=new Bundle();
        args.putInt(KEY_DATE_YEAR,year);
        args.putInt(KEY_DATE_MONTH,month);
        args.putInt(KEY_DATE_DAY,day);
        DatePickerDialogFragment datePickerDialogFragment=new DatePickerDialogFragment();
        datePickerDialogFragment.setArguments(args);
        return datePickerDialogFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if(this.getArguments()!=null){
            day =this.getArguments().getInt(KEY_DATE_DAY,day);
            month=this.getArguments().getInt(KEY_DATE_MONTH,month);
            year=this.getArguments().getInt(KEY_DATE_YEAR,year);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener){
        this.mListener=new WeakReference<>(listener);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(mListener!=null&&this.mListener.get()!=null){
            this.mListener.get().onDateSet(view,year,month,day);
        }
    }
}
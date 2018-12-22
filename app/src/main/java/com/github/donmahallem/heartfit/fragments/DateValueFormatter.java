package com.github.donmahallem.heartfit.fragments;

import com.github.donmahallem.heartfit.viewmodel.TimeFrame;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

class DateValueFormatter implements IAxisValueFormatter {
    private final TimeFrame mTimeFrame;

    public DateValueFormatter(TimeFrame timeFrame) {
        this.mTimeFrame=timeFrame;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        switch (this.mTimeFrame.getWindowSize()){
            case WEEK:
                return this.mTimeFrame.getStartTime().plusHours((long) Math.floor(value*12)).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
            case MONTH:
                return this.mTimeFrame.getStartTime().plusDays((long) value).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
            case YEAR:
                return this.mTimeFrame.getStartTime().plusDays((long)value*12).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        }
        return "-";
    }
}

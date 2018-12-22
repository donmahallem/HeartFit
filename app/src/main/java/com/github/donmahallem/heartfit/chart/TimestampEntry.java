package com.github.donmahallem.heartfit.chart;

import com.github.mikephil.charting.data.Entry;

public class TimestampEntry extends Entry {
    private long mTimestamp;
    public TimestampEntry(float x,float y, long timestamp, Object data){
        super(x,y,data);
        this.mTimestamp=timestamp;
    }
    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }
}

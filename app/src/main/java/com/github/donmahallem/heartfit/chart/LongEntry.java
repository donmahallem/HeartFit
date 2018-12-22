package com.github.donmahallem.heartfit.chart;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.Entry;

public class LongEntry extends Entry implements Parcelable {
    private  long mX;

    public LongEntry(long x, float y) {
        this(x,y, null);
    }
    public LongEntry(long x, float y, Object data) {
        super(x,y, data);
        this.mX=x;
    }

    protected LongEntry(Parcel in) {
        this.setY(in.readFloat());
        mX = in.readLong();
    }

    public void setX(long x) {
        mX = x;
    }

    public static final Creator<LongEntry> CREATOR = new Creator<LongEntry>() {
        @Override
        public LongEntry createFromParcel(Parcel in) {
            return new LongEntry(in);
        }

        @Override
        public LongEntry[] newArray(int size) {
            return new LongEntry[size];
        }
    };

    public long getLongX() {
        return mX;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.getY());
        parcel.writeLong(this.getLongX());
    }
}

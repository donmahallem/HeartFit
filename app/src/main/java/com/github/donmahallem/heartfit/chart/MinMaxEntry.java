package com.github.donmahallem.heartfit.chart;

import com.github.mikephil.charting.data.Entry;

public class MinMaxEntry extends Entry {

    private float mMax;
    private float mMin;
    public MinMaxEntry(float x, float y,float minY,float maxY) {
        this(x,y,minY,maxY,null);
    }
    public MinMaxEntry(float x, float y,float minY,float maxY, Object data) {
        super(x,y, data);
        if(y>maxY)
            throw new IllegalArgumentException("y cant be greater than maxY");
        if(y<minY)
            throw new IllegalArgumentException("y cant be smaller than minY");
        this.mMax=maxY;
        this.mMin=minY;
    }

    public float getMaxY(){
        return this.mMax;
    }

    public float getMinY(){
        return this.mMin;
    }
}

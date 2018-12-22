package com.github.donmahallem.heartfit.fragments;

import com.github.donmahallem.heartfit.viewmodel.WeightGraphFragmentViewModel;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.fitness.result.DataReadResponse;

/**
 * Created on 03.12.2018.
 */
class DisplayData extends DataReadResponse {
    private final int mSize;
    private final LineData mLineData;
    private final WeightGraphFragmentViewModel.WindowSize mWindowSize;

    public DisplayData(LineData lineData, WeightGraphFragmentViewModel.WindowSize windowSize, int size) {
        this.mLineData=lineData;
        this.mWindowSize=windowSize;
        this.mSize=size;
    }

    public int getSize() {
        return mSize;
    }

    public LineData getLineData() {
        return mLineData;
    }

    public WeightGraphFragmentViewModel.WindowSize getWindowSize() {
        return mWindowSize;
    }
}

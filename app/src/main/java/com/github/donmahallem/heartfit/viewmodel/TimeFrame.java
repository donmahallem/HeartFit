package com.github.donmahallem.heartfit.viewmodel;

import org.threeten.bp.OffsetDateTime;

/**
 * Created on 03.12.2018.
 */
public class TimeFrame   {
    private final WeightGraphFragmentViewModel.WindowSize mWindowSize;
    private final OffsetDateTime mStartTime;
    private final OffsetDateTime mEndTime;

    public TimeFrame(OffsetDateTime start, OffsetDateTime end, WeightGraphFragmentViewModel.WindowSize windowSize) {
        this.mStartTime=start;
        this.mEndTime=end;
        this.mWindowSize=windowSize;
    }

    public WeightGraphFragmentViewModel.WindowSize getWindowSize() {
        return mWindowSize;
    }

    public OffsetDateTime getStartTime() {
        return mStartTime;
    }

    public OffsetDateTime getEndTime() {
        return mEndTime;
    }

    @Override
    public String toString() {
        return "TimeFrame{" +
                "mWindowSize=" + mWindowSize +
                ", mStartTime=" + mStartTime +
                ", mEndTime=" + mEndTime +
                '}';
    }
}

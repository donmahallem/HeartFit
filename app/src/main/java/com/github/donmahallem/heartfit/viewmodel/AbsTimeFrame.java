package com.github.donmahallem.heartfit.viewmodel;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;

public abstract class AbsTimeFrame<T extends AbsTimeFrame> {
    private final OffsetDateTime mStartTime;
    private final OffsetDateTime mEndTime;
    private final WeightGraphFragmentViewModel.WindowSize mWindowSize;

    protected AbsTimeFrame(OffsetDateTime startTime, OffsetDateTime endTime, WeightGraphFragmentViewModel.WindowSize windowSize) {
        mStartTime = startTime.truncatedTo(ChronoUnit.DAYS);
        mEndTime = endTime.truncatedTo(ChronoUnit.DAYS);
        mWindowSize = windowSize;
    }

    public abstract T next();

    public OffsetDateTime getStartTime() {
        return mStartTime;
    }

    public OffsetDateTime getEndTime() {
        return mEndTime;
    }

    public WeightGraphFragmentViewModel.WindowSize getWindowSize() {
        return mWindowSize;
    }

    public abstract T previous();
}

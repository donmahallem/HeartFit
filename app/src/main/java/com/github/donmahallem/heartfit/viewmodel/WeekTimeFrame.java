package com.github.donmahallem.heartfit.viewmodel;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.TemporalAdjusters;

public class WeekTimeFrame extends AbsTimeFrame<WeekTimeFrame> {
    private final int mWeekOfYear;
    private final int mYear;

    public WeekTimeFrame(int weekOfYear, int year) {
        super(OffsetDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), null, WeightGraphFragmentViewModel.WindowSize.WEEK);
        this.mWeekOfYear = weekOfYear;
        this.mYear = year;
    }

    @Override
    public WeekTimeFrame next() {
        if (this.mWeekOfYear == 0) {
            return new WeekTimeFrame(this.mWeekOfYear - 1, this.mYear);
        } else {
            return new WeekTimeFrame(this.mWeekOfYear - 1, this.mYear);
        }
    }

    @Override
    public WeekTimeFrame previous() {
        return null;
    }
}

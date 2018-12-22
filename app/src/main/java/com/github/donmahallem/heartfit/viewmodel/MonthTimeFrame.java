package com.github.donmahallem.heartfit.viewmodel;


import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalAdjusters;

public class MonthTimeFrame extends AbsTimeFrame<MonthTimeFrame> {
    int year;
    int month;


    public MonthTimeFrame(int month, int year) {
        super(TimeFrameUtil.getMonthStart(month, year), TimeFrameUtil.getMonthEnd(month, year), WeightGraphFragmentViewModel.WindowSize.MONTH);
    }

    private MonthTimeFrame(OffsetDateTime start, OffsetDateTime end) {
        super(start, end, WeightGraphFragmentViewModel.WindowSize.MONTH);
    }


    @Override
    public MonthTimeFrame next() {
        final OffsetDateTime newStart = this.getStartTime().with(TemporalAdjusters.firstDayOfNextMonth()).truncatedTo(ChronoUnit.DAYS);
        final OffsetDateTime newEnd = newStart.with(TemporalAdjusters.firstDayOfNextMonth()).truncatedTo(ChronoUnit.DAYS);
        return new MonthTimeFrame(newStart, newEnd);
    }

    @Override
    public MonthTimeFrame previous() {
        final OffsetDateTime newEnd = this.getStartTime();
        final OffsetDateTime newStart = this.getStartTime().minusDays(3).with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
        return new MonthTimeFrame(newStart, newEnd);
    }
}

package com.github.donmahallem.heartfit.viewmodel;


import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalAdjusters;

public class YearTimeFrame extends AbsTimeFrame<YearTimeFrame> {

    public YearTimeFrame(int year) {
        this(OffsetDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS), OffsetDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfNextYear()).truncatedTo(ChronoUnit.DAYS));
    }

    protected YearTimeFrame(OffsetDateTime startTime, OffsetDateTime endTime) {
        super(startTime, endTime, WeightGraphFragmentViewModel.WindowSize.YEAR);
    }

    @Override
    public YearTimeFrame next() {
        final OffsetDateTime newStart = this.getStartTime().with(TemporalAdjusters.firstDayOfNextYear()).truncatedTo(ChronoUnit.DAYS);
        final OffsetDateTime newEnd = newStart.with(TemporalAdjusters.firstDayOfNextYear()).truncatedTo(ChronoUnit.DAYS);
        return new YearTimeFrame(newStart, newEnd);
    }

    @Override
    public YearTimeFrame previous() {
        final OffsetDateTime newEnd = this.getStartTime().with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
        final OffsetDateTime newStart = newEnd.minusMonths(2).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
        return new YearTimeFrame(newStart, newEnd);
    }
}

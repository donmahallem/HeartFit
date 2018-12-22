package com.github.donmahallem.heartfit.viewmodel;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalAdjusters;

public class TimeFrameUtil {
    public static OffsetDateTime getMonthStart(int month, int year){
        return OffsetDateTime.now()
                .withYear(year)
                .withMonth(month)
                .with(TemporalAdjusters.firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS);
    }
    public static OffsetDateTime getMonthEnd(int month,int year){
        return OffsetDateTime.now()
                .withYear(year)
                .withMonth(month)
                .with(TemporalAdjusters.firstDayOfNextMonth())
                .truncatedTo(ChronoUnit.DAYS);
    }
    public static OffsetDateTime getYearStart(int year){
        return OffsetDateTime.now()
                .withYear(year)
                .with(TemporalAdjusters.firstDayOfYear())
                .truncatedTo(ChronoUnit.DAYS);
    }
    public static OffsetDateTime getYearEnd(int year){
        return OffsetDateTime.now()
                .withYear(year)
                .with(TemporalAdjusters.firstDayOfNextYear())
                .truncatedTo(ChronoUnit.DAYS);
    }
}

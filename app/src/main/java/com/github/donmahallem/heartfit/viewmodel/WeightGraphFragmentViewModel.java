package com.github.donmahallem.heartfit.viewmodel;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalAdjusters;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created on 20.11.2018.
 */
public class WeightGraphFragmentViewModel extends ViewModel {

    private final BehaviorSubject<OffsetDateTime> mReferenceTimeSubject = BehaviorSubject.createDefault(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS));
    private final BehaviorSubject<WindowSize> mWindowSizeSubject = BehaviorSubject.createDefault(WindowSize.MONTH);
    private final Flowable<WindowSize> mWindowSizeFlowable = this.mWindowSizeSubject.toFlowable(BackpressureStrategy.LATEST);
    private final Flowable<OffsetDateTime> mEndTimeFlowable = this.mReferenceTimeSubject.toFlowable(BackpressureStrategy.LATEST);

    public void moveWindowRight() {
        switch (this.mWindowSizeSubject.getValue()){
            case WEEK:
                this.mReferenceTimeSubject.onNext(this.mReferenceTimeSubject.getValue().plusWeeks(1));
                break;
            case MONTH:
                this.mReferenceTimeSubject.onNext(this.mReferenceTimeSubject.getValue().plusMonths(1));
                break;
            case YEAR:
                this.mReferenceTimeSubject.onNext(this.mReferenceTimeSubject.getValue().plusYears(1));
                break;
        }
    }

    public void moveWindowLeft() {
        switch (this.mWindowSizeSubject.getValue()){
            case WEEK:
                this.mReferenceTimeSubject.onNext(this.mReferenceTimeSubject.getValue().minusWeeks(1));
                break;
            case MONTH:
                this.mReferenceTimeSubject.onNext(this.mReferenceTimeSubject.getValue().minusMonths(1));
                break;
            case YEAR:
                this.mReferenceTimeSubject.onNext(this.mReferenceTimeSubject.getValue().minusYears(1));
                break;
        }
    }

    public void setWindowSize(@NonNull WindowSize windowSize) {
        this.mWindowSizeSubject.onNext(windowSize);
    }

    public Flowable<WindowSize> getWindowSizeFlowable() {
        return this.mWindowSizeFlowable;
    }

    public Flowable<TimeFrame> getTimeFrameFlowable() {
        return Flowable.combineLatest(this.mWindowSizeFlowable, this.mEndTimeFlowable, new BiFunction<WindowSize, OffsetDateTime, TimeFrame>() {
            @Override
            public TimeFrame apply(WindowSize windowSize, OffsetDateTime referenceTime) throws Exception {
                OffsetDateTime start = null;
                OffsetDateTime end = null;
                switch (windowSize) {
                    case WEEK:
                        start=referenceTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
                        end=referenceTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
                        break;
                    case MONTH:
                        start=referenceTime.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
                        end=referenceTime.with(TemporalAdjusters.firstDayOfNextMonth()).truncatedTo(ChronoUnit.DAYS);
                        break;
                    case YEAR:
                        start=referenceTime.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
                        end=referenceTime.with(TemporalAdjusters.firstDayOfNextYear()).truncatedTo(ChronoUnit.DAYS);
                        break;
                }
                return new TimeFrame(start, end,windowSize);
            }
        }).observeOn(Schedulers.computation());
    }

    public WindowSize getWindowSize() {
        return this.mWindowSizeSubject.getValue();
    }

    public enum WindowSize {
        WEEK, MONTH, YEAR
    }

}

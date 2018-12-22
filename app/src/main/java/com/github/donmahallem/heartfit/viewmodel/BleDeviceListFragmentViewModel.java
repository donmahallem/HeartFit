package com.github.donmahallem.heartfit.viewmodel;

import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created on 19.11.2018.
 */
public class BleDeviceListFragmentViewModel extends ViewModel {

    private BehaviorSubject<Boolean> mScanIsInProgressSubject=BehaviorSubject.createDefault(false);


    public boolean isScanInProgress(){
        return this.mScanIsInProgressSubject.getValue();
    }

    public Flowable<Boolean> isScanInProgressObservable(){
        return this.mScanIsInProgressSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public void setScanInProgress(boolean inProgress) {
        this.mScanIsInProgressSubject.onNext(inProgress);
    }
}

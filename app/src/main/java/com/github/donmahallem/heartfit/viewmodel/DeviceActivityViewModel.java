package com.github.donmahallem.heartfit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.subjects.BehaviorSubject;

public class DeviceActivityViewModel extends ViewModel {

    private BehaviorSubject<Boolean> mHasBodySensorPermission;

    private MutableLiveData<Boolean> mIsScanningForDevices = new MutableLiveData<>();

    public DeviceActivityViewModel() {
        this.mIsScanningForDevices.setValue(false);
    }

    public LiveData<Boolean> isScanningForDevices() {
        return this.mIsScanningForDevices;
    }
}

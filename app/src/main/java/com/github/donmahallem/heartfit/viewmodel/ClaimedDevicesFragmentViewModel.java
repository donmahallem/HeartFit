package com.github.donmahallem.heartfit.viewmodel;

import com.google.android.gms.fitness.BleClient;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created on 22.11.2018.
 */
public class ClaimedDevicesFragmentViewModel extends ViewModel {
    private final BehaviorSubject<RefreshState> mRefreshStateBehaviorSubject = BehaviorSubject.createDefault(RefreshState.INIT);
    private final BehaviorSubject<List<BleDevice>> mBleDeviceListSubject = BehaviorSubject.createDefault(Collections.<BleDevice>emptyList());
    private final BiConsumer<? super List<BleDevice>, ? super Throwable> mUpdateBiConsumer = new BiConsumer<List<BleDevice>, Throwable>() {
        @Override
        public void accept(List<BleDevice> bleDevices, Throwable throwable) throws Exception {
            if (throwable != null) {
                mRefreshStateBehaviorSubject.onNext(RefreshState.ERRORED);
            } else {
                mBleDeviceListSubject.onNext(bleDevices);
                mRefreshStateBehaviorSubject.onNext(RefreshState.REFRESHED);
            }
        }
    };

    public Completable refreshData(final BleClient bleClient) {
        return Single.create(new SingleOnSubscribe<List<BleDevice>>() {
            @Override
            public void subscribe(SingleEmitter<List<BleDevice>> emitter) throws Exception {
                Task<List<BleDevice>> task = bleClient.listClaimedBleDevices();
                List<BleDevice> devices = Tasks.await(task, 10, TimeUnit.SECONDS);
                if (task.isSuccessful()) {
                    emitter.onSuccess(devices);
                } else {
                    emitter.onError(task.getException());
                }
            }

        })
                .doOnEvent(this.mUpdateBiConsumer)
                .ignoreElement();
    }

    public Flowable<RefreshState> getRefreshStateFlowable() {
        return this.mRefreshStateBehaviorSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    @NonNull
    public RefreshState getRefreshState() {
        return this.mRefreshStateBehaviorSubject.getValue();
    }

    public void setRefreshState(@NonNull RefreshState refreshState) {
        this.mRefreshStateBehaviorSubject.onNext(refreshState);
    }

    public Flowable<List<BleDevice>> getBleDeviceListFlowable(){
        return this.mBleDeviceListSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public boolean isRefreshing() {
        return this.mRefreshStateBehaviorSubject.getValue() == RefreshState.REFRESHING;
    }

    public enum RefreshState {
        REFRESHING,
        REFRESHED,
        INIT,
        ERRORED
    }
}

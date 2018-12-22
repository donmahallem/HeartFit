package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.recycler.viewholder.BleDeviceSimpleViewHolder;
import com.github.donmahallem.heartfit.viewholder.BleDeviceViewHolder;
import com.google.android.gms.fitness.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class ClaimedDevicesAdapter extends RecyclerView.Adapter<BleDeviceSimpleViewHolder> {
    private final List<BleDevice> mDevices=new ArrayList<>();
    private final PublishSubject<BleDevice> onClickSubject = PublishSubject.create();
    @NonNull
    @Override
    public BleDeviceSimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BleDeviceSimpleViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final BleDeviceSimpleViewHolder holder, int position) {
        holder.setSession(this.mDevices.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubject.onNext(holder.getBleDevice());
            }
        });
    }

    public void setDevices(List<BleDevice> devices){
        this.mDevices.clear();
        this.mDevices.addAll(devices);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mDevices.size();
    }
    public Flowable<BleDevice> getPositionClicks(){
        return onClickSubject.toFlowable(BackpressureStrategy.LATEST);
    }
}

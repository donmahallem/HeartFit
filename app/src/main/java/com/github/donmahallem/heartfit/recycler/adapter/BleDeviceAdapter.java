package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.recycler.viewholder.BleDeviceSimpleViewHolder;
import com.github.donmahallem.heartfit.recycler.viewholder.SessionSimpleViewHolder;
import com.google.android.gms.fitness.data.BleDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created on 07.11.2018.
 */
public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceSimpleViewHolder> {

    private SessionSimpleViewHolder.OnSessionInteractListener mOnSessionInteractListener;
    private List<BleDevice> mSessionList = new ArrayList<>();

    private final PublishSubject<BleDevice> onClickSubject = PublishSubject.create();
    @NonNull
    @Override
    public BleDeviceSimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final BleDeviceSimpleViewHolder viewHolder = new BleDeviceSimpleViewHolder(parent);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final BleDeviceSimpleViewHolder holder, int position) {
        holder.setSession(this.mSessionList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubject.onNext(holder.getBleDevice());
            }
        });
    }

    public void addBleDevice(@NonNull final BleDevice bleDevice) {
        if(!this.mSessionList.contains(bleDevice)){
            List<BleDevice> dest=new ArrayList<>();
            Collections.copy(dest,this.mSessionList);
            dest.add(bleDevice);
            setSessions(dest);
        }
    }
    public Flowable<BleDevice> getPositionClicks(){
        return onClickSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public void setSessions(@NonNull final List<BleDevice> sessions) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return BleDeviceAdapter.this.mSessionList.size();
            }

            @Override
            public int getNewListSize() {
                return sessions.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return sessions.get(newItemPosition).getAddress().equals(BleDeviceAdapter.this.mSessionList.get(oldItemPosition).getAddress());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }
        }, true);
        this.mSessionList.clear();
        this.mSessionList.addAll(sessions);
        result.dispatchUpdatesTo(this);
        Timber.d("List updated");
        //this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mSessionList.size();
    }

}

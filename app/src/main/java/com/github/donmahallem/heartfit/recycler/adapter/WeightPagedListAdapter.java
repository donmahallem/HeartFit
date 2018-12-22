package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.pagination.WeightTimestamp;
import com.github.donmahallem.heartfit.recycler.viewholder.WeightTimestampViewHolder;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created on 10.11.2018.
 */
public class WeightPagedListAdapter extends PagedListAdapter<WeightTimestamp, WeightTimestampViewHolder> {

    public static final DiffUtil.ItemCallback<WeightTimestamp> ITEM_CALLBACK = new DiffUtil.ItemCallback<WeightTimestamp>() {
        @Override
        public boolean areItemsTheSame(@NonNull WeightTimestamp oldItem, @NonNull WeightTimestamp newItem) {
            return oldItem.getTimestamp() == newItem.getTimestamp();
        }

        @Override
        public boolean areContentsTheSame(@NonNull WeightTimestamp oldItem, @NonNull WeightTimestamp newItem) {
            return oldItem.getWeight() == newItem.getWeight();
        }
    };
    private final PublishSubject<WeightTimestamp> mClickSubject = PublishSubject.create();
    private final PublishSubject<WeightTimestamp> mLongClickSubject = PublishSubject.create();

    public WeightPagedListAdapter() {
        super(ITEM_CALLBACK);
    }

    public Flowable<WeightTimestamp> getOnLongClickFlowable() {
        return this.mLongClickSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<WeightTimestamp> getOnClickFlowable() {
        return this.mClickSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    @NonNull
    @Override
    public WeightTimestampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeightTimestampViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightTimestampViewHolder holder, int position) {
        final WeightTimestamp weightTimestamp = this.getItem(position);
        holder.setWeightTimestamp(weightTimestamp);
        final ClickPublisher clickPublisher=new ClickPublisher(weightTimestamp,this.mClickSubject,this.mLongClickSubject);
        holder.itemView.setOnClickListener(clickPublisher);
        holder.itemView.setOnLongClickListener(clickPublisher);
    }

    @Override
    public WeightTimestamp getItem(int position) {
        return super.getItem(position);
    }

    private class ClickPublisher implements View.OnClickListener, View.OnLongClickListener {
        private final PublishSubject<WeightTimestamp> mLongClickSubject;
        private final PublishSubject<WeightTimestamp> mClickSubject;
        private final WeightTimestamp mWeightTimestamp;

        public ClickPublisher(WeightTimestamp weightTimestamp, PublishSubject<WeightTimestamp> clickSubject, PublishSubject<WeightTimestamp> longClickSubject) {
            this.mWeightTimestamp=weightTimestamp;
            this.mClickSubject=clickSubject;
            this.mLongClickSubject=longClickSubject;
        }

        @Override
        public void onClick(View view) {
            this.mClickSubject.onNext(this.mWeightTimestamp);
        }

        @Override
        public boolean onLongClick(View view) {
            this.mLongClickSubject.onNext(this.mWeightTimestamp);
            return true;
        }
    }
}

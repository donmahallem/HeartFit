package com.github.donmahallem.heartfit.module.nutritionviewer.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.module.nutritionviewer.model.NutritionHistoryBucket;
import com.github.donmahallem.heartfit.module.nutritionviewer.recycler.viewholder.NutritionHistoryViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class NutritionListAdapter extends RecyclerView.Adapter<NutritionHistoryViewHolder> {

    private final List<NutritionHistoryBucket> mHistory=new ArrayList<>();
    private final PublishSubject<NutritionHistoryBucket> mClickListener=PublishSubject.create();
    @NonNull
    @Override
    public NutritionHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NutritionHistoryViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull NutritionHistoryViewHolder holder, int position) {
        holder.setNutritionHistory(this.mHistory.get(position));
        holder.itemView.setOnClickListener(new ItemClickListener(holder,mClickListener));
    }

    @Override
    public void onViewRecycled (@NonNull NutritionHistoryViewHolder holder){
        holder.itemView.setOnClickListener(null);
    }

    public void setHistory(List<NutritionHistoryBucket> list){
        this.mHistory.clear();
        this.mHistory.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mHistory.size();
    }

    public Flowable<NutritionHistoryBucket> getClickListener() {
        return this.mClickListener.toFlowable(BackpressureStrategy.LATEST);
    }

    private class ItemClickListener implements View.OnClickListener {
        private final PublishSubject<NutritionHistoryBucket> mClickListener;
        private final NutritionHistoryViewHolder mHolder;

        public ItemClickListener(NutritionHistoryViewHolder holder, PublishSubject<NutritionHistoryBucket> clickListener) {
            this.mHolder=holder;
            this.mClickListener=clickListener;
        }

        @Override
        public void onClick(View view) {
            if(this.mHolder.itemView==view){
                this.mClickListener.onNext(this.mHolder.getNutritionHistory());
            }
        }
    }
}

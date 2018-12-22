package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.fit.FitResourceUtil;
import com.github.donmahallem.heartfit.recycler.viewholder.BottomSheetListItemViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class ResistanceTypesAdapter extends RecyclerView.Adapter<BottomSheetListItemViewHolder> {

    private final PublishSubject<Integer> mResistanceTypeSubject =PublishSubject.create();

    public Flowable<Integer> getOnResistanceTypeSelectedFlowable(){
        return this.mResistanceTypeSubject.toFlowable(BackpressureStrategy.DROP);
    }

    @NonNull
    @Override
    public BottomSheetListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final BottomSheetListItemViewHolder viewHolder= new BottomSheetListItemViewHolder(parent);
        viewHolder.setHasIcon(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetListItemViewHolder holder, int position) {
        holder.setText(FitResourceUtil.get(position));
        holder.itemView.setOnClickListener(createClickListener(position));
    }

    private View.OnClickListener createClickListener(final int resistanceType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResistanceTypesAdapter
                        .this
                        .mResistanceTypeSubject.onNext(resistanceType);
            }
        };
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}

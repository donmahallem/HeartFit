package com.github.donmahallem.common.recycler.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class LayoutViewHolder extends RecyclerView.ViewHolder {
    public LayoutViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false));
    }
}

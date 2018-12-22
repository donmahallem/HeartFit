package com.github.donmahallem.heartfit.recycler.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

public class LayoutViewHolder extends RecyclerView.ViewHolder {

    public LayoutViewHolder(ViewGroup parent, @LayoutRes int layoutRes){
        super(LayoutInflater.from(parent.getContext()).inflate(layoutRes,parent,false));
    }
}

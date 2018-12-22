package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.ViewGroup;

import com.github.donmahallem.heartfit.recycler.viewholder.DataSourceViewHolder;
import com.google.android.gms.fitness.data.DataSource;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * Created on 13.12.2018.
 */
public class DataSourceAdapter extends RecyclerView.Adapter<DataSourceViewHolder> {
    private List<DataSource> mItems=new ArrayList<>();

    @NonNull
    @Override
    public DataSourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DataSourceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setItems(List<DataSource> dataSources) {
        this.mItems.clear();
        this.mItems.addAll(dataSources);
        for(DataSource dataSource:dataSources){
            Timber.d("Datasource: %s",dataSource);
        }
        this.notifyDataSetChanged();
    }
}

package com.github.donmahallem.heartfit.pagination;

import com.google.android.gms.fitness.HistoryClient;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

public class UserWeightDataSourceFactory extends DataSource.Factory<Long,WeightTimestamp> {
    private final HistoryClient mHistoryClient;

    public UserWeightDataSourceFactory(HistoryClient historyClient) {
        mHistoryClient = historyClient;
    }

    @NonNull
    @Override
    public DataSource<Long, WeightTimestamp> create() {
        return new UserWeightDataSource(this.mHistoryClient);
    }
}

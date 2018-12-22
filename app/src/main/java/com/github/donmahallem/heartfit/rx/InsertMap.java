package com.github.donmahallem.heartfit.rx;

import com.github.donmahallem.heartfit.db.CacheDatabase;

import io.reactivex.functions.Function;

public class InsertMap implements Function<GoogleFitWeightRequest.Data, Boolean> {
    private final CacheDatabase mDatabase;

    public InsertMap(CacheDatabase database) {
        this.mDatabase=database;
    }

    @Override
    public Boolean apply(GoogleFitWeightRequest.Data data) throws Exception {
        this.mDatabase.bodyWeightDao().updateRange(data.mTimeFrame.getStartTime(),data.mTimeFrame.getEndTime(),data.mBodyWeights);
        this.mDatabase.bodyFatDao().updateRange(data.mTimeFrame.getStartTime(),data.mTimeFrame.getEndTime(),data.mBodyFatPercentages);
        return true;
    }
}

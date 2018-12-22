package com.github.donmahallem.heartfit.rx;

import com.github.donmahallem.heartfit.db.BodyFatPercentage;
import com.github.donmahallem.heartfit.db.BodyWeight;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.viewmodel.TimeFrame;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class DatabaseRequest implements Function<TimeFrame,Flowable<GoogleFitWeightRequest.Data>> {
    private final CacheDatabase mDatabase;

    public DatabaseRequest(CacheDatabase database) {
        this.mDatabase=database;
    }

    @Override
    public Flowable<GoogleFitWeightRequest.Data> apply(final TimeFrame timeFrame) throws Exception {
        return Flowable.combineLatest(this.mDatabase.bodyWeightDao().getArea(timeFrame.getStartTime(), timeFrame.getEndTime()), this.mDatabase.bodyFatDao().getArea(timeFrame.getStartTime(), timeFrame.getEndTime()), new BiFunction<List<BodyWeight>, List<BodyFatPercentage>, GoogleFitWeightRequest.Data>() {
            @Override
            public GoogleFitWeightRequest.Data apply(List<BodyWeight> bodyWeights, List<BodyFatPercentage> bodyWeights2) throws Exception {
               GoogleFitWeightRequest.Data data=new GoogleFitWeightRequest.Data();
               data.mBodyFatPercentages.addAll(bodyWeights2);
               data.mBodyWeights.addAll(bodyWeights);
               data.mTimeFrame=timeFrame;
               return data;
            }
        });
    }
}

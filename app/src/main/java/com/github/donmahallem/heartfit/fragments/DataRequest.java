package com.github.donmahallem.heartfit.fragments;

import com.github.donmahallem.heartfit.db.BodyWeight;
import com.github.mikephil.charting.data.LineData;

import java.util.List;

/**
 * Created on 03.12.2018.
 */
class DataRequest implements io.reactivex.functions.Function<List<BodyWeight>, LineData> {
    @Override
    public LineData apply(List<BodyWeight> bodyWeights) throws Exception {
        return null;
    }
}

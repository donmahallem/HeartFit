package com.github.donmahallem.heartfit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

public class InsertBodyWeightDialogFragment extends InsertValueDialogFragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insert_body_weight, container, false);
    }

    @Override
    public Task<Void> storeData() {
        final Context context = this.getContext();
        if (context == null)
            return null;
        DataSource weightDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_WEIGHT)
                .setName(getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .setDevice(Device.getLocalDevice(context))
                .build();

        DataSet weightDataSet = DataSet.create(weightDataSource);
        DataPoint weightDatapoint = weightDataSet.createDataPoint();
        weightDatapoint.setTimestamp(this.getTimestamp().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
        weightDatapoint.getValue(Field.FIELD_WEIGHT).setFloat(this.getValue());
        weightDataSet.add(weightDatapoint);
        Timber.d("HAS %s",weightDataSet);
        return Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .insertData(weightDataSet);
    }
}

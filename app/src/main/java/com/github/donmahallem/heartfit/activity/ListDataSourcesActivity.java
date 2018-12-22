package com.github.donmahallem.heartfit.activity;

import android.os.Bundle;

import com.github.donmahallem.common.recycler.activity.GoogleSignInActivity;
import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.recycler.adapter.DataSourceAdapter;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.SensorsClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import timber.log.Timber;

public class ListDataSourcesActivity extends GoogleSignInActivity implements SwipeRefreshLayout.OnRefreshListener {

    private DataSourceAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    public ListDataSourcesActivity() {
        super(Fitness.SCOPE_ACTIVITY_READ,
                Fitness.SCOPE_BODY_READ,
                Fitness.SCOPE_LOCATION_READ,
                Fitness.SCOPE_NUTRITION_READ);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_datasources);
        this.mAdapter = new DataSourceAdapter();
        this.mRecyclerView = this.findViewById(R.id.recyclerView);
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRefreshLayout = this.findViewById(R.id.swipeRefreshLayout);
        this.mRefreshLayout.setOnRefreshListener(this);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onRefresh();
    }

    @Override
    public void onRefresh() {
        this.mRefreshLayout.setRefreshing(true);
        DataSourcesRequest request = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_NUTRITION,DataType.TYPE_WEIGHT,DataType.TYPE_BODY_FAT_PERCENTAGE,DataType.TYPE_HEART_RATE_BPM)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();
        SensorsClient sensorsClientClient = Fitness.getSensorsClient(this, getSignedInAccount());
        sensorsClientClient.findDataSources(request)
                .addOnSuccessListener(new OnSuccessListener<List<DataSource>>() {
                    @Override
                    public void onSuccess(List<DataSource> dataSources) {
                        Timber.d("Recieved Items: %s",dataSources.size());
                        mAdapter.setItems(dataSources);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<DataSource>>() {
            @Override
            public void onComplete(@NonNull Task<List<DataSource>> task) {
                mRefreshLayout.setRefreshing(false);

            }
        });

    }
}

package com.github.donmahallem.heartfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.pagination.UserWeightDataSourceFactory;
import com.github.donmahallem.heartfit.pagination.WeightTimestamp;
import com.github.donmahallem.heartfit.recycler.adapter.WeightPagedListAdapter;
import com.github.donmahallem.heartfit.recycler.decorator.DateItemDecoration;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class WeightListFragment extends Fragment {

    private HistoryClient mHistoryClient;
    private LineChart mChart;
    private TabLayout mTabLayout;
    private RecyclerView mRecyclerView;
    private PagedList.Config mPagedListConfig;
    private WeightPagedListAdapter mAdapter;
    private UserWeightDataSourceFactory mUserWeightDataSourceFactory;
    private LiveData<PagedList<WeightTimestamp>> mLivePagedListBuilder;
    private LinearLayoutManager mLayoutManager;
    private Disposable mLongClickDisposable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weight_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRecyclerView=view.findViewById(R.id.recyclerView);
        this.mLayoutManager=new LinearLayoutManager(view.getContext());
        this.mPagedListConfig=new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(25)
                .setPageSize(25)
                .setPrefetchDistance(25)
                .build();
        this.mHistoryClient=Fitness.getHistoryClient(this.getActivity(),GoogleSignIn.getLastSignedInAccount(this.getActivity()));
        this.mAdapter=new WeightPagedListAdapter();
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),this.mLayoutManager.getOrientation()));
        this.mRecyclerView.addItemDecoration(new DateItemDecoration(view.getContext()));
        this.mUserWeightDataSourceFactory= new UserWeightDataSourceFactory(this.mHistoryClient);
        this.mLivePagedListBuilder=new LivePagedListBuilder<Long,WeightTimestamp>(this.mUserWeightDataSourceFactory,mPagedListConfig).build();
        this.mLivePagedListBuilder.observe(this, new Observer<PagedList<WeightTimestamp>>() {
            @Override
            public void onChanged(PagedList<WeightTimestamp> weightTimestamp) {
                mAdapter.submitList(weightTimestamp);
            }

        });

        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mLongClickDisposable.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mLongClickDisposable=this.mAdapter.getOnLongClickFlowable()
                .subscribe(new Consumer<WeightTimestamp>() {
                    @Override
                    public void accept(WeightTimestamp weightTimestamp) throws Exception {
                        DataDeleteRequest dataDeleteRequest=new DataDeleteRequest.Builder()
                                .addDataType(DataType.TYPE_WEIGHT)
                                .setTimeInterval(weightTimestamp.getTimestamp(),weightTimestamp.getTimestamp()+1,TimeUnit.MILLISECONDS)
                                .build();
/*
                        Fitness.getHistoryClient(getActivity(),GoogleSignIn.getLastSignedInAccount(getContext()))
                                .deleteData(dataDeleteRequest)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Timber.d("data deleted");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.e(e);
                                    }
                                });*/
                    }
                });
    }

}

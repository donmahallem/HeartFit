package com.github.donmahallem.heartfit.module.nutritionviewer.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.donmahallem.common.recycler.fragment.FragmentList;
import com.github.donmahallem.heartfit.module.nutritionviewer.model.NutritionHistoryBucket;
import com.github.donmahallem.heartfit.module.nutritionviewer.recycler.adapter.NutritionListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NutritionListFragment extends FragmentList {

    private  NutritionListAdapter mAdapter;
    private Disposable mNutritionDisposable;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.weight_history_graph, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mAdapter=new NutritionListAdapter();
        super.getRecyclerView().setAdapter(this.mAdapter);
    }
    @Override
    public void onResume(){
        super.onResume();
        final OffsetDateTime endTime=OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1);
        DataReadRequest.Builder builder=new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_NUTRITION,DataType.AGGREGATE_NUTRITION_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED,DataType.AGGREGATE_CALORIES_EXPENDED)
                .setTimeRange(endTime.minusWeeks(1).toEpochSecond(),endTime.toEpochSecond(), TimeUnit.SECONDS)
                .bucketByTime(1,TimeUnit.DAYS)               ;
        Fitness.getHistoryClient(this.getContext(),GoogleSignIn.getLastSignedInAccount(this.getContext()))
                .readData(builder.build())
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                        if(task.isSuccessful()){
                            updateData(task.getResult());
                        }else{
                        }
                    }
                });
        this.mNutritionDisposable =this.mAdapter.getClickListener()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<NutritionHistoryBucket>() {
                    @Override
                    public void accept(NutritionHistoryBucket nutritionHistoryBucket) throws Exception {
                        NutritionDetailDialogFragment fragment=NutritionDetailDialogFragment.createInstance(nutritionHistoryBucket);
                        fragment.show(getChildFragmentManager(),"item");
                    }
                });
    }

    @Override
    public void onPause(){
        super.onPause();
        this.mNutritionDisposable.dispose();
    }

    private void updateData(DataReadResponse result) {
        final List<NutritionHistoryBucket> list=new ArrayList<>();
        for(Bucket bucket:result.getBuckets()){
            list.add(new NutritionHistoryBucket.Builder().setData(bucket).build());
        }
        this.mAdapter.setHistory(list);
    }
}

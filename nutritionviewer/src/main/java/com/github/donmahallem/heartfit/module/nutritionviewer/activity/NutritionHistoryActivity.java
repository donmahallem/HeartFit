package com.github.donmahallem.heartfit.module.nutritionviewer.activity;

import android.os.Bundle;

import com.github.donmahallem.common.recycler.activity.GoogleSignInActivity;
import com.github.donmahallem.heartfit.module.nutritionviewer.R;
import com.github.donmahallem.heartfit.module.nutritionviewer.fragment.NutritionListFragment;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.result.DataReadResponse;

import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public final class NutritionHistoryActivity extends GoogleSignInActivity {

    public NutritionHistoryActivity() {
        super(Fitness.SCOPE_NUTRITION_READ);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_history);
        if(this.getSupportFragmentManager().findFragmentById(R.id.frameLayout)==null){
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout,new NutritionListFragment(),"nutrition_list");
            fragmentTransaction.commit();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(this.hasRequiredScopes()){
        }else{
            this.requestMissingScopes();
        }
    }

    private void updateData(DataReadResponse result) {
        for(Bucket bucket:result.getBuckets()){
            for(DataSet dataSet:bucket.getDataSets()){
                for(DataPoint dataPoint:dataSet.getDataPoints()){
                    Timber.d("Data %s - %s",dataPoint.getDataType(),dataPoint.toString());
                }
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}

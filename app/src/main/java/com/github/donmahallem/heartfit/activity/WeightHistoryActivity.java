package com.github.donmahallem.heartfit.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.donmahallem.common.recycler.activity.GoogleSignInActivity;
import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.chart.AreaLineChartRenderer;
import com.github.donmahallem.heartfit.fragments.InsertBodyWeightDialogFragment;
import com.github.donmahallem.heartfit.fragments.WeightGraphFragment;
import com.github.donmahallem.heartfit.fragments.WeightListFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class WeightHistoryActivity extends GoogleSignInActivity {

    private static final String KEY_SHOWING_GRAPH = "showing_graph";
    private boolean mShowingGraph = true;

    public WeightHistoryActivity() {
        super(Fitness.SCOPE_BODY_READ_WRITE);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);
        if (savedInstanceState != null) {
            this.mShowingGraph = savedInstanceState.getBoolean(KEY_SHOWING_GRAPH, true);
        }
        final Fragment currentFragment=getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(currentFragment==null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new WeightGraphFragment(), "weight_graph_fragment");
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weight_history, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem item=menu.findItem(R.id.menu_showmode);
        if(item!=null){
            item.setIcon(this.mShowingGraph?
                    R.drawable.ic_view_list_white_24dp:
                    R.drawable.ic_show_chart_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this,InsertWeightActivity.class));
                /*
                InsertBodyWeightDialogFragment fragment=new InsertBodyWeightDialogFragment();
                fragment.show(getSupportFragmentManager(),"select");*/
                return true;
            case R.id.menu_showmode:
                this.toggleViewingMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void toggleViewingMode() {
        this.mShowingGraph = !this.mShowingGraph;
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        if(this.mShowingGraph){
            fragmentTransaction.replace(R.id.frameLayout,new WeightGraphFragment(),"weight_graph_fragment");
        }else{
            fragmentTransaction.replace(R.id.frameLayout,new WeightListFragment(),"weight_list_fragment");
        }
        fragmentTransaction.commit();
        this.invalidateOptionsMenu();
    }

    @Override
    public void onResume(){
        super.onResume();
        this.invalidateOptionsMenu();
        if(!this.hasRequiredScopes()){
            this.requestMissingScopes();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putBoolean(KEY_SHOWING_GRAPH,this.mShowingGraph);
    }

}

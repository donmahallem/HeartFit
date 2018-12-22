package com.github.donmahallem.heartfit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.donmahallem.heartfit.adapter.DeviceRecyclerViewAdapter;
import com.github.donmahallem.heartfit.fragments.ClaimedDevicesFragment;
import com.github.donmahallem.heartfit.fragments.FragmentListDevices;
import com.github.donmahallem.heartfit.fragments.RequiresBodySensorFragment;
import com.github.donmahallem.heartfit.viewholder.OnBleDeviceClaimListener;
import com.github.donmahallem.heartfit.viewmodel.DeviceActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class DeviceActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private final TabLayout.BaseOnTabSelectedListener mTabSelectListener=new TabLayout.BaseOnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    showBleDeviceScan();
                    break;
                case 1:
                    showClaimedDevices();
                    break;
            }
        }


        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void showClaimedDevices() {
        final FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,new ClaimedDevicesFragment(),"claimed_devices");
        transaction.commit();
    }
    private void showBleDeviceScan(){
        final FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        if(hasBodySensorPermission()){
            transaction.replace(R.id.frameLayout,new FragmentListDevices(),"list_device_fragment");
        }else{
            transaction.replace(R.id.frameLayout,new RequiresBodySensorFragment(),"require_body_sensor");
        }
        transaction.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        this.mTabLayout=this.findViewById(R.id.tabLayout);
        this.mTabLayout.addOnTabSelectedListener(this.mTabSelectListener);
        final FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        if(hasBodySensorPermission()){
            transaction.replace(R.id.frameLayout,new FragmentListDevices(),"list_device_fragment");
        }else{
            transaction.replace(R.id.frameLayout,new RequiresBodySensorFragment(),"require_body_sensor");
        }
        transaction.commit();
    }

    public boolean hasBodySensorPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

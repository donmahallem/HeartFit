package com.github.donmahallem.heartfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.recycler.adapter.BleDeviceAdapter;
import com.github.donmahallem.heartfit.viewmodel.BleDeviceListFragmentViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.BleClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created on 19.11.2018.
 */
public class FragmentListDevices extends Fragment implements View.OnClickListener {
    private BleClient m;
    private BleDeviceListFragmentViewModel mViewModel;
    private BleScanCallback mCallback = new BleScanCallback() {
        @Override
        public void onDeviceFound(BleDevice bleDevice) {
            Toast.makeText(getActivity(), "Device found " + bleDevice.getName(), Toast.LENGTH_LONG).show();
            mAdapter.addBleDevice(bleDevice);
        }

        @Override
        public void onScanStopped() {
            Toast.makeText(getActivity(), "Scan stopped", Toast.LENGTH_LONG).show();
            mViewModel.setScanInProgress(false);
        }
    };
    private ProgressBar mProgressBarScanning;
    private Disposable mProgressBarScanningDisposable;
    private RecyclerView mRecyclerView;
    private BleDeviceAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_devices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mViewModel = ViewModelProviders.of(getActivity()).get(BleDeviceListFragmentViewModel.class);
        this.mProgressBarScanning = view.findViewById(R.id.progressBar);
        this.mRecyclerView=view.findViewById(R.id.recyclerView);
        this.mAdapter=new BleDeviceAdapter();
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.m = Fitness.getBleClient(this.getActivity(), GoogleSignIn.getLastSignedInAccount(this.getContext()));
        this.mProgressBarScanningDisposable = this.mViewModel.isScanInProgressObservable()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isScanning) throws Exception {
                        mProgressBarScanning.setVisibility(isScanning ? View.VISIBLE : View.GONE);
                    }
                });
    }

    @Override
    public void onPause() {
        this.m.stopBleScan(this.mCallback);
        this.mProgressBarScanningDisposable.dispose();
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scan_device_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_start_scan:
                this.startScan();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void startScan() {
        if (this.mViewModel.isScanInProgress()) {

        } else {
            List<DataType> types = new ArrayList<>();
            types.add(DataType.TYPE_HEART_RATE_BPM);
            this.m.startBleScan(types, 10, this.mCallback);
            this.mViewModel.setScanInProgress(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGrantPermission:
                break;
        }
    }
}

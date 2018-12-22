package com.github.donmahallem.heartfit.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.recycler.adapter.ClaimedDevicesAdapter;
import com.github.donmahallem.heartfit.viewmodel.ClaimedDevicesFragmentViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.BleClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ClaimedDevicesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ClaimedDevicesAdapter mAdapter;
    private Disposable mUpdateDisposable;
    private BleClient mBleClient;
    private SwipeRefreshLayout mRefreshLayout;
    private ClaimedDevicesFragmentViewModel mViewModel;
    private final SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
            mRefreshLayout.setRefreshing(false);
        }
    };
    private Disposable mRefreshStateDisposable;
    private final Consumer<? super Throwable> mRefreshErrorConsumer=new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Snackbar.make(ClaimedDevicesFragment.this.mRefreshLayout,R.string.error_occured_while_refreshing,Snackbar.LENGTH_SHORT).show();
        }
    };
    private final Action mRefreshCompleteAction=new Action() {
        @Override
        public void run() throws Exception {
            Snackbar.make(ClaimedDevicesFragment.this.mRefreshLayout,R.string.refreshed,Snackbar.LENGTH_SHORT).show();
        }
    };
    private Disposable mRefreshDataDisposable;
    private Disposable mDeviceListDisposable;
    private Disposable mClickDisposable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_claimed_devices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mViewModel = ViewModelProviders.of(this).get(ClaimedDevicesFragmentViewModel.class);
        this.mRecyclerView = view.findViewById(R.id.recyclerView);
        this.mLayoutManager = new LinearLayoutManager(view.getContext());
        this.mAdapter = new ClaimedDevicesAdapter();
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), this.mLayoutManager.getOrientation()));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        this.mRefreshLayout.setOnRefreshListener(this.mOnRefreshListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_claimed_devices, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_refresh:
                this.refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void refreshData() {
        this.mBleClient = Fitness.getBleClient(this.getActivity(), GoogleSignIn.getLastSignedInAccount(this.getActivity()));
        this.mRefreshDataDisposable=this.mViewModel.refreshData(this.mBleClient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.mRefreshCompleteAction,this.mRefreshErrorConsumer);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.refreshData();
        this.mRefreshStateDisposable =
                this.mViewModel.getRefreshStateFlowable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ClaimedDevicesFragmentViewModel.RefreshState>() {
                            @Override
                            public void accept(ClaimedDevicesFragmentViewModel.RefreshState refreshState) throws Exception {
                                switch (refreshState) {
                                    case INIT:

                                        break;
                                    case REFRESHED:
                                        mRefreshLayout.setRefreshing(false);
                                        break;
                                    case REFRESHING:
                                        mRefreshLayout.setRefreshing(true);
                                        break;
                                }
                            }
                        });
        this.mDeviceListDisposable=this.mViewModel.getBleDeviceListFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<BleDevice>>() {
                    @Override
                    public void accept(List<BleDevice> bleDevices) throws Exception {
                        mAdapter.setDevices(bleDevices);
                    }
                });
        this.mClickDisposable=this.mAdapter.getPositionClicks()
                .subscribe(new Consumer<BleDevice>() {
                    @Override
                    public void accept(BleDevice bleDevice) throws Exception {
                        Timber.d("CLICKED %s",bleDevice.toString());
                        BleDeviceBottomSheetDialogFragment asdf=new BleDeviceBottomSheetDialogFragment();
                        asdf.show(getChildFragmentManager(),"JK ");
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mRefreshStateDisposable.dispose();
        if(this.mRefreshDataDisposable!=null)
            this.mRefreshDataDisposable.dispose();
        this.mDeviceListDisposable.dispose();
        if(this.mClickDisposable!=null)
            this.mClickDisposable.dispose();
    }

}

package com.github.donmahallem.common.recycler.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.donmahallem.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FragmentList extends Fragment {
    private static final int STATUS_INITIALIZING = 0;
    private static final int STATUS_LOADING = 1;
    private static final int STATUS_LOADED = 2;
    private static final String KEY_CURRENT_STATUS = "current_status";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTxtStatusInfo;
    private Button mBtnRetry;
    private int mCurrentStatus=STATUS_INITIALIZING;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            this.mCurrentStatus=savedInstanceState.getInt(KEY_CURRENT_STATUS,STATUS_INITIALIZING);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mSwipeRefreshLayout=view.findViewById(R.id.refreshLayout);
        this.mRecyclerView=view.findViewById(R.id.recyclerView);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mBtnRetry=view.findViewById(R.id.btnRetry);
        this.mTxtStatusInfo=view.findViewById(R.id.txtStatusInfo);
    }

    public void updateViews(){
        if(this.mSwipeRefreshLayout!=null){
            this.mSwipeRefreshLayout.setRefreshing(this.mCurrentStatus==STATUS_LOADING);
        }
        if(this.mRecyclerView!=null){
            if(this.mRecyclerView.getAdapter()!=null){

            }
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public void setStatus(int status){
        this.mCurrentStatus=status;
        this.updateViews();
    }

    @Override
    public void onResume(){
        super.onResume();
        this.updateViews();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_CURRENT_STATUS,this.mCurrentStatus);
    }
}

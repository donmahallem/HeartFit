package com.github.donmahallem.heartfit.fragments;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Created on 19.11.2018.
 */
public class RequiresBodySensorFragment extends Fragment implements View.OnClickListener {
    private MaterialButton mBtnGrantPermission;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requires_body_sensor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mBtnGrantPermission=view.findViewById(R.id.btnGrantPermission);
        this.mBtnGrantPermission.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGrantPermission:
                this.askForPermission();
                break;
        }
    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.BODY_SENSORS},
                    1234);
    }
}

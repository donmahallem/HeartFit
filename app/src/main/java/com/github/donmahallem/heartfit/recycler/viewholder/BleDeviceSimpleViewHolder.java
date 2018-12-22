package com.github.donmahallem.heartfit.recycler.viewholder;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class BleDeviceSimpleViewHolder extends LayoutViewHolder  {
    private final TextView mTxtSubTitle;
    private final TextView mTxtTitle;
    private final ImageView mIvIcon;
    private BleDevice mSession;
    private BleDevice mBleDevice;

    public BleDeviceSimpleViewHolder(ViewGroup parent) {
        super(parent, R.layout.vh_ble_device);
        this.mIvIcon=this.itemView.findViewById(R.id.ivIcon);
        this.mTxtSubTitle=this.itemView.findViewById(R.id.txtSubtitle);
        this.mTxtTitle=this.itemView.findViewById(R.id.txtTitle);
    }


    public void setSession(BleDevice bleDevice) {
        this.mBleDevice=bleDevice;
        this.mSession = bleDevice;
        this.mTxtTitle.setText(bleDevice.getName());
        String in="";
        for(DataType dataType: bleDevice.getDataTypes()){
            if(in.length()>0)
                in+=in+",";
            in+=dataType.getName();
        }
        this.mTxtSubTitle.setText(in);
    }

    public BleDevice getBleDevice() {
        return this.mBleDevice;
    }

}

package com.github.donmahallem.heartfit.recycler.viewholder;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.pagination.WeightTimestamp;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.annotation.Nullable;

public class WeightTimestampViewHolder extends LayoutViewHolder{
    private final TextView mTxtWeight;
    private final TextView mTxtDateTime;
    private OffsetDateTime mLocalDateTime;

    public WeightTimestampViewHolder(ViewGroup parent) {
        super(parent, R.layout.vh_weight_timestamp);
        this.mTxtDateTime=this.itemView.findViewById(R.id.txtDateTime);
        this.mTxtWeight=this.itemView.findViewById(R.id.txtWeight);
    }

    public void setWeightTimestamp(@Nullable WeightTimestamp weightTimestamp) {
        final Resources resources=this.itemView.getResources();
        if(weightTimestamp!=null){
            this.mLocalDateTime=OffsetDateTime.ofInstant(Instant.ofEpochMilli(weightTimestamp.getTimestamp()),ZoneOffset.systemDefault());
            this.mTxtDateTime.setText(this.mLocalDateTime.format(DateTimeFormatter.ofPattern("dd HH:mm")));
            this.mTxtWeight.setText(resources.getString(R.string.x_kg,weightTimestamp.getWeight()));
        }else{
            this.mLocalDateTime=null;
            this.mTxtDateTime.setText("--");
            this.mTxtWeight.setText("xx kg");
        }
    }

    public LocalDateTime getLocalDateTime() {
        return mLocalDateTime.toLocalDateTime();
    }
}

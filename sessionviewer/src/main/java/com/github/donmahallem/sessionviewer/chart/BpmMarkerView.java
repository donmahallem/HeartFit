package com.github.donmahallem.sessionviewer.chart;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.donmahallem.sessionviewer.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class BpmMarkerView extends MarkerView {
    private final TextView mTxtBpm;
    private MPPointF mMPPointF=new MPPointF();

    public BpmMarkerView(Context context) {
        super(context, R.layout.marker_view_bpm);
        this.mTxtBpm=this.findViewById(R.id.txtBpm);
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        this.mTxtBpm.setText("" + ((int)(e.getY()/1)));
        super.refreshContent(e,highlight);
    }
    @Override
    public MPPointF getOffset() {
        mMPPointF.x=-(this.getWidth()/2f);
        mMPPointF.y=-(this.getHeight()/2f);
        return mMPPointF;
    }
}

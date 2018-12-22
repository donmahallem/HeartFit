package com.github.donmahallem.sessionviewer.chart;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created on 30.11.2018.
 */
public class SlidingAverageLineChartRenderer extends LineChartRenderer {
    private boolean mLeftBarEnabled=false;
    private float mLeftBarValue=0;
    private boolean mRightBarEnabled=false;
    private float mRightBarValue=0;
    private Paint mBarPaint=new Paint();

    public SlidingAverageLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        mBarPaint.setPathEffect(new DashPathEffect(new float[] {Utils.convertDpToPixel(10f),Utils.convertDpToPixel(3f)}, 0));
    }

    public float average(ILineDataSet dataSet, int center, int windowSize) {
        float items = 0;
        float sum = 0;
        for (int i = center - windowSize; i <= center + windowSize; i++) {
            if (i < 0)
                continue;
            if (i >= dataSet.getEntryCount()) {
                continue;
            }
            items += 1;
            sum += dataSet.getEntryForIndex(i).getY();
        }
        if (sum == 0)
            return 0;
        return sum / items;
    }


    @Override
    protected void drawHorizontalBezier(ILineDataSet dataSet) {
        this.mRenderPaint.setAlpha(128);
        super.drawHorizontalBezier(dataSet);
        this.mRenderPaint.setAlpha(255);
        float phaseY = mAnimator.getPhaseY();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mXBounds.set(mChart, dataSet);
        final int windowSize = 4;
        cubicPath.reset();

        if (mXBounds.range >= 1) {

            Entry prev = dataSet.getEntryForIndex(mXBounds.min);
            Entry cur = prev;

            float curY = average(dataSet, mXBounds.min, windowSize);
            float prevY = 0;
            // let the spline start
            cubicPath.moveTo(cur.getX(), curY * phaseY);

            for (int j = mXBounds.min + 1; j <= mXBounds.range + mXBounds.min; j++) {

                prev = cur;
                cur = dataSet.getEntryForIndex(j);

                curY = average(dataSet, j, windowSize);
                prevY = average(dataSet, j - 1, windowSize);
                final float cpx = (prev.getX())
                        + (cur.getX() - prev.getX()) / 2.0f;

                cubicPath.cubicTo(
                        cpx, prevY * phaseY,
                        cpx, curY * phaseY,
                        cur.getX(), curY * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, mXBounds);
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth()*2);
        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    public void setLeftBarEnabled(boolean enabled){
        this.mLeftBarEnabled=enabled;
    }

    public void setLeftBar(float value){
        this.mLeftBarValue=value;
    }
    public void setRightBarEnabled(boolean enabled){
        this.mRightBarEnabled=enabled;
    }

    public void setRightBar(float value){
        this.mRightBarValue=value;
    }

    public void setBarColor(int color){
        this.mBarPaint.setColor(color);
    }

    @Override
    protected void drawCubicBezier(ILineDataSet dataSet) {
        drawBars(dataSet);
        super.drawCubicBezier(dataSet);
        float phaseY = mAnimator.getPhaseY();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mXBounds.set(mChart, dataSet);

        float intensity = dataSet.getCubicIntensity();

        final int windowSize = 4;
        cubicPath.reset();

        if (mXBounds.range >= 1) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            // Take an extra point from the left, and an extra from the right.
            // That's because we need 4 points for a cubic bezier (cubic=4), otherwise we get lines moving and doing weird stuff on the edges of the chart.
            // So in the starting `prev` and `cur`, go -2, -1
            // And in the `lastIndex`, add +1

            final int firstIndex = mXBounds.min + 1;
            final int lastIndex = mXBounds.min + mXBounds.range;

            Entry prevPrev;
            Entry prev = dataSet.getEntryForIndex(Math.max(firstIndex - 2, 0));
            Entry cur = dataSet.getEntryForIndex(Math.max(firstIndex - 1, 0));
            Entry next = cur;
            int nextIndex = -1;

            if (cur == null) return;

            float curY = average(dataSet,Math.max(firstIndex - 1, 0), windowSize);
            float prevY = average(dataSet,Math.max(firstIndex - 2, 0), windowSize);
            float prevPrevY=0;
            float nextY=curY;
            // let the spline start
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);

            for (int j = mXBounds.min + 1; j <= mXBounds.range + mXBounds.min; j++) {

                //
                prevPrev = prev;
                prev = cur;
                cur = nextIndex == j ? next : dataSet.getEntryForIndex(j);
                //

                prevPrevY=prevY;
                prevY=curY;
                curY=average(dataSet,j, windowSize);
                //
                nextIndex = j + 1 < dataSet.getEntryCount() ? j + 1 : j;
                next = dataSet.getEntryForIndex(nextIndex);
                //CUSTOM
                nextY = average(dataSet, nextIndex, windowSize);
                //
                prevDx = (cur.getX() - prevPrev.getX()) * intensity;
                prevDy = (curY - prevPrevY) * intensity;
                curDx = (next.getX() - prev.getX()) * intensity;
                curDy = (nextY - prevY) * intensity;

                cubicPath.cubicTo(prev.getX() + prevDx, (prevY + prevDy) * phaseY,
                        cur.getX() - curDx,
                        (curY - curDy) * phaseY, cur.getX(), curY * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);

            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, mXBounds);
        }

        mRenderPaint.setColor(dataSet.getColor() | 0xFF000000);
        mRenderPaint.setStrokeWidth(10);

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }
    private Path mBarPath=new Path();
    private void drawBars(ILineDataSet dataSet) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mBarPath.reset();
        mBarPath.moveTo(this.mLeftBarValue,0);
        mBarPath.lineTo(this.mLeftBarValue,dataSet.getYMax());
        mBarPath.moveTo(this.mRightBarValue,0);
        mBarPath.lineTo(this.mRightBarValue,dataSet.getYMax());
        trans.pathValueToPixel(mBarPath);


        mBarPaint.setStrokeWidth( Utils.convertDpToPixel(1.5f));
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBitmapCanvas.drawPath(mBarPath, mBarPaint);

    }
}

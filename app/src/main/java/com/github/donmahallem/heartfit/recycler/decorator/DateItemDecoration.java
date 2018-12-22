package com.github.donmahallem.heartfit.recycler.decorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.pagination.WeightTimestamp;
import com.github.donmahallem.heartfit.recycler.adapter.WeightPagedListAdapter;
import com.github.donmahallem.heartfit.recycler.viewholder.WeightTimestampViewHolder;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class DateItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSubheaderSize;
    private final Paint mHeaderBackground;
    private Rect mBounds = new Rect();
    private boolean mSticky = false;
    private TextView mHeaderView;

    public DateItemDecoration(Context context) {
        this.mSubheaderSize = context.getResources().getDimensionPixelSize(R.dimen.list_caption_height);
        this.mHeaderBackground=new Paint();
        this.mHeaderBackground.setARGB(255,255,255,255);
        TypedValue a = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            int color = a.data;
            this.mHeaderBackground.setColor(color);
        }
    }

    public TextView getHeaderView(@NonNull RecyclerView parent) {
        if (this.mHeaderView == null) {
            final View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_section_header, parent, false);
            this.mHeaderView = (TextView) header;

            fixLayoutSize(parent, this.mHeaderView);
        }
        return this.mHeaderView;
    }

    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        drawHorizontal(c, parent);
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        TextView currentHeader = getHeaderView(parent);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            canvas.save();
            final View child = parent.getChildAt(i);
            if (!hasHeader(parent, child)) {
                continue;
            }
            final WeightTimestampViewHolder vh = (WeightTimestampViewHolder) parent.getChildViewHolder(child);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            currentHeader.setText(vh.getLocalDateTime().format(DateTimeFormatter.ofPattern("MMMM YYYY")));
            fixLayoutSize(parent, currentHeader);
            //Timber.d("Item %s - top: %s  -  %s - %s", i, child.getTop(), child.getY(), currentHeader.getHeight());
            final int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = 0;//right - mDivider.getIntrinsicWidth();

            //currentHeader.setTop(child.getTop());
            // currentHeader.setRight(right);
            //currentHeader.setLeft(left);
            final int top = child.getBottom();
            if (this.mSticky) {
                canvas.translate(0, Math.max(0, child.getTop() - currentHeader.getHeight()));
            } else {
                canvas.translate(0, mBounds.top);
                //canvas.translate(0, child.getY() - currentHeader.getHeight());
            }
            //currentHeader.setTop(mBounds.top);
            canvas.drawRect(0, 0, currentHeader.getWidth(), currentHeader.getHeight(), this.mHeaderBackground);
            //canvas.translate(0,child.getTop()+child.getPaddingTop());
            currentHeader.draw(canvas);
            canvas.restore();
        }
    }

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //Timber.d("offsets");
        final int vhPosition = parent.getChildAdapterPosition(view);
        if (this.hasHeader(parent, view)) {
            outRect.set(0, this.mSubheaderSize, 0, 0);
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }


    public boolean hasHeader(@NonNull RecyclerView parent, View currentView) {
        WeightPagedListAdapter adapter= (WeightPagedListAdapter) parent.getAdapter();
        final int childAdapterPosition=parent.getChildAdapterPosition(currentView);
        if(childAdapterPosition==0){
            return true;
        }
        WeightTimestamp currentItem=adapter.getItem(childAdapterPosition);
        WeightTimestamp previousItem=adapter.getItem(childAdapterPosition-1);
        if (previousItem == null && currentItem == null) {
            return false;
        } else if (previousItem == null) {
            return false;
        } else if (currentItem == null) {
            return false;
        } else {
            OffsetDateTime current=OffsetDateTime.ofInstant(Instant.ofEpochMilli(currentItem.getTimestamp()),ZoneOffset.systemDefault());
            OffsetDateTime previous=OffsetDateTime.ofInstant(Instant.ofEpochMilli(previousItem.getTimestamp()),ZoneOffset.systemDefault());
            return current.getMonth() != previous.getMonth()
                    || current.getYear() != previous.getYear();
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {

        final View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }

        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }

        TextView currentHeader = getHeaderView(parent);
        fixLayoutSize(parent, currentHeader);
        int contactPoint = currentHeader.getBottom();
        WeightTimestampViewHolder vh= (WeightTimestampViewHolder) parent.getChildViewHolder(topChild);
        currentHeader.setText(vh.getLocalDateTime().format(DateTimeFormatter.ofPattern("MMMM YYYY")));
        fixLayoutSize(parent, currentHeader);
        parent.getLayoutManager().getDecoratedBoundsWithMargins(topChild, mBounds);

        final View secondChild=parent.getChildAt(1);
        if(secondChild==null){
            c.drawRect(0,0,c.getWidth(),this.mSubheaderSize,this.mHeaderBackground);
            drawHeader(c, currentHeader,0);
            return;
        }
        final int secondChildPosition=parent.getChildAdapterPosition(secondChild);
        if(topChildPosition==RecyclerView.NO_POSITION){
            c.drawRect(0,0,c.getWidth(),this.mSubheaderSize,this.mHeaderBackground);
            drawHeader(c, currentHeader,0);
            return;
        }
        if(hasHeader(parent,secondChild)){
            final int offset=Math.min(0,topChild.getBottom()-topChild.getMeasuredHeight());
            c.drawRect(0,0,c.getWidth(),Math.max(0,this.mSubheaderSize+offset),this.mHeaderBackground);
            drawHeader(c, currentHeader,offset);
            //Timber.d("Offset: %s",offset);
        }else{
            c.drawRect(0,0,c.getWidth(),this.mSubheaderSize,this.mHeaderBackground);
            drawHeader(c, currentHeader,0);
        }

    }

    private void fixLayoutSize(ViewGroup parent, View view) {

        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private void drawHeader(Canvas c, View header, int topChild) {
        c.save();
        c.translate(0, topChild);
        header.draw(c);
        c.restore();
    }


    public View getLeadingItem(RecyclerView parent, View view) {
        final int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (childAdapterPosition == 0) {
            return null;
        }
        return parent.getChildAt(childAdapterPosition - 1);
    }
}

package com.github.donmahallem.heartfit.recycler.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;

import androidx.annotation.StringRes;

public class BottomSheetListItemViewHolder extends LayoutViewHolder {
    private final TextView mTxtTitle;
    private boolean mHasIcon = false;

    public BottomSheetListItemViewHolder(ViewGroup parent) {
        super(parent, R.layout.vh_bottom_sheet_list_item);
        this.mTxtTitle = itemView.findViewById(R.id.txtTitle);
    }

    public void setText(@StringRes int stringId) {
        this.mTxtTitle.setText(stringId);
    }

    public void setHasIcon(boolean hasIcon) {
        this.mHasIcon = hasIcon;
    }
}

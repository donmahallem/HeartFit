package com.github.donmahallem.common.recycler.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class SingleLineTextViewHolder extends LayoutViewHolder {
    private final TextView mTxtView;

    public SingleLineTextViewHolder(@NonNull ViewGroup parent) {
        super(parent, R.layout.vh_single_line_text);
        this.mTxtView= (TextView) this.itemView;
    }

    public void setText(@NonNull String text){
        this.mTxtView.setText(text);
    }

    public void setText(@StringRes int stringRes){
        this.mTxtView.setText(stringRes);
    }
}

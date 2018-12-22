package com.github.donmahallem.heartfit.module.nutritionviewer.recycler.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.common.recycler.viewholder.LayoutViewHolder;
import com.github.donmahallem.heartfit.module.nutritionviewer.R;
import com.github.donmahallem.heartfit.module.nutritionviewer.model.NutritionHistoryBucket;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import androidx.annotation.NonNull;

public final class NutritionHistoryViewHolder extends LayoutViewHolder {
    private final TextView mTxtDate;
    private final TextView mTxtCaloriesConsumed;
    private final TextView mTxtCaloriesExpanded;
    private NutritionHistoryBucket mNutritionHistory;

    public NutritionHistoryViewHolder(@NonNull ViewGroup parent) {
        super(parent, R.layout.vh_nutrition_history);
        this.mTxtDate=this.itemView.findViewById(R.id.txtDate);
        this.mTxtCaloriesConsumed=this.itemView.findViewById(R.id.txtCaloriesConsumed);
        this.mTxtCaloriesExpanded=this.itemView.findViewById(R.id.txtCaloriesExpanded);
    }

    public void setNutritionHistory(NutritionHistoryBucket nutritionHistory) {
        mNutritionHistory = nutritionHistory;
        this.mTxtDate.setText(nutritionHistory.getStartTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        this.mTxtCaloriesConsumed.setText(this.itemView.getResources().getString(R.string.x_kcal,nutritionHistory.getCalories()));
        this.mTxtCaloriesExpanded.setText(this.itemView.getResources().getString(R.string.x_kcal,nutritionHistory.getCaloriesExpended()));
    }

    public NutritionHistoryBucket getNutritionHistory() {
        return mNutritionHistory;
    }
}

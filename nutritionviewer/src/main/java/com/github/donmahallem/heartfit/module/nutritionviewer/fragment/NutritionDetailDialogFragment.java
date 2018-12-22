package com.github.donmahallem.heartfit.module.nutritionviewer.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.donmahallem.heartfit.module.nutritionviewer.R;
import com.github.donmahallem.heartfit.module.nutritionviewer.model.NutritionHistoryBucket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created on 12.12.2018.
 */
public class NutritionDetailDialogFragment extends DialogFragment {
    private static final String KEY_NUTRITION_HISTORY_BUCKET = "key_nutrition_history";
    private TextView mTxtCalcium;
    private TextView mTxtCalories;
    private TextView mTxtVitaminC;
    private TextView mTxtVitaminA;
    private TextView mTxtCarbsTotal,mTxtCholesterol,mTxtDietaryFiber
            ,mTxtFatMonosaturated,mTxtFatPolySaturated,mTxtFatSaturated
            ,mTxtFatUnsaturated,mTxtFatTotal,mTxtFatTrans,mTxtIron,mTxtPotassium
            ,mTxtProtein,mTxtSodium,mTxtSugar;
    private NutritionHistoryBucket mNutritionHistory;

    public static NutritionDetailDialogFragment createInstance(NutritionHistoryBucket nutritionHistoryBucket) {
        final Bundle args=new Bundle();
        args.putParcelable(KEY_NUTRITION_HISTORY_BUCKET,nutritionHistoryBucket);
        final NutritionDetailDialogFragment fragment=new NutritionDetailDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getArguments()!=null){
            this.mNutritionHistory=this.getArguments().getParcelable(KEY_NUTRITION_HISTORY_BUCKET);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrition_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mTxtCalories=view.findViewById(R.id.txtCaloriesConsumedValue);
        this.mTxtCalcium=view.findViewById(R.id.txtCalciumValue);
        this.mTxtVitaminA=view.findViewById(R.id.txtVitaminAValue);
        this.mTxtVitaminC=view.findViewById(R.id.txtVitaminCValue);
        this.mTxtCarbsTotal=view.findViewById(R.id.txtCarbsTotalValue);
        this.mTxtCholesterol=view.findViewById(R.id.txtCholesterolValue);
        this.mTxtDietaryFiber=view.findViewById(R.id.txtDietaryFiberValue);
        this.mTxtFatMonosaturated=view.findViewById(R.id.txtFatMonosaturatedValue);
        this.mTxtFatPolySaturated=view.findViewById(R.id.txtFatPolysaturatedValue);
        this.mTxtFatSaturated=view.findViewById(R.id.txtFatSaturatedValue);
        this.mTxtFatUnsaturated=view.findViewById(R.id.txtFatUnsaturatedValue);
        this.mTxtFatTotal=view.findViewById(R.id.txtFatTotalValue);
        this.mTxtFatTrans=view.findViewById(R.id.txtFatTransValue);
        this.mTxtIron=view.findViewById(R.id.txtIronValue);
        this.mTxtPotassium=view.findViewById(R.id.txtPotassiumValue);
        this.mTxtProtein=view.findViewById(R.id.txtProteinValue);
        this.mTxtSodium=view.findViewById(R.id.txtSodiumValue);
        this.mTxtSugar=view.findViewById(R.id.txtSugarValue);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.updateViews();
    }

    private void updateViews() {
        final Resources resources=this.getResources();
        this.mTxtCalories.setText(resources.getString(R.string.x_kcal,this.mNutritionHistory.getCalories()));
        this.mTxtCalcium.setText(resources.getString(R.string.x_mg,this.mNutritionHistory.getCalcium()));
        this.mTxtVitaminA.setText(resources.getString(R.string.x_mg,this.mNutritionHistory.getVitaminA()));
        this.mTxtVitaminC.setText(resources.getString(R.string.x_mg,this.mNutritionHistory.getVitaminC()));
        this.mTxtCarbsTotal.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getCarbsTotal()));
        this.mTxtCholesterol.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getCholesterol()));
        this.mTxtDietaryFiber.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getDietaryFiber()));
        this.mTxtFatMonosaturated.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getFatMonosaturated()));
        this.mTxtFatPolySaturated.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getFatPolysaturated()));
        this.mTxtFatSaturated.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getFatSaturated()));
        this.mTxtFatUnsaturated.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getFatUnsaturated()));
        this.mTxtFatTotal.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getFatTotal()));
        this.mTxtFatTrans.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getFatTrans()));
        this.mTxtIron.setText(resources.getString(R.string.x_mg,this.mNutritionHistory.getIron()));
        this.mTxtPotassium.setText(resources.getString(R.string.x_mg,this.mNutritionHistory.getPotassium()));
        this.mTxtProtein.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getProtein()));
        this.mTxtSodium.setText(resources.getString(R.string.x_mg,this.mNutritionHistory.getSodium()));
        this.mTxtSugar.setText(resources.getString(R.string.x_gramm,this.mNutritionHistory.getSugar()));
    }
}

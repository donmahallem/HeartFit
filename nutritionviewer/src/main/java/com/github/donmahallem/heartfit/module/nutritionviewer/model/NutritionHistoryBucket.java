package com.github.donmahallem.heartfit.module.nutritionviewer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class NutritionHistoryBucket implements Parcelable {

    private final OffsetDateTime mStartTime;
    private final OffsetDateTime mEndTime;
    private final float mCalcium;
    private final  float mCalories;
    private final   float mCarbsTotal;
    private final  float mCholesterol;
    private final  float mDietaryFiber;
    private final  float mFatMonosaturated;
    private final  float mFatPolysaturated;
    private final  float mFatSaturated;
    private final  float mFatTotal;
    private final  float mFatTrans;
    private  final float mIron;
    private final  float mPotassium;
    private  final float mProtein;
    private  final float mSodium;
    private  final float mSugar;
    private  final float mVitaminC;
    private  final float mVitaminA;
    private  final float mFatUnsaturated;
    private final  float mCaloriesExpended;

    private NutritionHistoryBucket(Builder builder) {
        this.mStartTime=OffsetDateTime.ofInstant(Instant.ofEpochSecond(builder.mStartTime),ZoneId.systemDefault());
        this.mEndTime=OffsetDateTime.ofInstant(Instant.ofEpochSecond(builder.mEndTime),ZoneId.systemDefault());
        this.mCalcium=builder.mCalcium;
        this.mCalories=builder.mCalories;
        this.mCarbsTotal=builder.mCarbsTotal;
        this.mCholesterol=builder.mCholesterol;
        this.mDietaryFiber=builder.mDietaryFiber;
        this.mFatMonosaturated=builder.mFatMonosaturated;
        this.mFatPolysaturated=builder.mFatPolysaturated;
        this.mFatSaturated=builder.mFatSaturated;
        this.mFatTotal=builder.mFatTotal;
        this.mFatTrans=builder.mFatTrans;
        this.mIron=builder.mIron;
        this.mPotassium=builder.mPotassium;
        this.mProtein=builder.mProtein;
        this.mSodium=builder.mSodium;
        this.mSugar=builder.mSugar;
        this.mVitaminA=builder.mVitaminA;
        this.mVitaminC=builder.mVitaminC;
        this.mFatUnsaturated=builder.mFatUnsaturated;
        this.mCaloriesExpended=builder.mCaloriesExpended;
    }

    public OffsetDateTime getStartTime() {
        return mStartTime;
    }

    public OffsetDateTime getEndTime() {
        return mEndTime;
    }

    public float getCalcium() {
        return mCalcium;
    }

    public float getCalories() {
        return mCalories;
    }

    public float getCarbsTotal() {
        return mCarbsTotal;
    }

    public float getCholesterol() {
        return mCholesterol;
    }

    public float getDietaryFiber() {
        return mDietaryFiber;
    }

    public float getFatMonosaturated() {
        return mFatMonosaturated;
    }

    public float getFatPolysaturated() {
        return mFatPolysaturated;
    }

    public float getFatSaturated() {
        return mFatSaturated;
    }

    public float getFatTotal() {
        return mFatTotal;
    }

    public float getFatTrans() {
        return mFatTrans;
    }

    public float getIron() {
        return mIron;
    }

    public float getPotassium() {
        return mPotassium;
    }

    public float getProtein() {
        return mProtein;
    }

    public float getSodium() {
        return mSodium;
    }

    public float getSugar() {
        return mSugar;
    }

    public float getVitaminC() {
        return mVitaminC;
    }

    public float getVitaminA() {
        return mVitaminA;
    }

    public float getFatUnsaturated() {
        return mFatUnsaturated;
    }

    public float getCaloriesExpended() {
        return mCaloriesExpended;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mStartTime.toEpochSecond());
        parcel.writeLong(this.mEndTime.toEpochSecond());
        parcel.writeFloat(this.mCalcium);
        parcel.writeFloat(this.mCalories);
        parcel.writeFloat(this.mCarbsTotal);
        parcel.writeFloat(this.mCholesterol);
        parcel.writeFloat(this.mDietaryFiber);
        parcel.writeFloat(this.mFatMonosaturated);
        parcel.writeFloat(this.mFatPolysaturated);
        parcel.writeFloat(this.mFatSaturated);
        parcel.writeFloat(this.mFatTotal);
        parcel.writeFloat(this.mFatTrans);
        parcel.writeFloat(this.mIron);
        parcel.writeFloat(this.mPotassium);
        parcel.writeFloat(this.mProtein);
        parcel.writeFloat(this.mSodium);
        parcel.writeFloat(this.mSugar);
        parcel.writeFloat(this.mVitaminC);
        parcel.writeFloat(this.mVitaminA);
        parcel.writeFloat(this.mFatUnsaturated);
        parcel.writeFloat(this.mCaloriesExpended);
    }
    public static final Parcelable.Creator<NutritionHistoryBucket> CREATOR
            = new Parcelable.Creator<NutritionHistoryBucket>() {
        public NutritionHistoryBucket createFromParcel(Parcel in) {
            final Builder builder=new Builder();
            builder.setStartTime(in.readLong());
            builder.setEndTime(in.readLong());
            builder.setCalcium(in.readFloat());
            builder.setCalories(in.readFloat());
            builder.setCarbsTotal(in.readFloat());
            builder.setCholesterol(in.readFloat());
            builder.setDietaryFiber(in.readFloat());
            builder.setFatMonosaturated(in.readFloat());
            builder.setFatPolysaturated(in.readFloat());
            builder.setFatSaturated(in.readFloat());
            builder.setFatTotal(in.readFloat());
            builder.setFatTrans(in.readFloat());
            builder.setIron(in.readFloat());
            builder.setPotassium(in.readFloat());
            builder.setProtein(in.readFloat());
            builder.setSodium(in.readFloat());
            builder.setSugar(in.readFloat());
            builder.setVitaminC(in.readFloat());
            builder.setVitaminA(in.readFloat());
            builder.setFatUnsaturated(in.readFloat());
            builder.setCaloriesExpended(in.readFloat());
            return builder.build();
        }

        public NutritionHistoryBucket[] newArray(int size) {
            return new NutritionHistoryBucket[size];
        }
    };

    public static class Builder {

        private long mStartTime;
        private long mEndTime;
        private float mCalcium;
        private float mCalories;
        private float mCarbsTotal;
        private float mCholesterol;
        private float mDietaryFiber;
        private float mFatMonosaturated;
        private float mFatPolysaturated;
        private float mFatSaturated;
        private float mFatTotal;
        private float mFatTrans;
        private float mIron;
        private float mPotassium;
        private float mProtein;
        private float mSodium;
        private float mSugar;
        private float mVitaminC;
        private float mVitaminA;
        private float mFatUnsaturated;
        private float mCaloriesExpended;

        private float safeUnbox(DataPoint dataPoint, Field field, String value) {
            if (dataPoint.getValue(field) == null)
                return 0f;
            final Float val = dataPoint.getValue(field).getKeyValue(value);
            if (val == null)
                return 0f;
            return val;
        }

        public Builder setData(Bucket bucket){
            for(DataSet dataSet:bucket.getDataSets()){
                for(DataPoint dataPoint:dataSet.getDataPoints()){
                    this.setData(dataPoint);
                }
            }
            this.mStartTime=bucket.getStartTime(TimeUnit.SECONDS);
            this.mEndTime=bucket.getEndTime(TimeUnit.SECONDS);
            return this;
        }

        public long getStartTime() {
            return mStartTime;
        }

        public Builder setStartTime(long startTime) {
            mStartTime = startTime;
            return this;
        }

        public long getEndTime() {
            return mEndTime;
        }

        public Builder setEndTime(long endTime) {
            mEndTime = endTime;
            return this;
        }

        public float getCalcium() {
            return mCalcium;
        }

        public Builder setCalcium(float calcium) {
            mCalcium = calcium;
            return this;
        }

        public float getCalories() {
            return mCalories;
        }

        public Builder setCalories(float calories) {
            mCalories = calories;
            return this;
        }

        public float getCarbsTotal() {
            return mCarbsTotal;
        }

        public Builder setCarbsTotal(float carbsTotal) {
            mCarbsTotal = carbsTotal;
            return this;
        }

        public float getCholesterol() {
            return mCholesterol;
        }

        public Builder setCholesterol(float cholesterol) {
            mCholesterol = cholesterol;
            return this;
        }

        public float getDietaryFiber() {
            return mDietaryFiber;
        }

        public Builder setDietaryFiber(float dietaryFiber) {
            mDietaryFiber = dietaryFiber;
            return this;
        }

        public float getFatMonosaturated() {
            return mFatMonosaturated;
        }

        public Builder setFatMonosaturated(float fatMonosaturated) {
            mFatMonosaturated = fatMonosaturated;
            return this;
        }

        public float getFatPolysaturated() {
            return mFatPolysaturated;
        }

        public Builder setFatPolysaturated(float fatPolysaturated) {
            mFatPolysaturated = fatPolysaturated;
            return this;
        }

        public float getFatSaturated() {
            return mFatSaturated;
        }

        public Builder setFatSaturated(float fatSaturated) {
            mFatSaturated = fatSaturated;
            return this;
        }

        public float getFatTotal() {
            return mFatTotal;
        }

        public Builder setFatTotal(float fatTotal) {
            mFatTotal = fatTotal;
            return this;
        }

        public float getFatTrans() {
            return mFatTrans;
        }

        public Builder setFatTrans(float fatTrans) {
            mFatTrans = fatTrans;
            return this;
        }

        public float getIron() {
            return mIron;
        }

        public Builder setIron(float iron) {
            mIron = iron;
            return this;
        }

        public float getPotassium() {
            return mPotassium;
        }

        public Builder setPotassium(float potassium) {
            mPotassium = potassium;
            return this;
        }

        public float getProtein() {
            return mProtein;
        }

        public Builder setProtein(float protein) {
            mProtein = protein;
            return this;
        }

        public float getSodium() {
            return mSodium;
        }

        public Builder setSodium(float sodium) {
            mSodium = sodium;
            return this;
        }

        public float getSugar() {
            return mSugar;
        }

        public Builder setSugar(float sugar) {
            mSugar = sugar;
            return this;
        }

        public float getVitaminC() {
            return mVitaminC;
        }

        public Builder setVitaminC(float vitaminC) {
            mVitaminC = vitaminC;
            return this;
        }

        public float getVitaminA() {
            return mVitaminA;
        }

        public Builder setVitaminA(float vitaminA) {
            mVitaminA = vitaminA;
            return this;
        }

        public float getFatUnsaturated() {
            return mFatUnsaturated;
        }

        public Builder setFatUnsaturated(float fatUnsaturated) {
            mFatUnsaturated = fatUnsaturated;
            return this;
        }

        public float getCaloriesExpended() {
            return mCaloriesExpended;
        }

        public Builder setCaloriesExpended(float caloriesExpended) {
            mCaloriesExpended = caloriesExpended;
            return this;
        }

        public Builder setData(DataPoint data) {
            if (data.getDataType().equals(DataType.AGGREGATE_NUTRITION_SUMMARY)) {
                this.mCalcium= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_CALCIUM);
                this.mCalories= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_CALORIES);
                this.mCarbsTotal= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_TOTAL_CARBS);
                this.mCholesterol= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_CHOLESTEROL);
                this.mDietaryFiber= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_DIETARY_FIBER);
                this.mFatMonosaturated= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_MONOUNSATURATED_FAT);
                this.mFatPolysaturated= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_POLYUNSATURATED_FAT);
                this.mFatUnsaturated= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_UNSATURATED_FAT);
                this.mFatSaturated= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_SATURATED_FAT);
                this.mFatTotal= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_TOTAL_FAT);
                this.mFatTrans= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_TRANS_FAT);
                this.mIron= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_IRON);
                this.mPotassium= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_POTASSIUM);
                this.mProtein= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_PROTEIN);
                this.mSodium= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_SODIUM);
                this.mSugar= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_SUGAR);
                this.mVitaminC= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_VITAMIN_C);
                this.mVitaminA= safeUnbox(data, Field.FIELD_NUTRIENTS, Field.NUTRIENT_VITAMIN_A);
            }
            if (data.getDataType().equals(DataType.TYPE_CALORIES_EXPENDED)) {
                this.mCaloriesExpended = data.getValue(Field.FIELD_CALORIES).asFloat();
            }
            return this;
        }

        public NutritionHistoryBucket build() {
            return new NutritionHistoryBucket(this);
        }
    }
}

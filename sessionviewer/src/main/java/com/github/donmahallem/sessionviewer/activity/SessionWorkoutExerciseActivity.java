package com.github.donmahallem.sessionviewer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.donmahallem.common.recycler.activity.GoogleSignInActivity;
import com.github.donmahallem.sessionviewer.R;
import com.github.donmahallem.sessionviewer.fragment.WorkoutExerciseListFragment;
import com.github.donmahallem.sessionviewer.fragment.WorkoutExerciseSummaryFragment;
import com.github.donmahallem.sessionviewer.viewmodel.ViewWorkoutExerciseViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Session;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.Disposable;

/**
 * Created on 18.11.2018.
 */
public class SessionWorkoutExerciseActivity extends GoogleSignInActivity {

    private ViewWorkoutExerciseViewModel mViewModel;
    private ViewPager mViewPager;


    public SessionWorkoutExerciseActivity(){
        super(Fitness.SCOPE_BODY_READ_WRITE);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_exercise);
        this.mViewModel = ViewModelProviders.of(this).get(ViewWorkoutExerciseViewModel.class);
        this.mViewModel.setSession(Session.extract(this.getIntent()));
        this.mViewPager=this.findViewById(R.id.viewPager);
        this.mViewPager
                .setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                    @Override
                    public Fragment getItem(int position) {
                        if(position==0){
                            return new WorkoutExerciseSummaryFragment();
                        }else{
                            return new WorkoutExerciseListFragment();
                        }
                    }

                    @Override
                    public CharSequence getPageTitle(int position){
                        if(position==0){
                            return "Summary";
                        }else{
                            return "List";
                        }
                    }

                    @Override
                    public int getCount() {
                        return 2;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}

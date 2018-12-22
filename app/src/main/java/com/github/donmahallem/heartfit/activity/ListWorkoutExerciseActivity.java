package com.github.donmahallem.heartfit.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.fragments.InsertWorkoutExerciseFragment;
import com.github.donmahallem.heartfit.fragments.ListWorkoutExerciseFragment;
import com.github.donmahallem.heartfit.viewmodel.InsertActivityViewModel;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class ListWorkoutExerciseActivity extends AppCompatActivity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_activity);
        if(getSupportFragmentManager().findFragmentById(R.id.frameLayout)==null)
            this.showListFragment();
    }
    private void showListFragment() {
        final FragmentTransaction fragmentTransaction=this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,new ListWorkoutExerciseFragment(),"list_workout_exercise");
        fragmentTransaction.commit();
    }

}

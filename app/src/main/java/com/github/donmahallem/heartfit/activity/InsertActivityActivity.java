package com.github.donmahallem.heartfit.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.fragments.InsertWorkoutExerciseFragment;
import com.github.donmahallem.heartfit.fragments.ListWorkoutExerciseFragment;
import com.github.donmahallem.heartfit.viewmodel.InsertActivityViewModel;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class InsertActivityActivity extends AppCompatActivity{

    public static final String KEY_MODE_EDIT = "key_mode_edit";
    public static final String KEY_WORKOUT_EXERCISE_ID = "key_workout_exercise_id";
    private InsertActivityViewModel mViewModel;
    private Disposable mSubmitDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_activity);
        this.mViewModel=ViewModelProviders.of(this).get(InsertActivityViewModel.class);
        this.mViewModel.onRestoreInstanceState(savedInstanceState);
        showFragment();
    }

    private void showFragment() {
        final Fragment currentFragment=getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        boolean editMode=false;
        @Nullable
        long editId=-1;
        if(this.getIntent()!=null&&this.getIntent().getExtras()!=null) {
            editMode = this.getIntent().getExtras().getBoolean(KEY_MODE_EDIT, false);
            editId=this.getIntent().getExtras().getLong(KEY_WORKOUT_EXERCISE_ID,-1);
        }
        if(currentFragment==null){
            if(editMode){
                final Fragment fragment=InsertWorkoutExerciseFragment.createEditInstance(editId);
                final FragmentTransaction fragmentTransaction=this.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,fragment,"edit_workout_exercise");
                fragmentTransaction.commit();
            }else{
                final FragmentTransaction fragmentTransaction=this.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,new InsertWorkoutExerciseFragment(),"insert_workout_exercise");
                fragmentTransaction.commit();
            }
            return;
        }
        final InsertWorkoutExerciseFragment currentWorkoutFragment= (InsertWorkoutExerciseFragment) currentFragment;
        if(editMode&&currentWorkoutFragment.isEditModeEnabled()){
            final Fragment fragment=InsertWorkoutExerciseFragment.createEditInstance(editId);
            final FragmentTransaction fragmentTransaction=this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout,fragment,"edit_workout_exercise");
            fragmentTransaction.commit();
        }else{
            if(!currentWorkoutFragment.isEditModeEnabled()){
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        this.mViewModel.onSaveInstanceState(savedInstance);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(this.mSubmitDisposable!=null)
            this.mSubmitDisposable.dispose();
    }

}

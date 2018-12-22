package com.github.donmahallem.sessionviewer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.sessionviewer.R;
import com.github.donmahallem.sessionviewer.model.WorkoutExercise;
import com.github.donmahallem.sessionviewer.recycler.adapter.WorkoutExerciseAdapter;
import com.github.donmahallem.sessionviewer.viewmodel.ViewWorkoutExerciseViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class WorkoutExerciseListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ViewWorkoutExerciseViewModel mViewModel;
    private Disposable mLoadDisposable;
    private WorkoutExerciseAdapter mAdapter;
    private Disposable mWorkoutClickDisposable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.weight_history_graph, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mViewModel = ViewModelProviders.of(getActivity()).get(ViewWorkoutExerciseViewModel.class);
        this.mSwipeRefreshLayout=view.findViewById(R.id.refreshLayout);
        this.mRecyclerView=view.findViewById(R.id.recyclerView);
        this.mAdapter=new WorkoutExerciseAdapter();
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRecyclerView.setAdapter(this.mAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.mLoadDisposable=this.mViewModel.getWorkoutExercises(Fitness.getHistoryClient(this.getContext(),GoogleSignIn.getLastSignedInAccount(this.getContext())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<WorkoutExercise>>() {
                    @Override
                    public void accept(List<WorkoutExercise> workoutExercises) throws Exception {
                        mAdapter.setWorkoutExercises(workoutExercises);
                        Timber.d("DAAA %s", workoutExercises.size());
                        Snackbar.make(getView(),"Success",Snackbar.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Snackbar.make(getView(),"Error",Snackbar.LENGTH_SHORT).show();
                        Timber.e(throwable);
                    }
                });
        this.mWorkoutClickDisposable=this.mAdapter
                .getOnWorkoutExerciseClickFlowable()
                .subscribe(new Consumer<WorkoutExercise>() {
                    @Override
                    public void accept(WorkoutExercise workoutExercise) throws Exception {
                        Timber.d("Clicked %s",workoutExercise.toString());
                        showWorkoutExerciseDetail(workoutExercise);
                    }
                });
    }

    private void showWorkoutExerciseDetail(WorkoutExercise workoutExercise) {
        final WorkoutExerciseDetailDialogFragment fragment=WorkoutExerciseDetailDialogFragment.newInstance(workoutExercise);
        fragment.show(getChildFragmentManager(),"workout_exercise_detail");
    }

    @Override
    public void onPause(){
        super.onPause();
        if(this.mLoadDisposable!=null)
            this.mLoadDisposable.dispose();
        this.mWorkoutClickDisposable.dispose();
    }
}

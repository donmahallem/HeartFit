package com.github.donmahallem.heartfit.fragments;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.activity.InsertActivityActivity;
import com.github.donmahallem.heartfit.db.CacheDatabase;
import com.github.donmahallem.heartfit.db.WorkoutExercise;
import com.github.donmahallem.heartfit.recycler.adapter.WorkoutExerciseOfflineAdapter;
import com.github.donmahallem.heartfit.recycler.viewholder.WorkoutExerciseViewHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;

import org.threeten.bp.temporal.ChronoUnit;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ListWorkoutExerciseFragment extends Fragment {
    private final Consumer<WorkoutExercise> mWorkoutExerciseClickConsumer = new Consumer<WorkoutExercise>() {
        @Override
        public void accept(WorkoutExercise workoutExercise) throws Exception {
            editWorkoutExercise(workoutExercise);
            Timber.d("Workout selected %s", workoutExercise.getId());
        }
    };
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private WorkoutExerciseOfflineAdapter mAdapter;
    private Disposable mUpdateDisposable;
    private Disposable mWorkoutExerciseClickDisposable;

    private void deleteWorkoutExercise(final WorkoutExercise workoutExercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                CacheDatabase.getDatabase(getContext())
                        .workoutDao()
                        .deleteWorkouts(workoutExercise)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Snackbar.make(getView(), "deleted", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.questionaire_delete_workout);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editWorkoutExercise(WorkoutExercise workoutExercise) {
        final Intent intent = new Intent(this.getContext(), InsertActivityActivity.class);
        intent.putExtra(InsertActivityActivity.KEY_MODE_EDIT, true);
        intent.putExtra(InsertActivityActivity.KEY_WORKOUT_EXERCISE_ID, workoutExercise.getId());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mAdapter != null && this.mAdapter.getSelectionTracker() != null)
            this.mAdapter.getSelectionTracker().onSaveInstanceState(outState);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weight_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRecyclerView = view.findViewById(R.id.recyclerView);
        this.mLayoutManager = new LinearLayoutManager(view.getContext());
        this.mAdapter = new WorkoutExerciseOfflineAdapter();
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), this.mLayoutManager.getOrientation()));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final SelectionTracker selectionTracker = new SelectionTracker.Builder<>("sheet_selection",
                this.mRecyclerView,
                new StableIdKeyProvider(this.mRecyclerView),
                new SheetDetailsLookup(this.mRecyclerView),
                StorageStrategy.createLongStorage())
                //.withOnContextClickListener(this)
                .withOnItemActivatedListener(new OnItemActivatedListener<Long>() {
                    @Override
                    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e) {
                        Timber.d("item activate: %s", item.toString());
                        if (item.hasSelectionKey()) {
                            editWorkoutExercise(item.getSelectionKey());
                            return true;
                        }
                        return false;
                    }
                })
                .build();
        if (savedInstanceState != null)
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        this.mAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            ActionMode actionMode = null;

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                Timber.d("On selection Changed");
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = appCompatActivity.startSupportActionMode(new ActionModeCtrl(getContext(),
                            selectionTracker));
                    actionMode.setTitle(R.string.selected);
                    actionMode.setSubtitle(""+selectionTracker.getSelection().size());
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else if(actionMode!=null){
                    actionMode.setTitle(R.string.selected);
                    actionMode.setSubtitle(""+selectionTracker.getSelection().size());
                }
            }
        });

    }

    private void editWorkoutExercise(long selectionKey) {

        final Intent intent = new Intent(this.getContext(), InsertActivityActivity.class);
        intent.putExtra(InsertActivityActivity.KEY_MODE_EDIT, true);
        intent.putExtra(InsertActivityActivity.KEY_WORKOUT_EXERCISE_ID, selectionKey);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_list_workout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_add:
                createWorkoutExercise();
                return true;
            case R.id.menu_sync:
                synchronizeManually();
                return true;
            case R.id.menu_delete:
                deleteWorkoutExercises();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void deleteWorkoutExercises() {
        final Selection selection=mAdapter.getSelectionTracker()
                .getSelection();
        final long ids[]=new long[selection.size()];
        int i=0;
        for (Iterator it = selection.iterator(); it.hasNext(); ) {
            //Timber.d("ITEM %s",it.next());
            ids[i]= (long) it.next();
            i++;
        }
        Disposable g=Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                int result=CacheDatabase
                        .getDatabase(getContext())
                        .workoutDao()
                        .deleteWorkouts(ids);
                emitter.onSuccess(result);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Timber.d("delete");
                        mAdapter.getSelectionTracker().clearSelection();
                    }
                });
    }

    private void synchronizeManually() {
        Disposable a = CacheDatabase
                .getDatabase(this.getContext())
                .workoutDao()
                .getAllWords()
                .map(new Function<List<WorkoutExercise>, DataSet>() {
                    @Override
                    public DataSet apply(List<WorkoutExercise> workoutExercises) throws Exception {

                        DataSource exerciseSource = new DataSource.Builder()
                                .setAppPackageName(getContext())
                                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                                .setName(getContext().getResources().getString(R.string.app_name))
                                .setType(DataSource.TYPE_RAW)
                                .build();

                        final DataSet weightDataSet = DataSet.create(exerciseSource);
                        for (WorkoutExercise exercise : workoutExercises) {
                            DataPoint workoutExercise = DataPoint.create(exerciseSource);
                            workoutExercise.setTimeInterval(exercise.getStartTime().toInstant().toEpochMilli(), exercise.getEndTime().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
                            workoutExercise.getValue(Field.FIELD_EXERCISE).setString(exercise.getExercise());
                            workoutExercise.getValue(Field.FIELD_REPETITIONS).setInt(exercise.getRepetitions());
                            workoutExercise.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(exercise.getResistanceType());
                            workoutExercise.getValue(Field.FIELD_RESISTANCE).setFloat(exercise.getResistance());
                            //In miliseconds
                            workoutExercise.getValue(Field.FIELD_DURATION).setInt((int)ChronoUnit.MILLIS.between(exercise.getStartTime(),exercise.getEndTime()));
                            weightDataSet.add(workoutExercise);
                        }
                        return weightDataSet;
                    }
                })
                .map(new Function<DataSet, Boolean>() {
                    @Override
                    public Boolean apply(DataSet dataSet) throws Exception {
                        final HistoryClient historyClient = Fitness.getHistoryClient(getContext(), GoogleSignIn.getLastSignedInAccount(getContext()));
                        Task<Void> task = historyClient.insertData(dataSet);
                        Tasks.await(task, 20, TimeUnit.SECONDS);
                        if (task.isSuccessful()) {
                            return true;
                        } else {
                            throw task.getException();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean dataSet) throws Exception {
                        Snackbar.make(getView(), "Successful sync", Snackbar.LENGTH_LONG).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }

    private void createWorkoutExercise() {
        final Intent intent = new Intent(this.getContext(), InsertActivityActivity.class);
        startActivity(intent);
        /*
        final FragmentTransaction fragmentTransaction = this.getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new InsertWorkoutExerciseFragment(), "workout_exercise");
        fragmentTransaction.addToBackStack("insert_workout");
        fragmentTransaction.commit();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mUpdateDisposable = CacheDatabase.getDatabase(this.getContext())
                .workoutDao()
                .getAllWordsFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<WorkoutExercise>>() {
                    @Override
                    public void accept(List<WorkoutExercise> workoutExercises) throws Exception {
                        mAdapter.setWorkoutExercises(workoutExercises);
                    }
                });
        this.mWorkoutExerciseClickDisposable = this.mAdapter.getWorkoutExerciseClickFlowable()
                .subscribe(this.mWorkoutExerciseClickConsumer);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.mUpdateDisposable != null)
            this.mUpdateDisposable.dispose();
        this.mWorkoutExerciseClickDisposable.dispose();
    }

    class SheetDetailsLookup extends ItemDetailsLookup<Long> {

        private RecyclerView recyclerView;

        SheetDetailsLookup(RecyclerView recyclerView) {
            super();
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            final View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                if (holder instanceof WorkoutExerciseViewHolder) {
                    return new ItemDetails<Long>() {
                        @Override
                        public int getPosition() {
                            return recyclerView.getChildAdapterPosition(view);
                        }

                        @Nullable
                        @Override
                        public Long getSelectionKey() {
                            return holder.getItemId();
                        }
                    };
                }
            }
            return null;
        }
    }

}

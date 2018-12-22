package com.github.donmahallem.heartfit.activity;

import android.os.Bundle;
import android.view.View;

import com.github.donmahallem.heartfit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.WorkoutExercises;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class CreateSessionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        this.findViewById(R.id.btnInsertActivity).setOnClickListener(this);
        this.findViewById(R.id.btnGetActivity).setOnClickListener(this);
    }

    public void addData2() {

        DataSource activitySegmentDataSource = new DataSource.Builder()
                .setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setName( "-activity segments")
                .setType(DataSource.TYPE_RAW)
                .build();
        /*
        DataSet activitySegments = DataSet.create(activitySegmentDataSource);

        DataPoint firstRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
        firstRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        activitySegments.add(firstRunningDp);

        DataPoint walkingDp = activitySegments.createDataPoint()
                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
        walkingDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
        activitySegments.add(walkingDp);

        DataPoint secondRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
        secondRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        activitySegments.add(secondRunningDp);*/

        final LocalDateTime endTime = LocalDateTime.now(Clock.systemUTC());
        final LocalDateTime startTime = endTime.minusMinutes(5);

// Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName("Streng thtraining")
                .setDescription("Long run around Shoreline Park")
                .setIdentifier(FitnessActivities.STRENGTH_TRAINING + "_" + System.currentTimeMillis())
                .setActivity(FitnessActivities.STRENGTH_TRAINING)
                .setStartTime(startTime.toEpochSecond(ZoneOffset.UTC), TimeUnit.SECONDS)
                .setEndTime(endTime.toEpochSecond(ZoneOffset.UTC), TimeUnit.SECONDS)
                .build();

// Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(addData(startTime,endTime))
                .build();
        Fitness.getSessionsClient(this,GoogleSignIn.getLastSignedInAccount(this))
                .insertSession(insertRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("SUCES");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Timber.e(e);
                    }
                });
    }

    public DataSet addData(LocalDateTime start,LocalDateTime end) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setName(getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .build();

//        // Create a data set
        DataSet dataSet = DataSet.create(dataSource);
        dataSet.add(createDataPoint(dataSet,start));
        dataSet.add(createDataPoint(dataSet,end));

        return dataSet;
    }

    public DataPoint createDataPoint(DataSet dataset,LocalDateTime date){
        DataPoint curls = dataset.createDataPoint();
        //curls.getValue(Field.FIELD_ACTIVITY).setString(FitnessActivities.STRENGTH_TRAINING);
        curls.setTimestamp(date.toEpochSecond(ZoneOffset.UTC), TimeUnit.SECONDS);
        curls.getValue(Field.FIELD_EXERCISE).setString(WorkoutExercises.BICEP_CURL);
        curls.getValue(Field.FIELD_DURATION).setInt(30000);
        curls.getValue(Field.FIELD_REPETITIONS).setInt(10);
        curls.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(Field.RESISTANCE_TYPE_DUMBBELL);
        curls.getValue(Field.FIELD_RESISTANCE).setFloat((float)Math.random()*10f+10f);
        return curls;
    }
    public void createDataPoint(DataSet dataset,ZonedDateTime date,String workoutExercises,int repitition,int resistanceType,float resistance){
        DataPoint curls = dataset.createDataPoint();
        //curls.getValue(Field.FIELD_ACTIVITY).setString(FitnessActivities.STRENGTH_TRAINING);
        curls.setTimestamp(date.toEpochSecond(), TimeUnit.SECONDS);
        curls.getValue(Field.FIELD_EXERCISE).setString(workoutExercises);
        curls.getValue(Field.FIELD_DURATION).setInt(30000);
        curls.getValue(Field.FIELD_REPETITIONS).setInt(repitition);
        curls.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(resistanceType);
        curls.getValue(Field.FIELD_RESISTANCE).setFloat(resistance);
        dataset.add(curls);
    }


    public ZonedDateTime convertTime(int hour, int minute){
        return ZonedDateTime.now().withHour(hour).withMinute(minute);
    }
    public void addData3() {

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setName(getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .build();
        DataSet dataSet=DataSet.create(dataSource);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnInsertActivity:
                this.addData3();
                break;
            case R.id.btnGetActivity:
                break;
        }
    }
}

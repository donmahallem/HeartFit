package com.github.donmahallem.heartfit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.donmahallem.heartfit.activity.CreateSessionActivity;
import com.github.donmahallem.heartfit.activity.InsertActivityActivity;
import com.github.donmahallem.heartfit.activity.ListDataSourcesActivity;
import com.github.donmahallem.heartfit.activity.ListWorkoutExerciseActivity;
import com.github.donmahallem.heartfit.activity.SessionListActivity;
import com.github.donmahallem.heartfit.activity.WeightHistoryActivity;
import com.github.donmahallem.heartfit.module.nutritionviewer.activity.NutritionHistoryActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.WorkoutExercises;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 230;
    private final static int REQUEST_BLUETOOTH_ADMIN_PERMISSION = 2929;
    private OnDataPointListener mListener;
    private TextView mTxtCurrentBpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.btnSignin).setOnClickListener(this);
        this.findViewById(R.id.btnHeartData).setOnClickListener(this);
        this.mTxtCurrentBpm = this.findViewById(R.id.txtCurrentBpm);
        this.findViewById(R.id.btnUnsubscribe).setOnClickListener(this);
        this.findViewById(R.id.btnInsertActivity).setOnClickListener(this);
        this.findViewById(R.id.btnViewNutritionActivity).setOnClickListener(this);
        this.findViewById(R.id.btnShowDataSources).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_device_selection:
                final Intent intent = new Intent(this, DeviceActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_history:
                showHistoryActivity();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void showHistoryActivity() {
        /*
        final HistoryApi.ViewIntentBuilder builder=new HistoryApi.ViewIntentBuilder(this,DataType.TYPE_WEIGHT);
        builder.setTimeInterval(OffsetDateTime.now().minusMonths(1).toEpochSecond(),OffsetDateTime.now().toEpochSecond(),SECONDS);
        final Intent historyIntent=builder.build();
        startActivity(historyIntent);*/
        final Intent historyIntent = new Intent(this, WeightHistoryActivity.class);
        startActivity(historyIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasGooglePermissions()) {
            return;
        }
        GoogleSignInAccount a = GoogleSignIn.getLastSignedInAccount(this);
        if (a != null) {
            this.mTxtCurrentBpm.setText(GoogleSignIn.getLastSignedInAccount(this).getDisplayName());
            if (!GoogleSignIn.hasPermissions(a, Fitness.SCOPE_BODY_READ_WRITE)) {
                GoogleSignIn.requestPermissions(
                        this,
                        1245,
                        a,
                        Fitness.SCOPE_BODY_READ_WRITE);
            }
            if (!GoogleSignIn.hasPermissions(a, Fitness.SCOPE_ACTIVITY_READ_WRITE)) {
                GoogleSignIn.requestPermissions(
                        this,
                        1246,
                        a,
                        Fitness.SCOPE_ACTIVITY_READ_WRITE);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private FitnessOptions createFitnessOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_LOCATION_BOUNDING_BOX, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_LOCATION_BOUNDING_BOX, FitnessOptions.ACCESS_READ)
                .build();

    }

    public boolean hasGooglePermissions() {
        return this.hasGooglePermissions(createFitnessOptions());
    }

    public boolean hasGooglePermissions(FitnessOptions fitnessOptions) {
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
    }

    public void signinToGoogle() {
        final FitnessOptions fitnessOptions = createFitnessOptions();
        if (!hasGooglePermissions(fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void accessGoogleFit() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.HOURS)
                .build();


        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        Timber.d("onSuccess()");
                        for (Bucket b : dataReadResponse.getBuckets()) {
                            Timber.d("BUCKET========");
                            for (DataSet dataSet : b.getDataSets()) {
                                for (DataPoint dataPoint : dataSet.getDataPoints())
                                    Timber.d("Rate %s", dataPoint.getValue(Field.FIELD_BPM));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e(e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Timber.d("onComplete()");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_BLUETOOTH_ADMIN_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignin:
                signinToGoogle();
                //registerBleDevice();
                break;
            case R.id.btnHeartData:
                startActivity(new Intent(this,ListWorkoutExerciseActivity.class));
                break;
            case R.id.btnUnsubscribe:
                startActivity(new Intent(this, DeviceActivity.class));
                break;
            case R.id.btnInsertActivity:
                startActivity(new Intent(this, SessionListActivity.class));
                break;
            case R.id.btnViewNutritionActivity:
                Intent intent=new Intent(this,NutritionHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btnShowDataSources:
                startActivity(new Intent(this,ListDataSourcesActivity.class));
                break;
        }
    }
    public void testa(){

        DataSource exerciseSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setName(getResources().getString(R.string.app_name))
                .setType(DataSource.TYPE_RAW)
                .build();
        final long start=1542273719;
        final long end=1542275825;
        final long step=(end-start)/30;
        LocalDateTime startTime=LocalDateTime.ofEpochSecond(1542273719,0,ZoneOffset.UTC);
        LocalDateTime endTime=  LocalDateTime.ofEpochSecond(1542275825,625000000,ZoneOffset.UTC);
        DataSet weightDataSet = DataSet.create(exerciseSource);
        for(int i=0;i<30;i++){
            final long segmentStart=start+(i*step);
            final long segmentEnd=segmentStart+step;
            DataPoint curls = DataPoint.create(exerciseSource);
            curls.setTimeInterval(segmentStart,segmentEnd,SECONDS);
            curls.getValue(Field.FIELD_DISTANCE).setFloat(12500/30);
            //curls.getValue(Field.FIELD_DURATION).setInt((int)step*1000);
            weightDataSet.add(curls);
        }
        HistoryClient historyClient=Fitness.getHistoryClient(this,GoogleSignIn.getLastSignedInAccount(this));
        historyClient.insertData(weightDataSet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Timber.e(e);
                    }
                });
    }
    public void createDistanceDataPoint(DataSet dataSet,LocalDateTime from, LocalDateTime to){
        DataPoint a=dataSet.createDataPoint();
        a.getValue(Field.FIELD_DISTANCE).setFloat(25*30);
        a.setTimeInterval(from.toEpochSecond(ZoneOffset.UTC),to.toEpochSecond(ZoneOffset.UTC),TimeUnit.SECONDS);
        dataSet.add(a);
    }

    public void createDataPoint(DataSet dataSet,LocalDateTime from, LocalDateTime to,String fitness){
        DataPoint a=dataSet.createDataPoint();
        a.getValue(Field.FIELD_ACTIVITY).setActivity(fitness);
        a.setTimeInterval(from.toEpochSecond(ZoneOffset.UTC),to.toEpochSecond(ZoneOffset.UTC),TimeUnit.SECONDS);
        dataSet.add(a);
    }
}

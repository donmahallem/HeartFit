package com.github.donmahallem.heartfit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.recycler.adapter.SessionAdapter;
import com.github.donmahallem.heartfit.recycler.viewholder.SessionSimpleViewHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.SessionsApi;
import com.google.android.gms.fitness.SessionsClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.threeten.bp.ZonedDateTime;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class SessionListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private GoogleSignInAccount mGoogleSigninAccount;
    private SessionsClient mSessionsClient;
    private SessionAdapter mAdapter;
    private SessionSimpleViewHolder.OnSessionInteractListener mOnSessionInteractListener = new SessionSimpleViewHolder.OnSessionInteractListener() {
        @Override
        public void onLongClick(Session session) {/*
            DataDeleteRequest dataDeleteRequest=new DataDeleteRequest.Builder()
                    .setTimeInterval(session.getStartTime(TimeUnit.SECONDS)-1,session.getEndTime(TimeUnit.SECONDS)+1,TimeUnit.SECONDS)
                    .addSession(session)
                    .build();
            Fitness.getHistoryClient(SessionListActivity.this,GoogleSignIn.getLastSignedInAccount(SessionListActivity.this))
                    .deleteData(dataDeleteRequest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Timber.d("deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Timber.e(e);
                }
            });*/
        }

        @Override
        public void onClick(Session session) {
            if(!session.getActivity().equals(FitnessActivities.STRENGTH_TRAINING))
                return;
            Timber.d("Start %s end %s",session.getStartTime(TimeUnit.NANOSECONDS),session.getEndTime(TimeUnit.NANOSECONDS));
            Intent intent=new SessionsApi.ViewIntentBuilder(SessionListActivity.this)
                    .setPreferredApplication(getPackageName())
                    .setSession(session).build();
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);
        this.mAdapter = new SessionAdapter();
        this.mAdapter.setOnSessionInteractListener(this.mOnSessionInteractListener);
        this.mRecyclerView = this.findViewById(R.id.recyclerView);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mRecyclerView.setAdapter(this.mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mGoogleSigninAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (this.mGoogleSigninAccount != null) {
            this.mSessionsClient = Fitness.getSessionsClient(this, this.mGoogleSigninAccount);
            final ZonedDateTime stopTime = ZonedDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
            final ZonedDateTime startTime = stopTime.minusDays(31);
            SessionReadRequest request = new SessionReadRequest.Builder()
                    .enableServerQueries()
                    //.read(DataType.TYPE_WORKOUT_EXERCISE)
                    .readSessionsFromAllApps()
                    .setTimeInterval(startTime.toEpochSecond(), stopTime.toEpochSecond(), TimeUnit.SECONDS)
                    .build();
            final Trace myTrace = FirebasePerformance.getInstance().newTrace("fit_sessions_read");
            myTrace.start();
            this.mSessionsClient.readSession(request)
                    .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                        @Override
                        public void onSuccess(SessionReadResponse sessionReadResponse) {
                            Timber.d("Got sessions %s", sessionReadResponse.getSessions().size());
                            for (Session session : sessionReadResponse.getSessions()) {
                                Timber.d("Session %s - %s - %s - %s", session.getAppPackageName(), session.getDescription(), session.getActivity(), session.getIdentifier());
                            }
                            mAdapter.setSessions(sessionReadResponse.getSessions());
                            myTrace.putMetric("number_of_sessions",sessionReadResponse.getSessions().size());
                            myTrace.incrementMetric("successful",1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Timber.e(e);
                            myTrace.incrementMetric("errored",1);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<SessionReadResponse>() {
                        @Override
                        public void onComplete(Task<SessionReadResponse> task) {
                            Timber.d("REquest finished");

                            myTrace.stop();
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnInsertActivity:
                break;
            case R.id.btnGetActivity:
                break;
        }
    }
}

package com.github.donmahallem.common.recycler.activity;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class GoogleSignInActivity extends AppCompatActivity {


    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1252;
    private final Scope[] mScopes;

    public GoogleSignInActivity(Scope... requiredScopes){
        this.mScopes=requiredScopes;
    }

    @Nullable
    public GoogleSignInAccount getSignedInAccount(){
        return GoogleSignIn.getLastSignedInAccount(this);
    }

    public boolean hasRequiredScopes(){
        return GoogleSignIn.hasPermissions(getSignedInAccount(),this.mScopes);
    }


    public boolean isSignedIn(){
        return GoogleSignIn.getLastSignedInAccount(this)!=null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                onPermissionRequestResponse();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @CallSuper
    private void onPermissionRequestResponse() {

    }

    protected void requestMissingScopes() {
        GoogleSignIn.requestPermissions(
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                this.mScopes);
    }
}

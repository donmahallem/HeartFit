package com.github.donmahallem.heartfit.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created on 11.10.2018.
 */
public class HeartRateLoggingService extends IntentService {
    public HeartRateLoggingService() {
        super("HeartRateLoggingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("recieved Intent");
    }
}

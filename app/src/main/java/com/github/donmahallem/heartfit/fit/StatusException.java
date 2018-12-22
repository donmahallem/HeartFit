package com.github.donmahallem.heartfit.fit;

import com.google.android.gms.common.api.Status;

/**
 * Created on 18.11.2018.
 */
public class StatusException extends Exception {

    public StatusException(Status status){
        super(status.getStatusMessage());
    }
}

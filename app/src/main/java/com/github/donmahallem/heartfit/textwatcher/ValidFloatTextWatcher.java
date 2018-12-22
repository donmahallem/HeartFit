package com.github.donmahallem.heartfit.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ValidFloatTextWatcher  implements TextWatcher {
    private final EditText mEditText;
    private final Pattern mPattern=Pattern.compile("[0-9]+((\\.|,)[0-9]*)?");
    private final TextInputLayout mTextInputLayout;

    public ValidFloatTextWatcher(EditText inputFat,TextInputLayout textInputLayout) {
        this.mEditText=inputFat;
        this.mTextInputLayout=textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        final Matcher matcher=mPattern.matcher(charSequence);
        if(matcher.matches()) {
            this.mTextInputLayout.setErrorEnabled(false);
        }else {
            this.mTextInputLayout.setErrorEnabled(true);
            this.mTextInputLayout.setError("Invalid");
        }
        Timber.d("CHANGE: %s",charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

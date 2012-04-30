package com.crossconnect.util;

import android.widget.AutoCompleteTextView.Validator;

public class VerseValidator implements Validator {

    @Override
    public CharSequence fixText(CharSequence invalidText) {
        return invalidText;
    }

    @Override
    public boolean isValid(CharSequence text) {
        return true;
    }

}

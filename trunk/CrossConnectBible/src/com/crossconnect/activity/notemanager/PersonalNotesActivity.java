package com.crossconnect.activity.notemanager;

import com.crossconnect.views.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PersonalNotesActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personal_notes_editor_view);
    }
}

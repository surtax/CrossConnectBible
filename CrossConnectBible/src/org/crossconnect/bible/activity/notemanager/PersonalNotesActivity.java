package org.crossconnect.bible.activity.notemanager;

import org.crossconnect.bible.activity.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import org.crossconnect.bible.R;

public class PersonalNotesActivity extends FragmentActivity{

	PersonalNotesEditorFragment personalNotesEditorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personal_notes_editor_view);
        

        personalNotesEditorFragment = (PersonalNotesEditorFragment) getSupportFragmentManager().findFragmentByTag("notes_editor_fragment");
        
		ImageView up = (ImageView) findViewById(R.id.title_bar_icon);
		up.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
  	  	        Intent i = new Intent(PersonalNotesActivity.this, MainActivity.class);
  	  	        startActivity(i);
			}

		});

    }
}

package org.crossconnect.bible.actions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * A custom NotesEditText but this one doesn't require bibleTextview. This is actually a more stripped down version of 
 * NotesEditText.
 * 
 * Main difference is the org.crossconnect.bible.database calls are different.
 */

public class PersonalNotesEditText extends NotesEditText {
	
	public static final int NEW_NOTE_ID = -1;

	public int id = NEW_NOTE_ID;
	
	public PersonalNotesEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initialise(ImageButton lockBtn, final BibleTextView bibleTextView, int id) {
		this.id = id;
		setLockBtn(lockBtn);
    	setEnabled(false); //by default set it to be disabled
		lockBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                
                //
                if(isEnabled()) {
                	if (getId() == NEW_NOTE_ID) {
                		setId(notesService.saveNewPersonalNote(getText().toString()));
                	} else {
                		notesService.updatePersonalNote(getId(), getText().toString(), getReferences(), getLabels());
                	}
                } 
                edit();
                if(isEnabled()) {
                    requestFocus();
                    requestFocusFromTouch();
                }
                
//              TODO: menu for notes
//                Intent intent = new Intent(Main.this, ResourceRepositoryActivity.class);
//                startActivity(intent);    
            }
        });        
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
    
    @Override
    public void save() {
    	//Super of this is used for ontabchanged this is not used for personalnotes editor at the moment
        edit();
    }

}

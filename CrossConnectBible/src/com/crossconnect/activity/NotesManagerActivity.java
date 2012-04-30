package com.crossconnect.activity;

import java.util.List;

import net.sword.engine.sword.SwordContentFacade;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crossconnect.model.BibleText;
import com.crossconnect.model.Note;
import com.crossconnect.model.SwordBibleText;
import com.crossconnect.service.NotesService;
import com.crossconnect.actions.R;

public class NotesManagerActivity extends ListActivity{

    List<Note> notesList;
    NotesService notesService;
    String translation;
    
    private static final String TAG = "NotesManagerActivity";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_manager_view);
        // Tell the list view which view to display when the list is empty
        getListView().setEmptyView(findViewById(R.id.empty));
        
//        no need to guard should always have a default version 
//        if (getIntent().getExtras() != null && getIntent().getExtras().getString("Translation") != null) {
        	translation = getIntent().getExtras().getString("Translation");
//        }
        
        
        //TODO: Load this when it actually scrolls to the notes part instead of onCreate
        notesService = new NotesService(this);        
        notesList = notesService.getAllBibleNotesFromDB();
        
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ResourceAdapter(this, R.layout.note_row, notesList));

        getListView().setTextFilterEnabled(true);
        
        final ListView listView = getListView();
        
        listView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//TODO: only one of these is getting called not sure which one
				Intent i = new Intent ();
				BibleText bibleText = new SwordBibleText(notesList.get(position).getBook(), notesList.get(position).getChapter(), translation);
				SwordContentFacade.getInstance().injectChapterFromJsword(bibleText); 
				i.putExtra("BibleText", bibleText);
				setResult(RESULT_OK, i);
				finish();

			}
        	
        });

	}
	
	
    /**
     * The adapter to populate the resource column listeview
     * @author glo1
     *
     */
    private class ResourceAdapter extends ArrayAdapter<Note> {

        private List<Note> items;

        public ResourceAdapter(Context context, int textViewResourceId, List<Note> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.note_row, null);
                v.setFocusable(true);
            }
            Note note = items.get(position);
            if (note != null) {
                TextView tt = (TextView) v.findViewById(R.id.note_row_reference);
                TextView bt = (TextView) v.findViewById(R.id.note_row_text);
//                ImageButton downloadBtn = (ImageButton) v.findViewById(R.id.audio_download_icon);

                if (tt != null) {
                	String display = note.getBook();
                	if(note.getChapter() != 0) {
                		display += ":" + note.getChapter();
                	}
                    tt.setText(display);
                }
                if (bt != null) {
                    bt.setText(note.getText());
                }
            }
            
            final int thisPosition = position;
            
            v.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					//TODO: only one of these is getting called not sure which one
					Intent i = new Intent ();
					BibleText bibleText = new SwordBibleText(notesList.get(thisPosition).getBook(), notesList.get(thisPosition).getChapter(), translation);
					SwordContentFacade.getInstance().injectChapterFromJsword(bibleText); 
					i.putExtra("BibleText", bibleText);
					setResult(RESULT_OK, i);
					finish();

				}});
            
            return v;
        }
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(TAG, "onDestroy losing DB");
    	notesService.close();
    }
    
    


}

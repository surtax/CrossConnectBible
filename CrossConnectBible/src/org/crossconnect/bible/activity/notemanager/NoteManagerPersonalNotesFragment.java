/*
 * Copyright 2011 Gary Lo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.crossconnect.bible.activity.notemanager;

import java.util.List;

import org.crossconnect.bible.model.Note;
import org.crossconnect.bible.service.NotesService;

import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import org.crossconnect.bible.actions.R;

public class NoteManagerPersonalNotesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Note>> {
	
	private static final String TAG = "NoteManagerBibleNotes";
	
    private static NotesService notesService;        

	
    
    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class NoteListLoader extends AsyncTaskLoader<List<Note>> {

        List<Note> notesList;

        public NoteListLoader(Context context) {
            super(context);
            notesService = new NotesService(getContext());
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<Note> loadInBackground() {
			Log.d(TAG, "Load in Background");
            // Done!
            return notesService.getAllPersonalNotesFromDB();
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<Note> notes) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (notes != null) {
                    onReleaseResources(notes);
                }
            }
            List<Note> oldApps = notes;
            //Add dummy row for the Add Row 
            notes.add(new Note());
            notesList = notes;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(notes);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (notesList != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(notesList);
            } else {
            	forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<Note> apps) {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (notesList != null) {
                onReleaseResources(notesList);
                notesList = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<Note> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }



    public static class NoteListAdapter extends ArrayAdapter<Note> {
        private final LayoutInflater mInflater;
        View addRow;

        public NoteListAdapter(Context context) {
            super(context, R.layout.list_item_icon_text_two_line);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<Note> data) {
            clear();
            //AddAll is not supported till 3.x
            if (data != null) {
	            for (Note note : data) {
	                if (note != null) {
	                    add(note);
	                }
	            }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
            } else {
                view = convertView;
            }


			Note item = getItem(position);
			((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.icon_notes);
			((TextView) view.findViewById(R.id.text)).setText(item.getText());
            
            return view;
        }
    }

    

	
    // This is the Adapter being used to display the list's data.
    NoteListAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No Notes");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        //Only have one choice
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new NoteListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }


    @Override public void onListItemClick(ListView l, View v, int pos, long id) {
    	
    	final int position = pos;
    	
        //Get the corresponding resource
        final Note note = mAdapter.getItem(position);

    	
        final ActionItem openAction = new ActionItem();
        
        openAction.setTitle("Read");
        openAction.setIcon(getResources().getDrawable(R.drawable.icon_read));

        final ActionItem deleteAction = new ActionItem();
        
        deleteAction.setTitle("Delete");
        deleteAction.setIcon(getResources().getDrawable(R.drawable.icon_close));
        
        final ActionItem shareAction = new ActionItem();
        
        shareAction.setTitle("Share");
        shareAction.setIcon(getResources().getDrawable(R.drawable.ic_action_share));

    	
        final QuickActionHorizontal mQuickAction  = new QuickActionHorizontal(v);
        
        openAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Opening Personal Note ID:" + note.getId());
                //Open the note pass the id to it
                Intent intent = new Intent(getActivity(), PersonalNotesActivity.class);
                intent.putExtra(PersonalNotesEditorFragment.NOTE_BUNDLE_ID,note.getId());
                startActivity(intent);
                mQuickAction.dismiss();
            }
        });

        deleteAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//        		BibleText bibleText = new SwordBibleText( note.getBook(),  note.getChapter(), getActivity().getIntent().getExtras().getString("Translation"));
        		//TODO: should use this ID instead of key but will do for now need TABLE change
        		//notesService.removeNote(note.getId());
        		notesService.removeNote(note.getId());
                getLoaderManager().restartLoader(0, null, NoteManagerPersonalNotesFragment.this);
            	mAdapter.notifyDataSetChanged();
                mQuickAction.dismiss();
            }
        });
        
        mQuickAction.addActionItem(openAction);
        mQuickAction.addActionItem(deleteAction);
        shareAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String text = note.getText();
                Toast.makeText(getActivity(), "Share " + text, Toast.LENGTH_SHORT).show();
                Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
                sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "CrossConnect Notes");
                sendMailIntent.setType("text/plain");
                getActivity().startActivity(Intent.createChooser(sendMailIntent, "Share using..."));
                mQuickAction.dismiss();
            }
        });
        	mQuickAction.addActionItem(shareAction);
        
        mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
        
        mQuickAction.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        
        mQuickAction.show();

    	Log.i(TAG, "Item clicked: " + id);
    }

    @Override public Loader<List<Note>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
    	Log.i(TAG, "onCreateLoader()");
        return new NoteListLoader(getActivity());
    }

    @Override public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
    	Log.i(TAG, "onLoadFinished");
        // Set the new data in the adapter.
        mAdapter.setData(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override public void onLoaderReset(Loader<List<Note>> loader) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(TAG, "onDestroy losing DB");
    	notesService.close();
    }

    
}
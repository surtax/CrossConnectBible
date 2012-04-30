package com.crossconnect.activity.main;

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


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.crossconnect.activity.MainActivity;
import com.crossconnect.model.BibleText;
import com.crossconnect.service.NotesService;
import com.crossconnect.views.BibleTextViewImpl;
import com.crossconnect.views.NotesEditText;
import com.crossconnect.views.R;

public class NotesEditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
	
	private static final String TAG = "NoteEditorFragment";
	
    private static NotesService notesService;        
    
    private NotesEditText notesEditText;

    private ImageButton notesLock;
    
    BibleTextViewImpl bibleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.notes_column, container, false);
    }
    
    public void updateNotes(BibleText bibleText) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("BibleText", bibleText);
            getLoaderManager().restartLoader(0, bundle, this);
    }

    
    
    private void loadNotes() {
        BibleTextViewImpl bibleTextView = (BibleTextViewImpl) getActivity().findViewById(R.id.bible_text);
        
        if (bibleTextView != null && bibleTextView.getBibleText() != null) {
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            Bundle bundle = new Bundle();
            bundle.putParcelable("BibleText", bibleTextView.getBibleText());
            getLoaderManager().initLoader(0, bundle, this);
        }
    }
    
    
    private void updateNotes(String note) {
        if (note != null) {
            notesEditText.setText(note);
        } else {
            notesEditText.setText("");
        }
        notesEditText.lock();
        getActivity().findViewById(R.id.notes_progress).setVisibility(View.GONE);
    }
    
    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class NoteLoader extends AsyncTaskLoader<String> {

        BibleText bibleText;
        String note;

        public NoteLoader(Context context, BibleText bibleText) {
            super(context);
            this.bibleText = bibleText; 
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public String loadInBackground() {
			Log.e(TAG, "Load in Background");
            // Done!
            return notesService.getAllNotesOfKey(bibleText.getKey());
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(String notes) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (notes != null) {
                    onReleaseResources(notes);
                }
            }
            String oldNote = note;
            note = notes;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(notes);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldNote != null) {
                onReleaseResources(oldNote);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (note != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(note);
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
        @Override public void onCanceled(String note) {
            super.onCanceled(note);

            // At this point we can release the resources associated with 'note'
            // if needed.
            onReleaseResources(note);
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
            if (note != null) {
                onReleaseResources(note);
                note = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(String notes) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        
        
        bibleTextView = (BibleTextViewImpl) getActivity().findViewById(R.id.bible_text);
        
        if (bibleTextView != null && bibleTextView.getBibleText() != null) {
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            Bundle bundle = new Bundle();
            bundle.putParcelable("BibleText", bibleTextView.getBibleText());
            getLoaderManager().initLoader(0, bundle, this);
        }
        
        notesEditText = ((NotesEditText) getActivity().findViewById(R.id.notes_edit_text));
        notesLock = ((ImageButton) getActivity().findViewById(R.id.menu_button_notes_lock));
        notesService = ((MainActivity) getActivity()).getNotesService();


        notesEditText.initialise(notesLock, bibleTextView);
        bibleTextView.post(new Runnable(){

            @Override
            public void run() {
                loadNotes();                
            }});
        
        // Set to true so option menu gets called future support for action bar ICS
        setHasOptionsMenu(true);
    }


    @Override 
    public Loader<String> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
    	Log.i(TAG, "onCreateLoader()");
        return new NoteLoader(getActivity(),(BibleText) args.getParcelable("BibleText"));
    }

    @Override public void onLoadFinished(Loader<String> loader, String data) {
    	Log.i(TAG, "onLoadFinished");
    	updateNotes(data);
    }

    @Override public void onLoaderReset(Loader<String> loader) {
    }
    
    
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Doesn't get called for one reason or another will just do it in the parent activity
        //        ((ImageButton) getActivity().findViewById(R.id.menu_button_notes)).setVisibility(View.VISIBLE);
        //        ((ImageButton) getActivity().findViewById(R.id.menu_button_notes_lock)).setVisibility(View.VISIBLE);

        // TODO: when ICS is popularised replace with action bar
//        MenuItem populateItem = menu.add(Menu.NONE, POPULATE_ID, 0, "Populate");
//        MenuItemCompat.setShowAsAction(populateItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        MenuItem clearItem = menu.add(Menu.NONE, CLEAR_ID, 0, "Clear");
//        MenuItemCompat.setShowAsAction(clearItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

    }
    
    
	public void save() {
    	Log.d(TAG, "Saving unsaved changes");
		notesEditText.save();
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return notesEditText.isEnabled();
	}
    
}
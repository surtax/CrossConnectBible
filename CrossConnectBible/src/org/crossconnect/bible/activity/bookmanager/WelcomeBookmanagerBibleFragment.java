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

package org.crossconnect.bible.activity.bookmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sword.engine.sword.AcceptableBookTypeFilter;
import net.sword.engine.sword.SwordDocumentFacade;

import org.crossconnect.bible.activity.DownloadStatus;
import org.crossconnect.bible.activity.WelcomeDownloadStatus;
import org.crossconnect.bible.service.DownloadManager;
import org.crossconnect.bible.util.RequestResultCodes;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;

import android.app.Activity;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.crossconnect.bible.R;

public class WelcomeBookmanagerBibleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Book>> {
	
	private static final String TAG = "WelcomeBookmanagerBibleFragment";

	DownloadManager installService;
	
	private static final String CROSSWIRE_REPOSITORY = "CrossWire";
	
	private static BookFilter SUPPORTED_DOCUMENT_TYPES = new AcceptableBookTypeFilter();
	
	boolean forceBasicFlow;
	

    List<Book> allBooks;
    
    List<Book> bibles;
    
    BookListLoader bookListLoader;
    
    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class BookListLoader extends AsyncTaskLoader<List<Book>> {

        List<Book> mApps;

        public BookListLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<Book> loadInBackground() {
			Log.d(TAG, "Load in Background");

        	List<Book> installableBooks = new ArrayList<Book>();
            List<Book> bibles = new ArrayList<Book>();
            
            DownloadManager installService = new DownloadManager();

      			try {
      				Log.d("BookManager", "Getting avialable books");
      				List<Book > availableBooks= installService.getDownloadableBooks(SUPPORTED_DOCUMENT_TYPES, CROSSWIRE_REPOSITORY, false);
	      			List<Book> installedBooks = SwordDocumentFacade.getInstance().getDocuments();
	      			installableBooks.addAll(availableBooks);
	      			//remove installed books
	      			for (Book book: installedBooks) {
		      			installableBooks.remove(book); 
	      			}
	      			
	      			bibles = new ArrayList<Book>();
	      			
	      			//Put ESV and KJV at the top if they exist
	      			Book ESV = null;
	      			Book KJV = null;
	      			
	      			for (Book book : installableBooks) {
	      				if(BookCategory.BIBLE.equals(book.getBookCategory())){
	      					if ("ESV".equals(book.getInitials())) {
	      						ESV = book;
	      					} else if ("KJV".equals(book.getInitials())) {
	      						KJV = book;
	      					}
	      					bibles.add(book);
	      				}
	      	        }
	      			
	      			Collections.sort(bibles);
	      			if (KJV != null )
	      				bibles.add(0, KJV);
	      			if (ESV != null)
	      				bibles.add(0, ESV);
	      			
	      			
      			} catch (Exception e) {
      				
      			}

            
            
            // Done!
            return bibles;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<Book> books) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (books != null) {
                    onReleaseResources(books);
                }
            }
            List<Book> oldApps = books;
            mApps = books;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(books);
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
            if (mApps != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mApps);
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
        @Override public void onCanceled(List<Book> apps) {
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
            if (mApps != null) {
                onReleaseResources(mApps);
                mApps = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<Book> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }



    public static class AppListAdapter extends ArrayAdapter<Book> {
        private final LayoutInflater mInflater;

        public AppListAdapter(Context context) {
            super(context, R.layout.list_item_text);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<Book> data) {
            clear();
            //AddAll is not supported till 3.x
            if(data != null) {
                for (Book book : data) {
                    if (book != null) {
                        add(book);
                    }
                }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            //Convertview will not be null if already drawn so no need to redraw
            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
            } else {
                view = convertView;
            }

            Book item = getItem(position);

            ((ImageView)view.findViewById(R.id.icon)).setImageResource(R.drawable.icon_bible);
            ((TextView)view.findViewById(R.id.text)).setText(item.getName());

            return view;
        }
    }

    

	
    // This is the Adapter being used to display the list's data.
    AppListAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No applications");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new AppListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }


    @Override public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("Installer", "Installing " + mAdapter.getItem(position).getInitials());
    	try {
    		Log.d("WelcomeBookmanagerBibleFragment", "Starting Download");
    		// the download happens in another thread
    		SwordDocumentFacade.getInstance().downloadDocument(mAdapter.getItem(position));
        	startActivityForResult(new Intent(getActivity(), WelcomeDownloadStatus.class), RequestResultCodes.DOWNLOAD_REQUEST);

    	} catch (Exception e) {
    		Log.d("BookManagerActivity", "Error on attempt to download", e);
    	}

    }

    @Override public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
    	Log.i("LoaderCustom", "onCreateLoader()");
        bookListLoader = new BookListLoader(getActivity());
        return bookListLoader;
    }

    @Override public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
    	Log.i("LoaderCustom", "onLoadFinished");
        // Set the new data in the adapter.
        mAdapter.setData(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override public void onLoaderReset(Loader<List<Book>> loader) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
    }
}
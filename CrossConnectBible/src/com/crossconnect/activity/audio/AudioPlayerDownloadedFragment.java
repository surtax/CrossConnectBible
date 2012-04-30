package com.crossconnect.activity.audio;

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


import java.util.List;

import musicplayer.MusicService;
import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crossconnect.activity.ArticleActivity;
import com.crossconnect.model.OnlineAudioResource;
import com.crossconnect.service.ResourceService;
import com.crossconnect.actions.R;

public class AudioPlayerDownloadedFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<OnlineAudioResource>> {
	
	private static final String TAG = "AudioPlayerDownloadFragment";
	
    private static ResourceService resourceService;        
    
    // This is the Adapter being used to display the list's data.
    ResourceListAdapter mAdapter;

    public static class ResourceListAdapter extends ArrayAdapter<OnlineAudioResource> {
        private final LayoutInflater mInflater;

        public ResourceListAdapter(Context context) {
            super(context, R.layout.list_item_text);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<OnlineAudioResource> data) {
            clear();
            //AddAll is not supported till 3.x
            if(data != null) {
                for (OnlineAudioResource book : data) {
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

            OnlineAudioResource item = getItem(position);
//            ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
            ((TextView)view.findViewById(R.id.text)).setText(item.getResourceName() + " " + item.getResourceVerse());

            return view;
        }
    }

    

    /**
     * A custom Loader that loads all of the downloaded resources.
     */
    public static class DownloadedListLoader extends AsyncTaskLoader<List<OnlineAudioResource>> {

        String book;
        boolean onlyDownloaded;
        List<OnlineAudioResource> resources;

        public DownloadedListLoader(Context context, String book, boolean onlyDownloaded) {
        	super(context);
            resourceService = new ResourceService(getContext());
            this.book = book;
            this.onlyDownloaded = onlyDownloaded;
		}

		/**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<OnlineAudioResource> loadInBackground() {
			Log.d(TAG, "Load in Background");
            // Done!
			if (onlyDownloaded) {
				return resourceService.getDownloadedExistsResources(book);
			}
			return null;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<OnlineAudioResource> newResources) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (newResources != null) {
                    onReleaseResources(newResources);
                }
            }
            List<OnlineAudioResource> oldResources = resources;
            resources = newResources;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(newResources);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldResources != null) {
                onReleaseResources(oldResources);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (resources != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(resources);
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
        @Override public void onCanceled(List<OnlineAudioResource> note) {
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
            if (resources != null) {
                onReleaseResources(resources);
                resources = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<OnlineAudioResource> notes) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
    
    @Override public void onListItemClick(ListView l, View v, int pos, long id) {
    	
    	final int position = pos;
    	
        //Get the corresponding resource
        final OnlineAudioResource resource = mAdapter.getItem(position);

    	
        final ActionItem playAction = new ActionItem();
        
        playAction.setTitle("Play");
        playAction.setIcon(getResources().getDrawable(R.drawable.kontak));

        final ActionItem deleteAction = new ActionItem();
        
        deleteAction.setTitle("Delete");
        deleteAction.setIcon(getResources().getDrawable(R.drawable.kontak));
        
        final ActionItem readAction = new ActionItem();
        
        readAction.setTitle("Read");
        readAction.setIcon(getResources().getDrawable(R.drawable.kontak));

    	
        final QuickActionHorizontal mQuickAction  = new QuickActionHorizontal(v);
        
        final String text               = "blah";
        
        
        //Check the links are actually there
        if (resource.getAudioURL() != null) {
            deleteAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	
//                	final String RESOURCE_FOLDER = "/CrossConnectAudio/Resources/";
//                    Toast.makeText(getActivity(), "Downloading Audio File " + text, Toast.LENGTH_SHORT).show();
//            		Log.d(TAG, "Downloading Audio " + mAdapter.getItem(position).getResourceName() + "from " + mAdapter.getItem(position).getAudioURL());
//
//            		Request request = new Request(Uri.parse(resource.getAudioURL()));
//            		File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + RESOURCE_FOLDER);
//            		path.mkdir();
//                    
//            		String filePath  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + RESOURCE_FOLDER + FileUtil.getFileName(mAdapter.getItem(position), bibleText); 
//            		
//            		resourceService.insertUpdate(mAdapter.getItem(position), filePath, bibleText);
//            		
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, RESOURCE_FOLDER + FileUtil.getFileName(mAdapter.getItem(position), bibleText));
//
//            	    ((DownloadManager) getActivity().getSystemService("download")).enqueue(request);

                    mQuickAction.dismiss();
                }
            });
        	mQuickAction.addActionItem(deleteAction);

        	
            playAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Streaming Audio File " + text, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Streaming Audio " + mAdapter.getItem(position).getResourceName() + " from " + mAdapter.getItem(position).getAudioURL());
                    try {
                        // the download happens in another thread
                        Intent i = new Intent(MusicService.ACTION_URL);
                        Uri uri = Uri.parse("file://" + mAdapter.getItem(position).getAudioURL());
                        i.setData(uri);
                        getActivity().startService(i);
//                        getActivity().startService(new Intent(MusicService.ACTION_PLAY));


                    } catch (Exception e) {
                        Log.e("BookManagerActivity", "Error on attempt to download", e);
                    }

                    mQuickAction.dismiss();
                }
            });

        	mQuickAction.addActionItem(playAction);
        }

        
        if (resource.getReadURL() != null) {
            readAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Read " + text, Toast.LENGTH_SHORT).show();
            		Intent intent = new Intent(getActivity(), ArticleActivity.class);
            		if(resource.getReadURL() != null){
            			intent.putExtra("url",resource.getReadURL());
            			intent.putExtra("verse",resource.getResourceVerse());
            			startActivity(intent);			
            		}
                    
                    mQuickAction.dismiss();
                }
            });
        	mQuickAction.addActionItem(readAction);
        }
        
        mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
        
        mQuickAction.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        
        mQuickAction.show();
    }


    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String book = "John";
        try {
             book = getActivity().getIntent().getExtras().getString("Book");
        } catch (NullPointerException e) {
        	
        }
//    	chapter = getIntent().getExtras().getInt("Chapter");
//    	translation = getIntent().getExtras().getString("Translation");

        
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Bundle bundle = new Bundle();
        bundle.putString(BOOK_BUNDLE_KEY, book);
        bundle.putBoolean(ONLY_DOWNLOADED_BUNDLE_KEY, true);
        getLoaderManager().initLoader(0, bundle, this);
        
        //Start with progress indicator
        setListShown(false);

//        // Give some text to display if there is no data.  In a real
//        // application this would come from a resource.
//        setEmptyText("Sorry no Online Resources were received");


        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new ResourceListAdapter(getActivity());
        setListAdapter(mAdapter);

        
    }

    private final static String BOOK_BUNDLE_KEY = "Book";
    private final static String ONLY_DOWNLOADED_BUNDLE_KEY = "Downloaded_Flag";
    

    @Override 
    public Loader<List<OnlineAudioResource>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
    	Log.i(TAG, "onCreateLoader()");
        return new DownloadedListLoader(getActivity(),args.getString(BOOK_BUNDLE_KEY), args.getBoolean((ONLY_DOWNLOADED_BUNDLE_KEY)));
    }

	@Override
	public void onLoadFinished(Loader<List<OnlineAudioResource>> loader, List<OnlineAudioResource> data) {
    	Log.d(TAG, "onLoadFinished");
        // Set the new data in the adapter.
        mAdapter.setData(data);
        
        setListShown(true);

		
	}


	@Override
	public void onLoaderReset(Loader<List<OnlineAudioResource>> arg0) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
	}
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(TAG, "onDestroy losing DB");
    	resourceService.close();
    }

}
package org.crossconnect.bible.activity.main;

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


import java.io.File;
import java.util.List;

import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;

import org.crossconnect.bible.R;
import org.crossconnect.bible.activity.ArticleActivity;
import org.crossconnect.bible.activity.MainActivity;
import org.crossconnect.bible.adapter.ResourceListAdapter;
import org.crossconnect.bible.loaders.ResourceLoader;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.OnlineAudioResource;
import org.crossconnect.bible.musicplayer.MusicService;
import org.crossconnect.bible.service.ResourceService;
import org.crossconnect.bible.util.FileUtil;
import org.crossconnect.bible.utility.Utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ResourceFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<OnlineAudioResource>> {
	
	private static final String TAG = "ResourceFragment";
	
    private ResourceService resourceService;        
    
    private OnClickListener downloadListener;
    
    ResourceLoader resourceListLoader;
    
    private BibleText bibleText;
    private BibleText loadingBibleText;

    private Button retryButton;
    private LinearLayout noInternetLayout;
    
    ProgressBar progress;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.resource_column, container, false);
		progress = (ProgressBar) v.findViewById(R.id.resource_progress);
		retryButton = (Button) v.findViewById(R.id.retry_btn);
		retryButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				noInternetLayout.setVisibility(View.GONE);
				updateResources(loadingBibleText);
			}} );
		
		noInternetLayout = (LinearLayout) v.findViewById(R.id.no_internet_msg_layout);
		return v;
	}
	


    public void updateResources(BibleText loadingBibleText) {
    	this.loadingBibleText = loadingBibleText;
    	//Update if the new bibleText is different
    	if (mAdapter != null && (bibleText == null || (bibleText != null && !loadingBibleText.getShortReferenceBookChapterVerse().equals(bibleText.getShortReferenceBookChapterVerse())))) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("BibleText", loadingBibleText);
            mAdapter.clear();
            progress.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.GONE);
            getLoaderManager().restartLoader(0, bundle, this);
    	}
    	
}


    
    
//    private void loadNotes() {
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("BibleText", Utils.loadBibleText(getActivity().getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE)));
//            getLoaderManager().initLoader(0, bundle, this);
//    }
    
    @Override public void onListItemClick(ListView l, View v, int pos, long id) {
    	
    	final int position = pos;
    	
        //Get the corresponding resource
        final OnlineAudioResource resource = mAdapter.getItem(position);

        final ActionItem readAction = new ActionItem();
        
        readAction.setTitle("Read");
        readAction.setIcon(getResources().getDrawable(R.drawable.icon_read));

        final ActionItem playAction = new ActionItem();
        
        playAction.setTitle("Play");
        playAction.setIcon(getResources().getDrawable(R.drawable.icon_play));

        final ActionItem downloadAction = new ActionItem();
        
        downloadAction.setTitle("Download");
        downloadAction.setIcon(getResources().getDrawable(R.drawable.icon_download));
        
        final QuickActionHorizontal mQuickAction  = new QuickActionHorizontal(v);
        
        if (resource.getReadURL() != null) {
            readAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
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

        
        //Check the links are actually there
        if (resource.getAudioURL() != null) {
            downloadAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	
                	final String RESOURCE_FOLDER = "/CrossConnectAudio/Resources/";
                    Toast.makeText(getActivity(), "Downloading Audio for " + resource.getResourceName(), Toast.LENGTH_SHORT).show();
            		Log.d(TAG, "Downloading Audio " + mAdapter.getItem(position).getResourceName() + "from " + mAdapter.getItem(position).getAudioURL());

            		Request request = new Request(Uri.parse(resource.getAudioURL()));
            		File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + RESOURCE_FOLDER);
            		path.mkdir();
                    
            		String filePath  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + RESOURCE_FOLDER + FileUtil.getFileName(mAdapter.getItem(position), bibleText); 
            		
            		resourceService.insertUpdate(mAdapter.getItem(position), filePath, bibleText);
            		
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, RESOURCE_FOLDER + FileUtil.getFileName(mAdapter.getItem(position), bibleText));

            	    ((DownloadManager) getActivity().getSystemService("download")).enqueue(request);

                    mQuickAction.dismiss();
                }
            });
        	mQuickAction.addActionItem(downloadAction);

        	
            playAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Streaming Audio for " + resource.getResourceName(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Streaming Audio" + mAdapter.getItem(position).getResourceName() + "from " + mAdapter.getItem(position).getAudioURL());
                    try {
                        // the download happens in another thread
                        Intent i = new Intent(MusicService.ACTION_URL);
                        Uri uri = Uri.parse(mAdapter.getItem(position).getAudioURL());
                        i.setData(uri);
                        getActivity().startService(i);

                    } catch (Exception e) {
                        Log.e("BookManagerActivity", "Error on attempt to download", e);
                    }

                    mQuickAction.dismiss();
                }
            });

        	mQuickAction.addActionItem(playAction);
        }

        mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
        
        mQuickAction.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        
        mQuickAction.show();
    }

    
    // This is the Adapter being used to display the list's data.
    ResourceListAdapter mAdapter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        resourceService = ((MainActivity) getActivity()).getResourceService();

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Bundle bundle = new Bundle();
        bundle.putParcelable("BibleText", Utils.loadBibleText(getActivity().getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE)));
        getLoaderManager().initLoader(0, bundle, this);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new ResourceListAdapter(getActivity());
        setListAdapter(mAdapter);
        
    }


    @Override public Loader<List<OnlineAudioResource>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  
    	Log.i(TAG, "onCreateLoader()");
    	resourceListLoader = new ResourceLoader(getActivity(),(BibleText) args.getParcelable("BibleText"));
        return resourceListLoader;
    }


	@Override
	public void onLoadFinished(Loader<List<OnlineAudioResource>> loader,
			List<OnlineAudioResource> data) {
    	Log.i(TAG, "onLoadFinished");
        // Set the new data in the adapter.
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        
        if (data == null) {
        	//Issue retreiving data
        	noInternetLayout.setVisibility(View.VISIBLE);
        } else {
        	// Successfully retreived data
        	noInternetLayout.setVisibility(View.GONE);
        	
        	//Set current bibleText to the one loaded
            bibleText = loadingBibleText;
        }
        
        // Animate so that the list fades in
        getActivity().findViewById(android.R.id.list).startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        progress.setVisibility(View.GONE);
	}


	@Override
	public void onLoaderReset(Loader<List<OnlineAudioResource>> arg0) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	    
}
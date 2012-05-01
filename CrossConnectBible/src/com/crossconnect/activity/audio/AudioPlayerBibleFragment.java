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


import musicplayer.MusicService;
import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import utility.Utils;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crossconnect.actions.R;
import com.crossconnect.model.BibleText;

public class AudioPlayerBibleFragment extends ListFragment {
	
	private static final String TAG = "AudioBibleFragment";
	
    // This is the Adapter being used to display the list's data.
    AudioBibleAdapter mAdapter;
    
    public void updateList(BibleText bibleText) {
        
        int numVerses = Utils.getNumVerses(bibleText.getKey());
        String book = bibleText.getBook();
        
        //Re-initialise list to number of chapters of the given audio
        mAdapter.setData(book, numVerses);
    }

    
    public static class AudioBibleAdapter extends ArrayAdapter<String> {
        private final LayoutInflater mInflater;

        private static final String ESV_MP3_API_URL = "http://www.esvapi.org/v2/rest/passageQuery?key=IP&output-format=mp3&passage=";
        
        private String book = null;
        public AudioBibleAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(String book, int numChapters) {
            clear();
            this.book = book;
            //AddAll is not supported till 3.x
                for (int i = 1; i <= numChapters; i++) {
                    add(ESV_MP3_API_URL + book.replaceAll(" ", "") + i);
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

//            ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
            ((TextView)view.findViewById(R.id.text)).setText(book + " " + ++position);

            return view;
        }
    }
    
    @Override public void onListItemClick(ListView l, View v, int pos, long id) {
        
        final int position = pos;
        

        
        final ActionItem playAction = new ActionItem();
        
        playAction.setTitle("Play");
        playAction.setIcon(getResources().getDrawable(R.drawable.kontak));

        final ActionItem downloadAction = new ActionItem();
        
        downloadAction.setTitle("Download");
        downloadAction.setIcon(getResources().getDrawable(R.drawable.kontak));
        
        final ActionItem readAction = new ActionItem();
        
        readAction.setTitle("Read");
        readAction.setIcon(getResources().getDrawable(R.drawable.kontak));

        
        final QuickActionHorizontal mQuickAction  = new QuickActionHorizontal(v);
        
        final String text               = "blah";
        
        
        playAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Play: " + mAdapter.getItem(position));
                try {
                    // the download happens in another thread
                    Intent i = new Intent(MusicService.ACTION_URL);
                    Uri uri = Uri.parse(mAdapter.getItem(position));
                    i.setData(uri);
                    getActivity().startService(i);

                } catch (Exception e) {
                    Log.e("BookManagerActivity", "Error on attempt to download", e);
                }

                mQuickAction.dismiss();
            }
        });

        downloadAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Downloading Audio File " + text, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Downloading Audio" + mAdapter.getItem(position));

                Request request = new Request(Uri.parse(mAdapter.getItem(position)));
                ((DownloadManager) getActivity().getSystemService("download")).enqueue(request);

                mQuickAction.dismiss();
            }
        });
        
        //Check the links are actually there
        mQuickAction.addActionItem(playAction);
        mQuickAction.addActionItem(downloadAction);
        if (mAdapter.getItem(position) != null) {
            readAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Play All " + text, Toast.LENGTH_SHORT).show();
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
    
    private BibleText initialBibleText;
    
    public void setBibleText(BibleText bibleText) {
    	this.initialBibleText = bibleText;
    }


    
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("Audio only available for KJV and ESV");

//        //Only have one choice
//        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new AudioBibleAdapter(getActivity());
        mAdapter.setNotifyOnChange(true);
        setListAdapter(mAdapter);


        
        //Just use the manually set initial bibletext if set else find the bibletextview
        if (initialBibleText != null) {
        	updateList(initialBibleText);
        }
        
        updateList(Utils.loadBibleText(getActivity().getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE)));
        
        // Set to true so option menu gets called future support for action bar ICS
        setHasOptionsMenu(true);
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
}
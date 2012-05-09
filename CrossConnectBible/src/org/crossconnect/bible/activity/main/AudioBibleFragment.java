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


import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;

import org.crossconnect.bible.R;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.musicplayer.MusicService;
import org.crossconnect.bible.utility.Utils;
import org.crossconnect.bible.views.BibleTextView;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
;
public class AudioBibleFragment extends ListFragment {
	
	private static final String TAG = "AudioBibleFragment";
	
    BibleTextView bibleTextView;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.book_manager_view, container, false);
//    }
    
    public void updateList(BibleText bibleText) {
        
        int numVerses = Utils.getNumVerses(bibleText.getKey());
        String book = bibleText.getBook();
        
        //Re-initialise list to number of chapters of the given audio
        if (mAdapter != null)
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

        	((ImageView)view.findViewById(R.id.icon)).setImageResource(R.drawable.icon_book_rss);
            ((TextView)view.findViewById(R.id.text)).setText(book + " " + ++position);

            return view;
        }
    }
    
    @Override public void onListItemClick(ListView l, View v, int pos, long id) {
        
        final int position = pos;
        

        
        final ActionItem playAction = new ActionItem();
        
        playAction.setTitle("Play");
        playAction.setIcon(getResources().getDrawable(R.drawable.icon_play));

        final ActionItem downloadAction = new ActionItem();
        
        downloadAction.setTitle("Download");
        downloadAction.setIcon(getResources().getDrawable(R.drawable.icon_download));
        
        final QuickActionHorizontal mQuickAction  = new QuickActionHorizontal(v);
        
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
                Toast.makeText(getActivity(), "Downloading Audio File", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Downloading Audio" + mAdapter.getItem(position));

                Request request = new Request(Uri.parse(mAdapter.getItem(position)));
                
//        		File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + "/CrossConnectAudio");
//        		path.mkdir();
//                
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, "/CrossConnectAudio/test.mp3");

                ((DownloadManager) getActivity().getSystemService("download")).enqueue(request);

                mQuickAction.dismiss();
            }
        });
        
        //Check the links are actually there
        mQuickAction.addActionItem(playAction);
        mQuickAction.addActionItem(downloadAction);
        
        mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
        
        mQuickAction.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        
        mQuickAction.show();
    }


    
    
    // This is the Adapter being used to display the list's data.
    AudioBibleAdapter mAdapter;

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


        
        bibleTextView = (BibleTextView) getActivity().findViewById(R.id.bible_text);
        
        if (bibleTextView != null && bibleTextView.getBibleText() != null) {
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            updateList(bibleTextView.getBibleText());
        }
        
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
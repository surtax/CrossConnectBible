/*
 * Copyright (C) 2011 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.crossconnect.activity;


import java.util.ArrayList;
import java.util.List;

import musicplayer.MusicActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.crossconnect.activity.bookmanager.BookmanagerInstalledFragment;
import com.crossconnect.activity.main.AudioBibleFragment;
import com.crossconnect.activity.main.BibleTextFragment;
import com.crossconnect.activity.main.NotesEditorFragment;
import com.crossconnect.activity.main.ResourceFragment;
import com.crossconnect.model.BibleText;
import com.crossconnect.model.SwordBibleText;
import com.crossconnect.service.NotesService;
import com.crossconnect.views.ChapterSelectionActivity;
import com.crossconnect.views.PreferencesFromXml;
import com.crossconnect.views.R;
import com.crossconnect.views.ResourceRepositoryActivity;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI that switches between tabs and also allows
 * the user to perform horizontal flicks to move between the tabs.
 */
public class MainActivity extends FragmentActivity {
	
    private static final String TAG = "MainActivity";
	
    TabHost mTabHost;

    ViewPager mViewPager;

    TabsAdapter mTabsAdapter;
    
    private final static int HONEYCOMB = 11;
    private final static String DEFAULT_TAB = "DEFAULT_TAB";
    
    private final static String AUDIO_TAG = "Audio";
    private final static String RESOURCE_TAG = "Resource";
    private final static String BIBLE_TAG = "Bible";
    private final static String NOTES_TAG = "Notes";
    
    //The return codes for startactivity for result
    public static final int CHAPTER_SELECT_CODE = 1;
    public static final int WINDOW_SELECT_CODE = 2;
    public static final int TRANSLATION_SELECT_CODE = 3;
    public static final int NOTES_SELECT_CODE = 4;
    public static final int MAIN_SELECT_CODE = 5;
    
    static List<ImageButton> resource_top_icons;
    static List<ImageButton> bible_text_top_icons;
    static List<ImageButton> audio_top_icons;
    static List<ImageButton> notes_top_icons;
    
    private BibleText bibleText;
    
    public int windowId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
        
        
        //Add top menu icons into the seperate list 
        resource_top_icons = new ArrayList<ImageButton>();
        resource_top_icons.add((ImageButton) findViewById(R.id.menu_button_churches));
        resource_top_icons.add((ImageButton) findViewById(R.id.menu_button_browse_resources));

        
        bible_text_top_icons = new ArrayList<ImageButton>();
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_translations));
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_windows));
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_star));

        audio_top_icons = new ArrayList<ImageButton>();
        audio_top_icons.add((ImageButton) findViewById(R.id.menu_button_audio));
        audio_top_icons.add((ImageButton) findViewById(R.id.menu_button_browse_audio));

        notes_top_icons = new ArrayList<ImageButton>();
        notes_top_icons.add((ImageButton) findViewById(R.id.menu_button_notes));
        notes_top_icons.add((ImageButton) findViewById(R.id.menu_button_notes_lock));
        
        hideAllIcons();
        
        //Load last opened verse
        String bibleBook = getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE).getString("current_book", "Philipiians");
        int chapter = Integer.valueOf(getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE).getString("current_chapter", "1"));
        int verse = Integer.valueOf(getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE).getString("current_verse", "1"));
        String translation = getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE).getString("current_translation", "ESV");

        bibleText = new SwordBibleText(bibleBook, chapter, verse, translation);

        
        //Setup listeners for top menu icons

        //Go to notes browsing view
        ((ImageButton) findViewById(R.id.menu_button_notes)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
                startActivityForResult(intent, NOTES_SELECT_CODE);    
            }
        });        

        //Go to download books
        ((ImageButton) findViewById(R.id.menu_button_translations)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookManagerActivity.class);
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
//                intent.putExtra("WindowId", windowId);
                startActivityForResult(intent, TRANSLATION_SELECT_CODE);    
//                overridePendingTransition(R.anim.zoom_enter, 0);

//                QuickActionVertical qa = new QuickActionVertical(v);
//                
//                qa.addActionItem(first);
//                qa.addActionItem(second);
//                
//                qa.show();

            }
        });        
        
        ((ImageButton) findViewById(R.id.menu_button_star)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                
                startActivity(new Intent(MainActivity.this, PreferencesFromXml.class));
            }
        });        

            
        ((ImageButton) findViewById(R.id.menu_button_churches)).setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ResourceRepositoryActivity.class);
                    startActivity(intent);    
                }
        });
        
        ((ImageButton) findViewById(R.id.menu_button_browse_audio)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResourceRepositoryActivity.class);
                startActivity(intent);    
            }
        });

        
        ((ImageButton) findViewById(R.id.menu_button_audio)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);    
            }
        });        
        
        ((ImageButton) findViewById(R.id.menu_button_browse_resources)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);    
            }
        });
        
        findViewById(R.id.header_box).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                //TODO: can we pass the actual BibleText?
                Intent intent = new Intent(MainActivity.this, ChapterSelectionActivity.class);
                intent.putExtra("Book", bibleText.getBook());
                intent.putExtra("Chapter", bibleText.getChapter());
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
                startActivityForResult(intent, CHAPTER_SELECT_CODE);
            }
        });

        
        


//        if (Build.VERSION.SDK_INT >=  HONEYCOMB) {
//            // If has holo theme use holo themed buttons
//            mTabsAdapter.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"), NoteManagerBibleNotesFragment.class, null);
//            mTabsAdapter.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"), BookmanagerBibleFragment.class, null);

            //            mTabsAdapter.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
//                LoaderCursorSupport.CursorLoaderListFragment.class, null);
//            mTabsAdapter.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"), LoaderCustomSupport.AppListFragment.class, null);
//            mTabsAdapter.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
//                LoaderThrottleSupport.ThrottledLoaderListFragment.class, null);
//        } else {
            // Use custom tab style
            setupTab(new TextView(this), AUDIO_TAG, AudioBibleFragment.class);
            setupTab(new TextView(this), BIBLE_TAG, BibleTextFragment.class);
            setupTab(new TextView(this), NOTES_TAG, NotesEditorFragment.class);
            setupTab(new TextView(this), RESOURCE_TAG, ResourceFragment.class);

            //            setupTab(new TextView(this), "Tab 3", LoaderCustomSupport.AppListFragment.class);
//            setupTab(new TextView(this), "Tab 4", LoaderThrottleSupport.ThrottledLoaderListFragment.class);
//        }

        //Set starting tab
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString(DEFAULT_TAB));
        } else {
            mTabHost.setCurrentTabByTag(BIBLE_TAG);
        }
    }
    
    /**
     * Called when tabs change to make sure it is the latest bibletext when tabs change
     */
    public void refreshFragments() {
        try {
            AudioBibleFragment audioBibleFragment = (AudioBibleFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":0");
            audioBibleFragment.updateList(bibleText);
            
            //perhaps bibltext doesn't need to be refreshed because this is where the change comes from in the first place
//            BibleTextFragment bibleTextFragment = (BibleTextFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":1");
//            bibleTextFragment.updateBibleTextView(bibleText);
            NotesEditorFragment notesFragment = (NotesEditorFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":2");
            notesFragment.updateNotes(bibleText);
            ResourceFragment resourceFragment = (ResourceFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":3");
            resourceFragment.updateResources(bibleText);

        } catch (NullPointerException e) {
            //Catch exception which occurs at start onTabChanged when loaded
        }

    }
    
    /**
     * Actually update the fragments this is called from activityResults as well as verse selection on bibletextfragment
     * @param bibleText
     */
    public void updateFragments(BibleText bibleText) {
        this.bibleText = bibleText;
        BibleTextFragment bibleTextFragment = (BibleTextFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":1");
        bibleTextFragment.updateBibleTextView(bibleText);
        NotesEditorFragment fragment = (NotesEditorFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":2");
        fragment.updateNotes(bibleText);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        //Result code from child fragments activity is bugged so need to strip off last digits
        int windowsReqCode = requestCode & 0xffff;
        if (resultCode == Activity.RESULT_OK && (requestCode == CHAPTER_SELECT_CODE || windowsReqCode == WINDOW_SELECT_CODE || requestCode == NOTES_SELECT_CODE)) {
            if (requestCode == NOTES_SELECT_CODE) {
                mTabHost.setCurrentTabByTag(NOTES_TAG);
            } else {
                mTabHost.setCurrentTabByTag(BIBLE_TAG);
            }
            updateFragments((BibleText) data.getParcelableExtra("BibleText"));
            
            if (windowsReqCode == WINDOW_SELECT_CODE) {
                windowId = data.getExtras().getInt("WindowId");
                Log.i("Main", "Window ID Received" + windowId);
            }
        }  else if (resultCode == Activity.RESULT_OK && requestCode == TRANSLATION_SELECT_CODE) {
            bibleText.setTranslation((String) data.getExtras().get("Translation"));
            updateFragments((BibleText) data.getParcelableExtra("BibleText"));
//            new SwordVerseTask().execute(bibleTextView.getBibleText());
        }
    }
    
    private void hideAllIcons() {
        ((ImageButton) findViewById(R.id.menu_button_churches)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_browse_resources)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_translations)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_windows)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_star)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_audio)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_browse_audio)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_notes)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_notes_lock)).setVisibility(View.GONE);
    }

    
    private void setupTab(final View view, final String tag, Class fragment) {
        View tabview = createTabView(mTabHost.getContext(), tag);

        mTabsAdapter.addTab(mTabHost.newTabSpec(tag).setIndicator(tabview), fragment, null);

    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEFAULT_TAB, mTabHost.getCurrentTabTag());
    }
    
    private NotesService notesService;
    
    /**
     * Notes service is on the parent activity to prevent multiple helpers being opened by fragments and thus prevents locking
     * issues and synch writes     
     * @return a notes service
     */
    public NotesService getNotesService() {
    	if (notesService == null) {
            notesService = new NotesService(MainActivity.this);
    	}
    	return notesService;
    }
    
    /**
     * Remember to close the DB when this activity is destroyed
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(TAG, "onDestroy losing DB");
    }


    /**
     * This is a helper class that implements the management of tabs and all details of connecting a ViewPager with
     * associated TabHost. It relies on a trick. Normally a tab host has a simple API for supplying a View or Intent
     * that each tab will show. This is not sufficient for switching between pages. So instead we make the content part
     * of the tab host 0dp high (it is not shown) and the TabsAdapter supplies its own dummy view to show as the tab
     * content. It listens to changes in tabs, and takes care of switch to the correct paged in the ViewPager whenever
     * the selected tab changes.
     */
    public class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;

        private final TabHost mTabHost;

        private final ViewPager mViewPager;

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        final class TabInfo {
            private final String tag;

            private final Class<?> clss;

            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
        	Log.d(TAG, "onTabChanged tabid:" + tabId + "Current mViewPager:" + mViewPager.getCurrentItem());
                NotesEditorFragment fragment = (NotesEditorFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":2");
                if ( fragment != null && fragment.isEnabled()) {
                	fragment.save();
                }
                    	
        	
            refreshFragments();
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
            
            
            hideAllIcons();
            
            List<ImageButton> showIcons;
            
            if (RESOURCE_TAG.equals(mTabHost.getCurrentTabTag())) {
                showIcons = resource_top_icons;
            } else if (BIBLE_TAG.equals(mTabHost.getCurrentTabTag())) {
                showIcons = bible_text_top_icons;
            } else if (AUDIO_TAG.equals(mTabHost.getCurrentTabTag())) {
                showIcons = audio_top_icons;
            } else {
                showIcons = notes_top_icons;
            }
            
            for (ImageButton icon : showIcons) {
                icon.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}

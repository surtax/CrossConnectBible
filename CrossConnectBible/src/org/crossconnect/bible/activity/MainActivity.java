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
package org.crossconnect.bible.activity;


import java.util.ArrayList;
import java.util.List;

import org.crossconnect.bible.activity.main.AudioBibleFragment;
import org.crossconnect.bible.activity.main.BibleTextFragment;
import org.crossconnect.bible.activity.main.NotesEditorFragment;
import org.crossconnect.bible.activity.main.ResourceFragment;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.musicplayer.MusicActivity;
import org.crossconnect.bible.service.NotesService;
import org.crossconnect.bible.service.ResourceService;
import org.crossconnect.bible.utility.SharedPreferencesHelper;
import org.crossconnect.bible.utility.Utils;

import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.crossconnect.bible.R;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI that switches between tabs and also allows
 * the user to perform horizontal flicks to move between the tabs.
 */
public class MainActivity extends FragmentActivity {
	
    private static final String TAG = "MainActivity";
	
    TabHost mTabHost;

    ViewPager mViewPager;

    TabsAdapter mTabsAdapter;
    
    Button headerTitleText;
    
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
    public static final int SETTINGS_CODE = 6;
    
    
    static List<ImageButton> resource_top_icons;
    static List<ImageButton> bible_text_top_icons;
    static List<ImageButton> audio_top_icons;
    static List<ImageButton> notes_top_icons;
    
    private BibleText bibleText;
    
    public int windowId;
    
    //The popup menu shown on icon press
    private QuickActionHorizontal mQuickAction;

    
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
//        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_tabs));
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_windows));

        audio_top_icons = new ArrayList<ImageButton>();
        audio_top_icons.add((ImageButton) findViewById(R.id.menu_button_audio));
//        audio_top_icons.add((ImageButton) findViewById(R.id.menu_button_browse_audio));

        notes_top_icons = new ArrayList<ImageButton>();
        notes_top_icons.add((ImageButton) findViewById(R.id.menu_button_notes));
        notes_top_icons.add((ImageButton) findViewById(R.id.menu_button_notes_lock));
        
        hideAllIcons();
        
        //Load last opened verse
        bibleText = Utils.loadBibleText(getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE));

        
        //Setup listeners for top menu icons

        //Go to notes browsing view
        ((ImageButton) findViewById(R.id.menu_button_notes)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	saveCurrentTab();
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
                startActivityForResult(intent, NOTES_SELECT_CODE);    
            }
        });        

        ((ImageButton) findViewById(R.id.menu_button_churches)).setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ResourceRepositoryActivity.class);
                    startActivity(intent);    
                }
        });
        
//        ((ImageButton) findViewById(R.id.menu_button_browse_audio)).setOnClickListener(new OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ResourceRepositoryActivity.class);
//                startActivity(intent);    
//            }
//        });

        
        ((ImageButton) findViewById(R.id.menu_button_audio)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putExtra("Book", bibleText.getBook());
                intent.putExtra("Chapter", bibleText.getChapter());
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
                startActivity(intent);    
            }
        });        
        
        ((ImageButton) findViewById(R.id.menu_button_browse_resources)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putExtra("Book", bibleText.getBook());
                intent.putExtra("Chapter", bibleText.getChapter());
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
                startActivity(intent);    
            }
        });
        
        headerTitleText = (Button) findViewById(R.id.header_title); 
    	headerTitleText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "Ubuntu-R.ttf"),Typeface.NORMAL);

        headerTitleText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	
            	saveCurrentTab();
                
                //TODO: can we pass the actual BibleText?
                Intent intent = new Intent(MainActivity.this, ChapterSelectionActivity.class);
                intent.putExtra("Book", bibleText.getBook());
                intent.putExtra("Chapter", bibleText.getChapter());
                intent.putExtra("Translation", bibleText.getTranslation().getInitials());
                startActivityForResult(intent, CHAPTER_SELECT_CODE);
            }
        });
        
        
        final ActionItem settingsAction = new ActionItem();
        
        settingsAction.setTitle("Settings");
        settingsAction.setIcon(getResources().getDrawable(R.drawable.icon_gear));

//        final ActionItem accAction = new ActionItem();
//        
//        accAction.setTitle("Share");
//        accAction.setIcon(getResources().getDrawable(R.drawable.icon_gear));
//        
//        final ActionItem upAction = new ActionItem();
//        
//        upAction.setTitle("Star");
//        upAction.setIcon(getResources().getDrawable(R.drawable.icon_support));

        
        findViewById(R.id.title_bar_icon).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        mQuickAction  = new QuickActionHorizontal(findViewById(R.id.title_bar_icon));
		        
		        settingsAction.setOnClickListener(new OnClickListener() {
		            
		            //Copy text action item
		            @Override
		            public void onClick(View v) {
		                startActivityForResult(new Intent(MainActivity.this, PreferencesFromXml.class), SETTINGS_CODE);
		                mQuickAction.dismiss();
		            }
		        });
		
//		        accAction.setOnClickListener(new OnClickListener() {
//		            @Override
//		            public void onClick(View v) {
//		                mQuickAction.dismiss();
//		            }
//		        });
//		        
//		        upAction.setOnClickListener(new OnClickListener() {
//		            @Override
//		            public void onClick(View v) {
//		                mQuickAction.dismiss();
//		            }
//		        });
		        
		        mQuickAction.addActionItem(settingsAction);
//		        mQuickAction.addActionItem(accAction);
//		        mQuickAction.addActionItem(upAction);
		        
		        mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
		        
		        mQuickAction.setOnDismissListener(new OnDismissListener() {
		            @Override
		            public void onDismiss() {
		            }
		        });
		        mQuickAction.show();
		}});

        
        


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
            setupTab(new TextView(this), AUDIO_TAG, R.drawable.ico_audio, AudioBibleFragment.class);
            setupTab(new TextView(this), BIBLE_TAG, R.drawable.ico_bible, BibleTextFragment.class);
            setupTab(new TextView(this), NOTES_TAG, R.drawable.ico_notes, NotesEditorFragment.class);
            setupTab(new TextView(this), RESOURCE_TAG, R.drawable.ic_action_microphone, ResourceFragment.class);

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
            if (RESOURCE_TAG.equals(mTabHost.getCurrentTabTag())) {
                ResourceFragment resourceFragment = (ResourceFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":3");
                if (resourceFragment != null)
                	resourceFragment.updateResources(bibleText);
            } else if (BIBLE_TAG.equals(mTabHost.getCurrentTabTag())) {
                BibleTextFragment bibleTextFragment = (BibleTextFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":1");
                if (bibleTextFragment != null)
                	bibleTextFragment.updateBibleTextView(bibleText);
            } else if (AUDIO_TAG.equals(mTabHost.getCurrentTabTag())) {
                AudioBibleFragment audioBibleFragment = (AudioBibleFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":0");
                if (audioBibleFragment != null)
                	audioBibleFragment.updateList(bibleText);
            } else {
                NotesEditorFragment notesFragment = (NotesEditorFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":2");
                if (notesFragment != null)
                	notesFragment.updateNotes(bibleText);
            }
    }
    
    public void updateHeaderText(String text) {
    	headerTitleText.setText(text);
    }
    
    /**
     * Actually update the fragments this is called from activityResults as well as verse selection on bibletextfragment
     * @param bibleText
     */
    public void updateBibleText(BibleText bibleText) {
        this.bibleText = bibleText;

    	//Update the header text
    	updateHeaderText(bibleText.getDisplayReferenceHeader());

    	// Get verse number if on bibletext fragment
        BibleTextFragment bibleTextFragment = (BibleTextFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":1");
        int verseNumber = 1;
        try {
        	verseNumber = bibleTextFragment.getVerseNumber();
        } catch (Exception e) {
        	
        }
        
        //Save the current passage so it can be reloaded
        Utils.saveCurrentPassage(bibleText, getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE), verseNumber);
        
        //Refresh fragments with new value
        refreshFragments();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() ResultCode" + resultCode + " RequestCode:" + requestCode);
        //Result code from child fragments activity is bugged so need to strip off last digits
        int windowsReqCode = requestCode & 0xffff;
        if (resultCode == Activity.RESULT_OK && (requestCode == CHAPTER_SELECT_CODE || windowsReqCode == WINDOW_SELECT_CODE || requestCode == NOTES_SELECT_CODE)) {
        	updateBibleText((BibleText) data.getParcelableExtra("BibleText"));
        	
            if (windowsReqCode == WINDOW_SELECT_CODE) {
                windowId = data.getExtras().getInt("WindowId");
                Log.i("Main", "Window ID Received" + windowId);
                //If window select then default go back to BIBLE_TAG column
                mTabHost.setCurrentTabByTag(BIBLE_TAG);
            } else {
            	//Everything else go back to original tab
            	mTabHost.setCurrentTabByTag(getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE).getString(SharedPreferencesHelper.CURRENT_TAB, BIBLE_TAG));
            }
            
        }  else if (resultCode == Activity.RESULT_OK && requestCode == TRANSLATION_SELECT_CODE) {
            bibleText.setTranslation((String) data.getExtras().get("Translation"));
            updateBibleText((BibleText) data.getParcelableExtra("BibleText"));
//            new SwordVerseTask().execute(bibleTextView.getBibleText());
        } else if (requestCode == SETTINGS_CODE) {
        	updateBibleText(bibleText);
        } else if (requestCode == NOTES_SELECT_CODE && resultCode == Activity.RESULT_CANCELED) {
        	//refresh if back is hit because note may have been deleted
        	updateBibleText(bibleText);
        }
    } 
    
    private void hideAllIcons() {
        ((ImageButton) findViewById(R.id.menu_button_churches)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_browse_resources)).setVisibility(View.GONE);
//        ((ImageButton) findViewById(R.id.menu_button_tabs)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_windows)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_audio)).setVisibility(View.GONE);
//        ((ImageButton) findViewById(R.id.menu_button_browse_audio)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_notes)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.menu_button_notes_lock)).setVisibility(View.GONE);
    }

    
    private void setupTab(final View view, final String tag, int iconDrawable, Class<?> fragment) {
        View tabview = createTabView(mTabHost.getContext(), tag, iconDrawable);

        mTabsAdapter.addTab(mTabHost.newTabSpec(tag).setIndicator(tabview), fragment, null);

    }

    private static View createTabView(final Context context, final String text, int iconDrawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        ImageView iv = (ImageView) view.findViewById(R.id.tabsIcon);
        iv.setImageResource(iconDrawable);
        tv.setText(text);
        return view;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEFAULT_TAB, mTabHost.getCurrentTabTag());
    }
    
    private void saveCurrentTab() {
    	Editor editor = getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE).edit();
    	editor.putString(SharedPreferencesHelper.CURRENT_TAB, mTabHost.getCurrentTabTag());
    	editor.commit();
    }
    
    private NotesService notesService;
    private ResourceService resourceService;
    
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
     * Resource service is on the parent activity to prevent multiple helpers being opened by fragments and thus prevents locking
     * issues and synch writes     
     * @return a resource service
     */
    public ResourceService getResourceService() {
    	if (resourceService == null) {
    		resourceService = new ResourceService(MainActivity.this);
    	}
    	return resourceService;
    }

    
    /**
     * Remember to close the DB when this activity is destroyed
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if (resourceService != null) {
    		resourceService.close();
    	}
    	
    	if (notesService != null) {
    		notesService.close();
    	}

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

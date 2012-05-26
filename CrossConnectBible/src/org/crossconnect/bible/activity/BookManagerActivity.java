package org.crossconnect.bible.activity;

import org.crossconnect.bible.activity.bookmanager.BookmanagerBibleFragment;
import org.crossconnect.bible.activity.bookmanager.BookmanagerCommentaryFragment;
import org.crossconnect.bible.activity.bookmanager.BookmanagerInstalledFragment;
import org.crossconnect.bible.activity.main.AudioBibleFragment;
import org.crossconnect.bible.activity.main.BibleTextFragment;
import org.crossconnect.bible.activity.main.NotesEditorFragment;
import org.crossconnect.bible.activity.main.ResourceFragment;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.swipeytabs.SwipeyTabFragment;
import org.crossconnect.bible.swipeytabs.SwipeyTabs;
import org.crossconnect.bible.swipeytabs.SwipeyTabsAdapter;
import org.crossconnect.bible.util.RequestResultCodes;
import org.crossconnect.bible.utility.SharedPreferencesHelper;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.crossconnect.bible.R;

public class BookManagerActivity extends FragmentActivity {

    private static final String[] TITLES = { "INSTALLED", "BIBLES", "COMMENTARIES", };

    private SwipeyTabs mTabs;

    private ViewPager mViewPager;

    private SwipeyTabsPagerAdapter adapter;
    
    private static final String TAG = "BookManagerActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_manager);

        mViewPager = (ViewPager) findViewById(R.id.book_manage_pager);
        mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

        adapter = new SwipeyTabsPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabs.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mTabs);
        mViewPager.setCurrentItem(0);
        
		ImageView up = (ImageView) findViewById(R.id.title_bar_icon);
		up.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
  	  	        Intent i = new Intent(BookManagerActivity.this, MainActivity.class);
  	  	        startActivity(i);
			}

		});

    }

    public Fragment getFragment(int position) {
        return adapter.getItem(position);
    }

    private class SwipeyTabsPagerAdapter extends FragmentPagerAdapter implements SwipeyTabsAdapter {

        private final Context mContext;

        public SwipeyTabsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);

            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new BookmanagerInstalledFragment();
            }

            if (position == 1) {
                return new BookmanagerBibleFragment();
            }

            if (position == 2) {
                return new BookmanagerCommentaryFragment();
            }

            return SwipeyTabFragment.newInstance(TITLES[position]);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        public TextView getTab(final int position, SwipeyTabs root) {
            TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.swipey_tab_indicator, root, false);
            view.setText(TITLES[position]);
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mViewPager.setCurrentItem(position);
                }
            });

            return view;
        }

    }
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() ResultCode" + resultCode + " RequestCode:" + requestCode);
        //Result code from child fragments activity is bugged so need to strip off last digits
        int subFragmentReqCode = requestCode & 0xffff;
        
    	if (subFragmentReqCode == RequestResultCodes.DOWNLOAD_REQUEST) {
    		if (resultCode == Activity.RESULT_OK) {
    			//SUCCESSFUL DOWNLOAD refresh and set to installed tab
    	    	Log.i("BookManagerActivity", "Sucessful Download");
            	refreshFragments();
            	mViewPager.setCurrentItem(0);
    	    	
    		} else {
    	    	Log.e("BookManagerActivity", "Unssucessful Download");
    		}
    	}

    } 
    
	/**
	 * Called when tabs change to make sure it is the latest bibletext when tabs
	 * change
	 */
	public void refreshFragments() {
		BookmanagerInstalledFragment installedFragment = (BookmanagerInstalledFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.book_manage_pager + ":0");
		if (installedFragment != null)
			installedFragment.refresh();

	}


}

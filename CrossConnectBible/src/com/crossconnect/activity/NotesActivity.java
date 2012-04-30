package com.crossconnect.activity;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.crossconnect.activity.bookmanager.BookmanagerBibleFragment;
import com.crossconnect.activity.bookmanager.BookmanagerCommentaryFragment;
import com.crossconnect.activity.bookmanager.BookmanagerInstalledFragment;
import com.crossconnect.activity.notemanager.NoteManagerBibleNotesFragment;
import com.crossconnect.activity.notemanager.NoteManagerPersonalNotesFragment;
import com.crossconnect.swipeytabs.SwipeyTabFragment;
import com.crossconnect.swipeytabs.SwipeyTabs;
import com.crossconnect.swipeytabs.SwipeyTabsAdapter;
import com.crossconnect.util.MifareUltralightTagTester;
import com.crossconnect.actions.R;

public class NotesActivity extends FragmentActivity {

	private static final String [] TITLES = {
		"BIBLE",
		"PERSONAL",
	};

	private SwipeyTabs mTabs;
	private ViewPager mViewPager;
	private SwipeyTabsPagerAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notes_manager);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

		adapter = new SwipeyTabsPagerAdapter(this,
				getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mTabs.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(mTabs);
		mViewPager.setCurrentItem(0);
	}
	
	public Fragment getFragment(int position) {
		return adapter.getItem(position);
	}

	private class SwipeyTabsPagerAdapter extends FragmentPagerAdapter implements
			SwipeyTabsAdapter {
		
		private final Context mContext;

		public SwipeyTabsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);

			this.mContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new NoteManagerBibleNotesFragment();
			} else if (position == 1) {
				return new NoteManagerPersonalNotesFragment();				
			}
			return SwipeyTabFragment.newInstance(TITLES[position]);
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		public TextView getTab(final int position, SwipeyTabs root) {
			TextView view = (TextView) LayoutInflater.from(mContext).inflate(
					R.layout.swipey_tab_indicator, root, false);
			view.setText(TITLES[position]);
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mViewPager.setCurrentItem(position);
				}
			});
			
			return view;
		}

	}

}

    

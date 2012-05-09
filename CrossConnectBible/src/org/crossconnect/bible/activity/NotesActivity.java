package org.crossconnect.bible.activity;

import java.util.ArrayList;
import java.util.List;

import org.crossconnect.bible.R;
import org.crossconnect.bible.activity.notemanager.NoteManagerBibleNotesFragment;
import org.crossconnect.bible.activity.notemanager.NoteManagerPersonalNotesFragment;
import org.crossconnect.bible.activity.notemanager.PersonalNotesActivity;
import org.crossconnect.bible.swipeytabs.SwipeyTabFragment;
import org.crossconnect.bible.swipeytabs.SwipeyTabs;
import org.crossconnect.bible.swipeytabs.SwipeyTabsActionBarIcons;
import org.crossconnect.bible.swipeytabs.SwipeyTabsAdapter;

import android.content.Context;
import android.content.Intent;
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

public class NotesActivity extends FragmentActivity {

	private static final String [] TITLES = {
		"BIBLE",
		"PERSONAL",
	};

	private SwipeyTabsActionBarIcons mTabs;
	private ViewPager mViewPager;
	private SwipeyTabsPagerAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notes_manager);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabs = (SwipeyTabsActionBarIcons) findViewById(R.id.swipeytabs);
		View actionButton = findViewById(R.id.menu_button_edit_document);
		actionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	            Intent intent = new Intent(NotesActivity.this, PersonalNotesActivity.class);
	    		startActivity(intent);
			}});
		List<View> col1Icons = new ArrayList<View>();
		col1Icons.add(actionButton);
		mTabs.addIcons(PERSONAL_NOTES, col1Icons);
		
		adapter = new SwipeyTabsPagerAdapter(this,
				getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mTabs.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(mTabs);
		mViewPager.setCurrentItem(BIBLE_NOTES);
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
			if (position == BIBLE_NOTES) {
				return new NoteManagerBibleNotesFragment();
			} else if (position == PERSONAL_NOTES) {
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
	
    private static final int BIBLE_NOTES = 0;
    private static final int PERSONAL_NOTES = 1;
    
}

    

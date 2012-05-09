package org.crossconnect.bible.activity;

import org.crossconnect.bible.activity.bookmanager.BookmanagerBibleFragment;
import org.crossconnect.bible.activity.bookmanager.BookmanagerCommentaryFragment;
import org.crossconnect.bible.activity.bookmanager.BookmanagerInstalledFragment;
import org.crossconnect.bible.swipeytabs.SwipeyTabFragment;
import org.crossconnect.bible.swipeytabs.SwipeyTabs;
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
import android.widget.ImageView;
import android.widget.TextView;

import org.crossconnect.bible.actions.R;

public class BookManagerActivity extends FragmentActivity {

    private static final String[] TITLES = { "INSTALLED", "BIBLES", "COMMENTARIES", };

    private SwipeyTabs mTabs;

    private ViewPager mViewPager;

    private SwipeyTabsPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_manager);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
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

}

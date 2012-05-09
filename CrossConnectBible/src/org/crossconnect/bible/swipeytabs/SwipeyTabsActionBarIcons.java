/*
 * Copyright 2011 Peter Kuterna
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

package org.crossconnect.bible.swipeytabs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crossconnect.bible.actions.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SwipeyTabsActionBarIcons extends SwipeyTabs {

	public SwipeyTabsActionBarIcons(Context context) {
		this(context, null);
	}

	public SwipeyTabsActionBarIcons(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeyTabsActionBarIcons(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	
	}
	
	private Map<Integer, List<View>> actionBarIconsMap = new HashMap<Integer, List<View>>();
	
	public void addIcons(int column, List<View> actionBarIcons) {
		actionBarIconsMap.put(column, actionBarIcons);
	}

	@Override
	public void onPageSelected(int position) {
		super.onPageSelected(position);
		hideAllIcons();
		
		//Show the new icons
		List<View> views = actionBarIconsMap.get(position);
		if (views != null) {
			for (View view : views) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void hideAllIcons() {
		for (List<View> views : actionBarIconsMap.values()) {
			for (View view : views) {
				view.setVisibility(View.GONE);
			}
		}
	}
}

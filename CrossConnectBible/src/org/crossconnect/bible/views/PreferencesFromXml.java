/*
 * Copyright (C) 2007 The Android Open Source Project
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

package org.crossconnect.bible.views;

import org.crossconnect.bible.views.ColorPickerDialog.OnColorChangedListener;

import org.crossconnect.bible.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class PreferencesFromXml extends PreferenceActivity {

	Preference colorPreference = null;
	ListPreference bookPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		final Context myContext = this;
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		
		colorPreference = getPreferenceScreen().findPreference("color");
		colorPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				new ColorPickerDialog(myContext, colorListener , Color.BLACK).show();
				return false;
			}
			
		});


	}
	
	private OnColorChangedListener colorListener = new OnColorChangedListener(){
		
	public void colorChanged(int color) {
		SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		editor.putInt(getApplicationContext().getString(R.string.text_color_key), color);
		editor.commit();
	}
	};

}

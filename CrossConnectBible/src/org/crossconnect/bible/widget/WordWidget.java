/*
 * Copyright (C) 2009 The Android Open Source Project
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

package org.crossconnect.bible.widget;

import org.crossconnect.bible.activity.MainActivity;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;

import net.sword.engine.sword.SwordContentFacade;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import org.crossconnect.bible.R;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class WordWidget extends AppWidgetProvider {
	public static class UpdateService extends Service {
		/**
		 * Build a widget update to show the current Wiktionary "Word of the
		 * day." Will block until the online API returns.
		 */
		public RemoteViews buildUpdate(Context context) {

			// Find current month and day
			Time today = new Time();
			today.setToNow();

			// Build the page titleView for today, such as "March 21"
			String widgetTitle = null;
			SpannableStringBuilder widgetText = null;
			
			//TODO: Get form local list rather than static
			BibleText bibleText = new SwordBibleText("John",3,"ESV");
			SwordContentFacade.getInstance().injectChapterFromJsword(bibleText); 


			try {
				// Get daily bible verse
				widgetText = bibleText.getSpannableStringBuilder();
				widgetTitle = bibleText.getReferenceBookChapterVerse();
			} catch (Exception e) {
				Log.e("WordWidget", "Couldn't contact API", e);
				widgetText = null;
			}

			RemoteViews views = null;

			//Generate layout
			if (widgetText != null && widgetText.length() > 0 && widgetTitle.length() > 0) {
				// Build an update that holds the updated widget contents
				views = new RemoteViews(context.getPackageName(), R.layout.widget_word);

				// String wordTitle = matcher.group(1);
				views.setTextViewText(R.id.word_title, widgetTitle);
				// views.setTextViewText(R.id.word_type, matcher.group(2));
				views.setTextViewText(R.id.definition, widgetText);

				String definePage = "";//String.format("%s://%s/%s", ESVApiHelper.ESV_AUTHORITY, ESVApiHelper.WIKI_LOOKUP_HOST, pageName);
				Intent defineIntent = new Intent(this, MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, 0 /* no flags */);
				views.setOnClickPendingIntent(R.id.definition, pendingIntent);
				defineIntent = new Intent(Intent.ACTION_RUN, Uri.parse(definePage));
				pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, 0 /* no flags */);
				views.setOnClickPendingIntent(R.id.icon, pendingIntent);

				//Hide the refresh button and set searh icon visible
				views.setViewVisibility(R.id.icon, View.VISIBLE);
				views.setViewVisibility(R.id.refresh, View.GONE);

			} else {
				// Didn't find word of day, so show error message with refresh
				views = new RemoteViews(context.getPackageName(), R.layout.widget_word);

				views.setTextViewText(R.id.word_title, "Oops sorry");
				views.setTextViewText(R.id.definition, context.getString(R.string.widget_error));

				String definePage = "";//String.format("%s://%s/%s", ESVApiHelper.ESV_AUTHORITY, ESVApiHelper.WIKI_LOOKUP_HOST, pageName);
				Intent defineIntent = new Intent(this, MainActivity.class);

				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, 0 /* no flags */);
				views.setOnClickPendingIntent(R.id.definition, pendingIntent);

				//Hide search icon and show refresh icon
				views.setViewVisibility(R.id.icon, View.GONE);
				views.setViewVisibility(R.id.refresh, View.VISIBLE);

				//Refresh all widget intent
				pendingIntent = PendingIntent.getBroadcast(context, 0, // no requestCode 
						new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE), 0 // no flags 
						);

				//Refreshes when the button is clicked
				views.setOnClickPendingIntent(R.id.refresh, pendingIntent);

			}
			return views;
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		@Override
		public void onStart(Intent intent, int startId) {
			// Build the widget update for today
			RemoteViews updateViews = buildUpdate(this);

			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(this, WordWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(thisWidget, updateViews);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// To prevent any ANR timeouts, we perform the update in a service
		context.startService(new Intent(context, UpdateService.class));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		context.startService(new Intent(context, UpdateService.class));

	}
}

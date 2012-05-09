package org.crossconnect.bible.service;

import org.crossconnect.bible.database.DatabaseHelper;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.SpannableStringBuilder;
import android.util.Log;



/**
 * Service class to retreive org.crossconnect.bible.database
 */
public class MainService {
	DatabaseHelper databaseHelper;
	Context ctx;
	SQLiteDatabase db;
	
	public MainService(Context ctx) {
		this.ctx = ctx;
		databaseHelper = new DatabaseHelper(ctx);
		db = databaseHelper.getWritableDatabase();
	}
	
	/**
	 * Get current bibletext from org.crossconnect.bible.database
	 * @return
	 */
	public BibleText getCurrentFromDB(int window) {
		
		BibleText bibleText = null;
		
		Cursor cursor = db.query(DatabaseHelper.WINDOWS_TABLE_NAME, null, null, null, null, null, null);
		
		if (cursor.getCount() > 0) {
			// Windows exist move to correct one
			cursor.moveToPosition(window);			
			int id = cursor.getInt(0);
			//			String refTxt = cursor.getString(1);
			String book = cursor.getString(2);
			int chapter = cursor.getInt(3);
			String text = cursor.getString(4);
//			String title = cursor.getString(5);
			String translation = cursor.getString(6);
//			int verse = cursor.getInt(7);
			Log.i("MainService", "Get from DB ID:" + id + " book" + book);
			
			bibleText = new SwordBibleText(book, chapter, -1, translation);
			bibleText.setSpannableStringBuilder(new SpannableStringBuilder(text));

		} else {
			//No windows should handle it here
		}
		
		cursor.close();
		return bibleText;
	}

	

}

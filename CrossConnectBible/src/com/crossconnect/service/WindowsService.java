package com.crossconnect.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.SpannableStringBuilder;
import android.util.Log;

import com.crossconnect.model.BibleText;
import com.crossconnect.model.SwordBibleText;
import com.crossconnect.model.Window;
import com.crossconnect.model.WindowImpl;

import database.DatabaseHelper;

/**
 * A service used to handle window creation and database management
 * 
 * Windows are always verse references not chapter references
 * 
 * @author garylo
 *
 */
public class WindowsService {
	
	DatabaseHelper databaseHelper;
	Context ctx;
	SQLiteDatabase db;
	
	public WindowsService(Context ctx) {
		this.ctx = ctx;
		databaseHelper = new DatabaseHelper(ctx);
		db = databaseHelper.getWritableDatabase();
	}
	
	public List<Window> getWindows() {
		return getWindowsFromDB();
	}
	
	private void saveNewWindow(Window window) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.ID, window.getId());
		values.put(DatabaseHelper.REF_TXT_COL, window.getBibleText().getReferenceBookChapterVersion());
		values.put(DatabaseHelper.BOOK_COL,window.getBibleText().getBook());
		values.put(DatabaseHelper.CHAPTER_COL,window.getBibleText().getChapter());
		values.put(DatabaseHelper.TEXT_COL,window.getBibleText().getPreview().toString());
		values.put(DatabaseHelper.TITLE_COL,window.getBibleText().getReferenceBookChapter());
		values.put(DatabaseHelper.TRANSLATION_COL,window.getBibleText().getTranslation().getInitials());
		values.put(DatabaseHelper.VERSE_COL,window.getBibleText().getVerse());		
		db.insert(DatabaseHelper.WINDOWS_TABLE_NAME, null, values);
	}
	
	public void updateWindow(Window window) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.ID, window.getId());
		values.put(DatabaseHelper.REF_TXT_COL, window.getBibleText().getReferenceBookChapterVersion());
		values.put(DatabaseHelper.BOOK_COL,window.getBibleText().getBook());
		values.put(DatabaseHelper.CHAPTER_COL,window.getBibleText().getChapter());
		values.put(DatabaseHelper.TEXT_COL,window.getBibleText().getPreview().toString());
		values.put(DatabaseHelper.TITLE_COL,window.getBibleText().getReferenceBookChapter());
		values.put(DatabaseHelper.TRANSLATION_COL,window.getBibleText().getTranslation().getInitials());
		values.put(DatabaseHelper.VERSE_COL,window.getBibleText().getVerse());
		String where = DatabaseHelper.ID + "= ?";
		String[] whereArgs = {String.valueOf(window.getId())};
		db.update(DatabaseHelper.WINDOWS_TABLE_NAME, values, where, whereArgs);
	}


	public void removeWindow(Window window) {
		String where = DatabaseHelper.ID + "= ?";
		String[] whereArgs = {window.getId() + ""};
		db.delete(DatabaseHelper.WINDOWS_TABLE_NAME, where, whereArgs);
		
//		db.execSQL("DELETE FROM " + DatabaseHelper.WINDOWS_TABLE_NAME + " where Id = " + window.getId());
	}
	
	/**
	 * Get a list of windows from the database
	 * @return
	 */
	public List<Window> getWindowsFromDB() {
		Cursor cursor = db.query(DatabaseHelper.WINDOWS_TABLE_NAME, null, null, null, null, null, null);
		
		List<Window> windows = new ArrayList<Window>();
		
		while(cursor.moveToNext()){
			int id = cursor.getInt(0);
			//			String refTxt = cursor.getString(1);
			String book = cursor.getString(2);
			int chapter = cursor.getInt(3);
			String text = cursor.getString(4);
//			String title = cursor.getString(5);
			String translation = cursor.getString(6);
			int verse = cursor.getInt(7);
			
			Log.i("WindowsService", "Get from DB ID:" + id + " book" + book);
			
			BibleText bibleText = new SwordBibleText(book, chapter, verse, translation);
			bibleText.setSpannableStringBuilder(new SpannableStringBuilder(text));
			windows.add(restoreWindow(bibleText, id));
		}
		cursor.close();
		return windows;
	}
	
	public Window getNewWindow(BibleText bibleText) {
		Window window = new WindowImpl(bibleText, getNewId());
		saveNewWindow(window);
		return window;
	}
	
	/**
	 * Restore an existing window usually from database
	 * @param bibleText
	 * @param id
	 * @return
	 */
	public Window restoreWindow(BibleText bibleText, int id) {
		Window window = new WindowImpl(bibleText, id);				;
		return window;
	}

	
	private int getNewId() {
		List<Integer> ids = new ArrayList<Integer>();
		for (Window window : getWindowsFromDB()){
			Log.i("WindowsService", "IDS From DB: " + window.getId());
			ids.add(window.getId());
		}
		
		int i = 0;
		while(ids.contains(i)) {
			i++;
		}
		Log.i("NEW ID", i +"");
		return i;
		
	}
	
	/**
	 * Create a new window but does not yet save to the database 
	 * needs to be careful because if two of these are created at one time they will clash 
	 * when saving
	 * @return
	 */
	public Window getEmptyWindow() {
		Window window = new WindowImpl(getNewId());
		return window;
	}



	public void close() {
		db.close();
	}
	
}

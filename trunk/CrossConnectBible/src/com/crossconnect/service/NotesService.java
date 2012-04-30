package com.crossconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.passage.Key;

import com.crossconnect.model.Note;

import utility.Utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import database.DatabaseHelper;

/**
 * A service used to handle window creation and database management
 * 
 * Windows are always verse references not chapter references
 * 
 * @author garylo
 *
 */
public class NotesService {
	
	DatabaseHelper databaseHelper;
	Context ctx;
	SQLiteDatabase db;
	
	public NotesService(Context ctx) {
		this.ctx = ctx;
		databaseHelper = new DatabaseHelper(ctx);
		db = databaseHelper.getWritableDatabase();
	}
	
	/**
	 * Get a single notea given id
	 * @param key
	 * @return
	 */
	public String getNotes(Key key) {
		return getAllNotesOfKey(key);
	}
	
	/**
	 * Insert or update a note given a key and the note variables
	 * @param key
	 * @param note
	 * @param references
	 * @param labels
	 */
	public void insertUpdate(Key key, String note, List<String> references, List<String> labels) {
		Log.i("NotesService", "InsertUpdate note");
		if (getAllNotesOfKey(key).length() == 0) {
			saveNewNote(key, note);
		} else {
			updateNote(key, note);
		}
//		removeInsertReferences(key, references);
		removeInsertLabels(key, labels);
		
	}
	
	private void removeInsertLabels(Key key, List<String> labels) {
		String where = DatabaseHelper.LABEL_BOOK_COL + "= ? and " + DatabaseHelper.LABEL_CHAPTER_COL + "= ?";
		String[] whereArgs = {String.valueOf(Utils.getBook(key)), String.valueOf(Utils.getChapter(key))};
//		Log.d("NotesSerice", "Labels whereargs" + String.valueOf(Utils.getBook(key)) + "," + String.valueOf(Utils.getChapter(key));
		try {
			db.delete(DatabaseHelper.REF_TABLE_NAME, where, whereArgs);
		} catch (Exception e) {
			
		}
		
		for (String label : labels) {
			Log.d("NotesSerice", "Save new label" + key.getName() + ":" + label);
			ContentValues values = new ContentValues();
			//TODO: make this increment to allow for multiple ids on the same chapter
			values.put(DatabaseHelper.LABEL_BOOK_COL, Utils.getBook(key));
			values.put(DatabaseHelper.LABEL_CHAPTER_COL, Utils.getChapter(key));
			values.put(DatabaseHelper.LABEL_LABEL_COL, label);
			db.insert(DatabaseHelper.LABEL_TABLE_NAME, null, values);
		}
	}

	private void removeInsertReferences(Key key, List<String> references) {
		String where = DatabaseHelper.REF_BOOK_COL + "= ? and " + DatabaseHelper.REF_CHAPTER_COL + "= ?";
		String[] whereArgs = {String.valueOf(Utils.getBook(key)), String.valueOf(Utils.getChapter(key))};
		db.delete(DatabaseHelper.REF_TABLE_NAME, where, whereArgs);
		
		for (String reference : references) {
			Log.d("NotesSerice", "Save new label" + key.getName() + ":" + reference);
			ContentValues values = new ContentValues();
			//TODO: make this increment to allow for multiple ids on the same chapter
			values.put(DatabaseHelper.REF_BOOK_COL, Utils.getBook(key));
			values.put(DatabaseHelper.REF_CHAPTER_COL, Utils.getChapter(key));
			values.put(DatabaseHelper.REF_REF_COL, reference);
			db.insert(DatabaseHelper.REF_TABLE_NAME, null, values);
		}
	}

	/**
	 * Insert the new note into the database
	 * @param key
	 * @param note
	 */
	private void saveNewNote(Key key, String note) {
		Log.d("NotesSerice", "Save new notes" + key.getName() + ":" + note);
		
		//Only save if note is of length
		if (note.length() > 0 ) {
			ContentValues values = new ContentValues();
			//TODO: make this increment to allow for multiple ids on the same chapter
			values.put(DatabaseHelper.NOTES_ID_COL, 0);
			values.put(DatabaseHelper.NOTES_BOOK_COL, Utils.getBook(key));
			values.put(DatabaseHelper.NOTES_CHAPTER_COL, Utils.getChapter(key));
			values.put(DatabaseHelper.NOTES_TEXT_COL, note);
			db.insert(DatabaseHelper.NOTES_TABLE_NAME, null, values);
		}
	}
	
	/**
	 * Update existing note to the database
	 * @param key
	 * @param note
	 */
	public void updateNote(Key key, String note) {
		Log.d("NotesSerice", "Update new notes" + key.getName() + ":" + note);
		if (note == null || note.length() == 0) {
			removeNote(key);
		} else {
			ContentValues values = new ContentValues();
			//TODO: make this increment to allow for multiple ids on the same chapter
			values.put(DatabaseHelper.NOTES_ID_COL, 0);
			values.put(DatabaseHelper.NOTES_BOOK_COL, Utils.getBook(key));
			values.put(DatabaseHelper.NOTES_CHAPTER_COL, Utils.getChapter(key));
			values.put(DatabaseHelper.NOTES_TEXT_COL, note);
			String where = DatabaseHelper.NOTES_BOOK_COL + "= ? and " + DatabaseHelper.NOTES_CHAPTER_COL + "= ?";
			String[] whereArgs = {String.valueOf(Utils.getBook(key)), String.valueOf(Utils.getChapter(key))};
			db.update(DatabaseHelper.NOTES_TABLE_NAME, values, where, whereArgs);
		}
	}


	/**
	 * Delete a note given a key
	 * @param key
	 */
	public void removeNote(Key key) {
		String where = DatabaseHelper.NOTES_BOOK_COL + "= ? and " + DatabaseHelper.NOTES_CHAPTER_COL + "= ?";
		String[] whereArgs = {String.valueOf(Utils.getBook(key)), String.valueOf(Utils.getChapter(key))};
		db.delete(DatabaseHelper.NOTES_TABLE_NAME, where, whereArgs);
	}
	
	/**
	 * Delete a note given a key
	 * @param key
	 */
	public void removeNote(int id) {
		String where = DatabaseHelper.NOTES_ID_COL + "= ? ";
		String[] whereArgs = {String.valueOf(id)};
		db.delete(DatabaseHelper.NOTES_TABLE_NAME, where, whereArgs);
	}

	
	/**
	 * Get a list of notes from the database given a key
	 * @param key 
	 * @return
	 */
	public String getAllNotesOfKey(Key key) {
		String where = DatabaseHelper.NOTES_BOOK_COL + "='" + Utils.getBook(key) + "' and " + DatabaseHelper.NOTES_CHAPTER_COL + "= " + Utils.getChapter(key);
		Log.e("NotesService", "where:" + where);
		Cursor cursor = db.query(DatabaseHelper.NOTES_TABLE_NAME, null, where, null, null, null, null);
		String text = "";
		while(cursor.moveToNext()){
			text += cursor.getString(3);
			//should add divider between them - store as seperate notes but present as singular
			Log.e("NotesService", "Get Notes from DB id:" + key.toString() + "  Note" + text);
		}
		cursor.close();
		return text;
	}
	
	public List<Note> getAllNotesFromDB() {
		//should make it sort by reference
		Cursor cursor = db.query(DatabaseHelper.NOTES_TABLE_NAME, null, null, null, null, null, DatabaseHelper.BOOK_COL);
		List<Note> notesList = new ArrayList<Note>();
		while(cursor.moveToNext()){
			notesList.add(new Note(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
			Log.e("NotesService", "Get Notes from DB" + cursor.getString(3) + " Ref:" + cursor.getString(1) + ":" + cursor.getInt(2));
		}
		cursor.close();
		return notesList;
	}
	
	public List<Note> getAllLabelsFromDB() {
		//should make it sort by reference
		Cursor cursor = db.query(DatabaseHelper.LABEL_TABLE_NAME, null, null, null, null, null, DatabaseHelper.LABEL_LABEL_COL);
		List<Note> notesList = new ArrayList<Note>();
		while(cursor.moveToNext()){
			notesList.add(new Note(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
			Log.e("NotesService", "Get Notes from DB" + cursor.getString(3) + " Ref:" + cursor.getString(1) + ":" + cursor.getInt(2));
		}
		cursor.close();
		return notesList;
	}

	

	
//	private int getNewId() {
//		List<Integer> ids = new ArrayList<Integer>();
//		for (Window window : getWindowsFromDB()){
//			Log.i("WindowsService", "IDS From DB: " + window.getId());
//			ids.add(window.getId());
//		}
//		
//		int i = 0;
//		while(ids.contains(i)) {
//			i++;
//		}
//		Log.i("NEW ID", i +"");
//		return i;
//		
//	}
	
	public void close() {
		db.close();
	}
	
}

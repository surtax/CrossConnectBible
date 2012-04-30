package com.crossconnect.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utility.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.crossconnect.model.BibleText;
import com.crossconnect.model.Note;
import com.crossconnect.model.OnlineAudioResource;

import database.DatabaseHelper;

/**
 * A service used to handle window creation and database management
 * 
 * This service is used to handle resources.
 * 
 * @author garylo
 *
 */
public class ResourceService {
	
	public static final String TAG = "ResourceService";
	
	DatabaseHelper databaseHelper;
	Context ctx;
	SQLiteDatabase db;
	
	public ResourceService(Context ctx) {
		this.ctx = ctx;
		databaseHelper = new DatabaseHelper(ctx);
		db = databaseHelper.getWritableDatabase();
	}
	
	public void insertUpdate(OnlineAudioResource item, String filePath,
			BibleText bibleText) {
		if (isResourceExist(item.getResourceName(), item.getResourceRepo().getChurchName())) {
			updateResource(item, filePath, bibleText);
		} else {
			insertNewResource(item, filePath, bibleText);
		}
		
	}
	
	private void insertNewResource(OnlineAudioResource item, String filePath,
			BibleText bibleText) {
		Log.d(TAG, "Insert new resource" + item.getResourceName() + ":" + filePath + " " + bibleText.getReferenceBookChapterVerse());
		ContentValues values = new ContentValues();
		//TODO: make this increment to allow for multiple ids on the same chapter
		values.put(DatabaseHelper.RES_PATH_COL, filePath);
		values.put(DatabaseHelper.RES_NAME_COL, item.getResourceName());
		values.put(DatabaseHelper.RES_RESOURCEREF_COL, item.getResourceVerse());
		values.put(DatabaseHelper.RES_AUDIO_URL_COL, item.getAudioURL());
		values.put(DatabaseHelper.RES_READ_URL_COL, item.getReadURL());
		values.put(DatabaseHelper.RES_RESOURCEREPO_COL, item.getResourceRepo().getChurchName());
		values.put(DatabaseHelper.RES_BOOK_COL, bibleText.getBook());
		values.put(DatabaseHelper.RES_CHAPTER_COL, bibleText.getChapter());
		values.put(DatabaseHelper.RES_VERSE_COL, bibleText.getVerse());
		values.put(DatabaseHelper.RES_TRANSLATION_COL, bibleText.getTranslation().getInitials());
		db.insert(DatabaseHelper.RES_TABLE_NAME, null, values);

		
	}
	
	/**
	 * Currently assumes that resourcename with churchname is unique
	 * @param item
	 * @param filePath
	 * @param bibleText
	 */
	private void updateResource(OnlineAudioResource item, String filePath,
			BibleText bibleText) {
		Log.d(TAG, "Update new resource" + item.getResourceName() + ":" + filePath + " " + bibleText.getReferenceBookChapterVerse());
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.RES_PATH_COL, filePath);
		values.put(DatabaseHelper.RES_NAME_COL, item.getResourceName());
		values.put(DatabaseHelper.RES_RESOURCEREF_COL, item.getResourceVerse());
		values.put(DatabaseHelper.RES_AUDIO_URL_COL, item.getAudioURL());
		values.put(DatabaseHelper.RES_READ_URL_COL, item.getReadURL());
		values.put(DatabaseHelper.RES_RESOURCEREPO_COL, item.getResourceRepo().getChurchName());
		values.put(DatabaseHelper.RES_BOOK_COL, bibleText.getBook());
		values.put(DatabaseHelper.RES_CHAPTER_COL, bibleText.getChapter());
		values.put(DatabaseHelper.RES_VERSE_COL, bibleText.getVerse());
		values.put(DatabaseHelper.RES_TRANSLATION_COL, bibleText.getTranslation().getInitials());
		String where = DatabaseHelper.RES_NAME_COL + "= ? and " + DatabaseHelper.RES_RESOURCEREPO_COL + " = ?";
		String[] whereArgs = {item.getResourceName(), item.getResourceRepo().getChurchName()};
		db.update(DatabaseHelper.RES_TABLE_NAME, values, where, whereArgs);
	}
	

	/**
	 * Update given a specific ID
	 * @param item
	 * @param filePath
	 * @param bibleText
	 */
	private void updateResource(int id, OnlineAudioResource item, String filePath, BibleText bibleText) {
		Log.d(TAG, "Update new resource" + item.getResourceName() + ":" + filePath + " " + bibleText.getReferenceBookChapterVerse());
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.RES_PATH_COL, filePath);
		values.put(DatabaseHelper.RES_NAME_COL, item.getResourceName());
		values.put(DatabaseHelper.RES_RESOURCEREF_COL, item.getResourceVerse());
		values.put(DatabaseHelper.RES_AUDIO_URL_COL, item.getAudioURL());
		values.put(DatabaseHelper.RES_READ_URL_COL, item.getReadURL());
		values.put(DatabaseHelper.RES_RESOURCEREPO_COL, item.getResourceRepo().getChurchName());
		values.put(DatabaseHelper.RES_BOOK_COL, bibleText.getBook());
		values.put(DatabaseHelper.RES_CHAPTER_COL, bibleText.getChapter());
		values.put(DatabaseHelper.RES_VERSE_COL, bibleText.getVerse());
		values.put(DatabaseHelper.RES_TRANSLATION_COL, bibleText.getTranslation().getInitials());
		String where = DatabaseHelper.RES_ID_COL + "= ?";
		String[] whereArgs = {String.valueOf(id)};
		db.update(DatabaseHelper.NOTES_TABLE_NAME, values, where, whereArgs);
	}

	

	/**
	 * Determine if the current resource is already in the DB
	 * @param audioURL 
	 * @param key 
	 * @return
	 */
	public boolean isResourceExist(String name, String churchName) {
		String where = DatabaseHelper.RES_NAME_COL + "='" + name + "' and " + DatabaseHelper.RES_RESOURCEREPO_COL + "= '" + churchName + "'";
		Log.e("ResourceService", "where:" + where);
		Cursor cursor = db.query(DatabaseHelper.RES_TABLE_NAME, null, where, null, null, null, null);
		boolean exists = cursor.moveToNext();
		if (exists) {
			Log.d(TAG, "Resource exists: " + cursor.getString(1) + " " + cursor.getString(2));
		} else {
			Log.d(TAG, "No Resource Entry Exists");
		}
		return exists;
	}


	
	/**
	 * Delete a note given a key
	 * @param key
	 */
//	public void removeNote(Key key) {
//		String where = DatabaseHelper.NOTES_BOOK_COL + "= ? and " + DatabaseHelper.NOTES_CHAPTER_COL + "= ?";
//		String[] whereArgs = {String.valueOf(Utils.getBook(key)), String.valueOf(Utils.getChapter(key))};
//		db.delete(DatabaseHelper.NOTES_TABLE_NAME, where, whereArgs);
//	}
//	

	
	public List<Note> getAllNotesFromDB() {
		//should make it sort by reference
		Cursor cursor = db.query(DatabaseHelper.NOTES_TABLE_NAME, null, null, null, null, null, DatabaseHelper.BOOK_COL);
		List<Note> notesList = new ArrayList<Note>();
		while(cursor.moveToNext()){
			notesList.add(new Note(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
			Log.d(TAG, "Get Notes from DB " + cursor.getString(3) + " Ref:" + cursor.getString(1) + ":" + cursor.getInt(2) + " ID:" + cursor.getInt(0));
		}
		cursor.close();
		return notesList;
	}

	
	
	public void close() {
		db.close();
	}
	
	
//	Convenience for all the column names 
//	public static final String RES_TABLE_NAME = "Resources";
//	public static final String RES_ID_COL = "Id";
//	public static final String RES_PATH_COL = "Path";
//	public static final String RES_NAME_COL = "Name";
//	public static final String RES_RESOURCEREF_COL = "Reference";
//	public static final String RES_AUDIO_URL_COL = "AudioURL";
//	public static final String RES_READ_URL_COL = "ReadURL";
//	public static final String RES_RESOURCEREPO_COL = "ReadURL";
//	public static final String RES_BOOK_COL = "Book";
//	public static final String RES_CHAPTER_COL = "Chapter";
//	public static final String RES_VERSE_COL = "Reference";
//	public static final String RES_TRANSLATION_COL = "Reference";
	
	
//	private static final String RESOURCE_TABLE_CREATE = "CREATE TABLE "
//			+ RES_TABLE_NAME + " ("+ RES_ID_COL + " INTEGER  PRIMARY KEY AUTOINCREMENT, " + RES_PATH_COL + " TEXT, " + RES_NAME_COL
//			+ " TEXT, " + RES_RESOURCEREF_COL + " TEXT, " + RES_AUDIO_URL_COL + " TEXT, "
//			+ RES_READ_URL_COL + " TEXT, " + RES_RESOURCEREPO_COL + " TEXT, " + RES_BOOK_COL
//			+ " TEXT, " + RES_CHAPTER_COL + "  INTEGER, " + RES_VERSE_COL + "  INTEGER, " + RES_TRANSLATION_COL + "  TEXT);";
	public List<OnlineAudioResource> getDownloadedExistsResources(String book) {
		List<OnlineAudioResource> resources = getDownloadedResources(book);
		List<OnlineAudioResource> existingResources = new ArrayList<OnlineAudioResource>();
		for (OnlineAudioResource resource : resources) {
			//If the file doesn't exist in file system remove the row
			if (new File(resource.getAudioURL()).exists()){
				existingResources.add(resource);
			} else {
				delete(resource.getId());
			}
		}
		return existingResources;
	}

	public List<OnlineAudioResource> getDownloadedResources(String book) {
		String where = DatabaseHelper.RES_BOOK_COL + "='" + book + "'";
		Log.d(TAG, "where:" + where);
		Cursor cursor = db.query(DatabaseHelper.RES_TABLE_NAME, null, where, null, null, null, null);
		
		List<OnlineAudioResource> resourceList = new ArrayList<OnlineAudioResource>();
		while(cursor.moveToNext()){
			resourceList.add(new OnlineAudioResource(cursor.getInt(0), cursor.getString(2), cursor.getString(3), cursor.getString(1), cursor.getString(6)));
			Log.d(TAG, "Get Resources from DB " + cursor.getString(2) + " Ref:" + cursor.getString(3) + ":" + cursor.getString(1) + " ID:" + cursor.getInt(0));
		}
		cursor.close();
		return resourceList;
	}

	public void delete(int id) {
		String where = DatabaseHelper.RES_ID_COL + "= ?";
		Log.d(TAG, "Delete where" + where);
		String[] whereArgs = {String.valueOf(id)};
		db.delete(DatabaseHelper.RES_TABLE_NAME, where, whereArgs);

	}
	
}

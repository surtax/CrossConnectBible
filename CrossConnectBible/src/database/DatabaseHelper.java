package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class to handle database creation, version control and updgrades
 * @author garylo
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "CrossConnect DB";

	public DatabaseHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Windows Tables and Columns
	public static final String WINDOWS_TABLE_NAME = "Windows";
	public static final String ID = "Id";
	public static final String REF_TXT_COL = "ReferenceText";
	public static final String TITLE_COL = "Title";
	public static final String TEXT_COL = "Text";
	public static final String BOOK_COL = "Book";
	public static final String CHAPTER_COL = "Chapter";
	public static final String VERSE_COL = "Verse";
	public static final String TRANSLATION_COL = "Translation";
	
	
	//Notes and Columns
	public static final String NOTES_TABLE_NAME = "Notes";
	public static final String NOTES_ID_COL = "Id";
	public static final String NOTES_BOOK_COL = "Book";
	public static final String NOTES_CHAPTER_COL = "Chapter";
	public static final String NOTES_TEXT_COL = "Note";
	public static final String NOTES_REFERENCES_COL = "Refs";
	public static final String NOTES_LABELS_COL = "Labels";
	public static final String NOTES_TITLE_COL = "Title";

	
	//Labels Table and columns
	public static final String LABEL_TABLE_NAME = "Labels";
	public static final String LABEL_BOOK_COL = "Book";
	public static final String LABEL_LABEL_COL = "Label";
	public static final String LABEL_CHAPTER_COL = "Chapter";
	
	
	//Reference Table and columns
	public static final String REF_TABLE_NAME = "Reference";
	public static final String REF_BOOK_COL = "Book";
	public static final String REF_REF_COL = "Reference";
	public static final String REF_CHAPTER_COL = "Chapter";

	//Reference Table and columns
	public static final String RES_TABLE_NAME = "Resources";
	public static final String RES_ID_COL = "Id";
	public static final String RES_PATH_COL = "Path";
	public static final String RES_NAME_COL = "Name";
	public static final String RES_RESOURCEREF_COL = "Reference";
	public static final String RES_AUDIO_URL_COL = "AudioURL";
	public static final String RES_READ_URL_COL = "ReadURL";
	public static final String RES_RESOURCEREPO_COL = "ResourceRepo";
	public static final String RES_BOOK_COL = "Book";
	public static final String RES_CHAPTER_COL = "Chapter";
	public static final String RES_VERSE_COL = "Verse";
	public static final String RES_TRANSLATION_COL = "Translation";

	
	//Resource create proc
	private static final String RESOURCE_TABLE_CREATE = "CREATE TABLE "
			+ RES_TABLE_NAME + " ("+ RES_ID_COL + " INTEGER  PRIMARY KEY AUTOINCREMENT, " + RES_PATH_COL + " TEXT, " + RES_NAME_COL
			+ " TEXT, " + RES_RESOURCEREF_COL + " TEXT, " + RES_AUDIO_URL_COL + " TEXT, "
			+ RES_READ_URL_COL + " TEXT, " + RES_RESOURCEREPO_COL + " TEXT, " + RES_BOOK_COL
			+ " TEXT, " + RES_CHAPTER_COL + "  INTEGER, " + RES_VERSE_COL + "  INTEGER, " + RES_TRANSLATION_COL + "  TEXT);";

	
	
	//Windows create proc
	private static final String WINDOWS_TABLE_CREATE = "CREATE TABLE "
			+ WINDOWS_TABLE_NAME + " ("+ ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, " + REF_TXT_COL + " TEXT, " + BOOK_COL
			+ " TEXT, " + CHAPTER_COL + " INTEGER, " + TEXT_COL + " TEXT, "
			+ TITLE_COL + " TEXT, " + TRANSLATION_COL + " TEXT, " + VERSE_COL
			+ " INTEGER);";

	
	//Just add additional column of label here - simplify the process one label per note
	//Notes create proc
	private static final String NOTES_TABLE_CREATE = "CREATE TABLE "
			+ NOTES_TABLE_NAME + " ("+ NOTES_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOTES_BOOK_COL + " TEXT, "  + NOTES_CHAPTER_COL + " INTEGER, " + NOTES_TEXT_COL
			+ " TEXT);";

	//Labels create proc
	private static final String LABEL_TABLE_CREATE = "CREATE TABLE "
			+ LABEL_TABLE_NAME + " ("+ LABEL_BOOK_COL + " TEXT, " + LABEL_CHAPTER_COL
			+ " INTEGER, " + LABEL_LABEL_COL + " TEXT);";

	//References view will just pull out the passage where it exists and the label of the note
	//Reference create proc
	private static final String REF_TABLE_CREATE = "CREATE TABLE "
			+ REF_TABLE_NAME + " ("+ REF_BOOK_COL + " TEXT, " + REF_CHAPTER_COL
			+ "INTEGER, " + REF_REF_COL + " TEXT);";
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(RESOURCE_TABLE_CREATE);
		db.execSQL(WINDOWS_TABLE_CREATE);
		db.execSQL(NOTES_TABLE_CREATE);
		db.execSQL(LABEL_TABLE_CREATE);
		db.execSQL(REF_TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}

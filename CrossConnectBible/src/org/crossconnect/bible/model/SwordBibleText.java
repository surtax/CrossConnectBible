package org.crossconnect.bible.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sword.engine.sword.SwordDocumentFacade;

import org.crossconnect.bible.utility.Utils;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.RocketPassage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleBook;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.util.Log;

public class SwordBibleText implements BibleText, Parcelable {

	private Key key;
	private Book translation;
	private SpannableStringBuilder spannableStringBuilder;
	private List<Integer> boundaries;

	/**
	 * DO not use this constructor except for placeholders
	 */
	public SwordBibleText() {
		spannableStringBuilder = new SpannableStringBuilder();
	}

	
	public SwordBibleText(Book trans, Key key) {
		this.key = key;
		this.translation = trans;
	}
	
    public SwordBibleText(String bookAndChapter, String translation) throws NoSuchKeyException {
        this.translation = SwordDocumentFacade.getInstance().getDocumentByInitials(translation);
        //Get first verse 
        Iterator iterator = ((RocketPassage) this.translation.getKey(bookAndChapter)).iterator();
        Verse verse = (Verse) iterator.next();
        key = new VerseRange(verse.getFirstVerseInChapter(), verse.getLastVerseInChapter());
    }

	public SwordBibleText(String bibleBook, int chapterNo, String translation) {
		
		Verse verse = new Verse(BibleBook.getBook(bibleBook), chapterNo, 1, true);
        key = new VerseRange(verse.getFirstVerseInChapter(), verse.getLastVerseInChapter());
		this.translation = SwordDocumentFacade.getInstance()
				.getDocumentByInitials(translation);

	}

	/**
	 * Create sword text with a specific verse 
	 * @param bibleBook
	 * @param chapterNo
	 * @param verseNo
	 * @param translation
	 */
	public SwordBibleText(String bibleBook, int chapterNo, int verseNo,
			String translation) {
			this.key = new Verse(BibleBook.getBook(bibleBook), chapterNo, verseNo, true);
		this.translation = SwordDocumentFacade.getInstance()
				.getDocumentByInitials(translation);
	}
	
	@Override
	public BibleText getNextChapterRef() {
		return new SwordBibleText(translation, Utils.nextChapter(key));
	}

	/**
	 * Get previous chapters reference
	 */
	@Override
	public BibleText getPrevChapterRef() {
		return new SwordBibleText(translation, Utils.prevChapter(key));
	}

	/**
	 * Used in windows to get the preview for windows
	 */
	@Override
	public CharSequence getPreview() {
		
//		
//		if (spannableStringBuilder.length() > 200) {
//			return spannableStringBuilder.subSequence(0, 200);			
//		} else {
//			return spannableStringBuilder.subSequence(0, spannableStringBuilder.length());			
//		}		
		
		//Default it to empty for new windows 
		CharSequence preview = "";
		
		//Empty boundaries most likely loaded from org.crossconnect.bible.database
		//Then just return the whole text
		if (boundaries == null || boundaries.size() == 0) {
			return spannableStringBuilder.toString();
		} else {
			try{
				int verseToPreview = Utils.getVerse(key);

				Log.d("SwordBibleText", key.toString() + " Verse to preview " + verseToPreview);
				//TODO: need to limit the number of chars returned
				preview = spannableStringBuilder.subSequence(boundaries.get(verseToPreview-1), boundaries.get(verseToPreview));
			} catch (Exception e) {
				
			}
		}		
		return preview;		
	}

	public List<Integer> getTxtBoundaries() {
		return boundaries;
	}

	/**
	 * Returns the the book and chapter i.e. Matthew 1
	 */
	@Override
	public String getReferenceBookChapter() {
		return Utils.getBookChapter(key);
	}
	
	@Override
	public String getDisplayReferenceBookChapter() {
		return getReferenceBookChapter().replace("Revelation of John", "Revelation").replace("Song of Solomon", "Songs");
	}

	
	
	/**
	 * Used for when sharing verses
	 * TODO: this will actually already have verse if it is a ver need to check fo isVerse() and make it not spit original verse
	 */
	@Override
	public String getReferenceBookChapterVerse() {

		if (key == null) {
			return "New";
		} else {
			return getReferenceBookChapter() + ":" + Utils.getVerse(key);
		}
	}
	
	@Override
	public String getDisplayReferenceHeader() {
		return getReferenceBookChapterVerse().replace("Revelation of John", "Revelation").replace("Song of Solomon", "Songs");
		
	}
	
	/**
	 * Used on tabs for windows shortcut
	 * @return
	 */
	public String getShortReferenceBookChapterVerse() {
		return Utils.getShortBook(key); 
	}


	
	/**
	 * A summary of the reference of the current key
	 * Used as key on the org.crossconnect.bible.database for windows
	 */
	@Override
	public String getReferenceBookChapterVersion() {
		return getReferenceBookChapter() + "-"  + translation;
	}
	
	@Override
	public String getReferenceBookChapterVerseVersion(int startVerse, int endVerse) {
		String verse;
		if(startVerse == 0) {
			verse = " ";
		} else {
			verse = (startVerse == endVerse) ? ":" + startVerse + " ": ":" + startVerse + "-" + endVerse + " ";
		}

		
		return getReferenceBookChapter() + "" + verse + translation;
	}

	/**
	 * Get which verse the given character is part of
	 * 
	 * @return which verse this character belongs to
	 */
	@Override
	public int getVerseFromCharPos(int characterPos) {
		Log.d("SwordBibleText", "GetVerseFrom CharPos: " + characterPos);

		int verse = 0;

		Iterator<Integer> it = boundaries.iterator();

		// Loop through the boundaries to find out which verse was selected and
		// highlight it
		while (it.hasNext()) {
			Integer currentBoundary = (Integer) it.next();
			if (characterPos < currentBoundary) {
				Log.d("SwordBibleText", "SelectedVerse is: " + verse);
				return verse;
			} else if (it.hasNext() == false) {
				if (currentBoundary == characterPos) {
					//Last verse so should check if it is the end character 
					return verse;					
				}
			}
			verse++;

		}
		// TODO: Character is out of bounds should throw exception?
		return -1;
	}

	@Override
	public int getCharPosStartFromVerse(int verseQuery) {
		Log.d("CharPosStartFromVerse", "verseQuery:" + verseQuery);
		Log.d("CharPosStartFromVerse", " return:" + boundaries.get(verseQuery - 1));
		return boundaries.get(verseQuery - 1);
	}

	@Override
	public int getCharPosEndFromVerse(int verseQuery) {
		Log.d("CharPosEndFromVerse", "verse:" + verseQuery + 1 + " return:"
				+ boundaries.get(verseQuery));
		return boundaries.get(verseQuery);
	}

	public int getChapter() {
		return Utils.getChapter(key);
	}

	public void setChapter(int chapter) {
//		this.chapter = chapter;
	}

	public int getVerse() {
		return Utils.getVerse(key);
	}

	public void setVerse(int verse) {
		this.key = Utils.getVerseKey(key, verse);
	}

	public String getBook() {
		return Utils.getBook(key);
		
	}

	public void setBook(String book) {
//		this.book = book;
	}

	public SpannableStringBuilder getSpannableStringBuilder() {
		return spannableStringBuilder;
	}

	public void setSpannableStringBuilder(
			SpannableStringBuilder spannableStringBuilder) {
		this.spannableStringBuilder = spannableStringBuilder;
	}

	public List<Integer> getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(List<Integer> boundaries) {
		this.boundaries = boundaries;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Book getTranslation() {
		return translation;
	}

	public void setTranslation(Book translation) {
		this.translation = translation;
	}
	

	//Make the model parcellable so that it can be passed between activities
	
	public SwordBibleText(Parcel in) {
		key = (Key) in.readValue(Key.class.getClassLoader());
		String translationInitials = in.readString();
		translation = SwordDocumentFacade.getInstance().getDocumentByInitials(translationInitials);
		CharSequence cs = (CharSequence) in.readValue(CharSequence.class.getClassLoader());
		if (cs != null ) {
			spannableStringBuilder = new SpannableStringBuilder(cs);
			
		}
		boundaries = new ArrayList<Integer>();
		in.readList(boundaries, Integer.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(key);
		dest.writeString(translation.getInitials());
		CharSequence cs = spannableStringBuilder;
		dest.writeValue(cs);
		dest.writeList(boundaries);
	}
	
	public static final Parcelable.Creator<SwordBibleText> CREATOR = new Parcelable.Creator<SwordBibleText>() {
	
		public SwordBibleText createFromParcel(Parcel in) {
			return new SwordBibleText(in);
		}

		public SwordBibleText[] newArray(int size) {
		    return new SwordBibleText[size];
		}
	};

	@Override
	public void setTranslation(String translation) {
		this.translation = SwordDocumentFacade.getInstance().getDocumentByInitials(translation);
	}

}

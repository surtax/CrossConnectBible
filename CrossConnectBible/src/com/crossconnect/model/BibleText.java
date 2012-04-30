package com.crossconnect.model;

import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import android.os.Parcelable;
import android.text.SpannableStringBuilder;

public interface BibleText extends Parcelable {
    
    public BibleText getNextChapterRef();
    public BibleText getPrevChapterRef();
    
    /**
     * Get full reference book  and chapter i.e. John 3
     * @return
     */
    public String getReferenceBookChapter();
    
    public String getDisplayReferenceBookChapter();
    
	/**
	 * Used on tabs for windows shortcut
	 * @return
	 */
	public String getShortReferenceBookChapterVerse();


    /**
     * Get full reference book chapter and verse as well as ttranslation version. i.e. John 3:16 (ESV)
     * 
	 * Used for when sharing verses
	 * TODO: this will actually already have verse if it is a ver need to check fo isVerse() and make it not spit original verse
     * 
     * @param endVerse the ending verse number 
     * @param startVerse the starting verse number 
     * @return
     */
    public String getReferenceBookChapterVerseVersion(int start, int end);

    
    /**
     * Get the surrounding couple of verses to fill window preview
     * if total is 200 characters and verse is already 120 then
     * add 40 to beginning and end
     * 
     * @return
     */
    public CharSequence getPreview();
    
    /**
     * Boundaries are the character numbers in which a verse starts
     * @return
     */
    public List<Integer> getTxtBoundaries();
    
    
    /**
     * Return the verse number based on the character position given
     * 
     */
    public int getVerseFromCharPos(int characterPos);
    
	public int getCharPosEndFromVerse(int conjoiningVerseEnd);
	
	public int getCharPosStartFromVerse(int conjoiningVerseStart);
	
	public int getChapter();
	
	public String getBook();
	
	public int getVerse();
	
	public Book getTranslation();
	
	public SpannableStringBuilder getSpannableStringBuilder();
	
	public void setSpannableStringBuilder(SpannableStringBuilder sb);
	
	public void setBoundaries(List<Integer> boundaries);
	
	public String getReferenceBookChapterVersion();
	
	public void setTranslation(String string);
	public void setVerse(int verseNumber);
	public String getReferenceBookChapterVerse();
	public String getDisplayReferenceHeader();
	public Key getKey();

}

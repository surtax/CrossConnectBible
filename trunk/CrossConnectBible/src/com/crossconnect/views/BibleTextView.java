package com.crossconnect.views;

import com.crossconnect.model.BibleText;

public interface BibleTextView {
	
	public void loadVerseReference(BibleText verseReference);
	public void loadNextChapter();
	public void loadPreviousChapter();
	public BibleText getCurrentVerseReference();
	
}

package org.crossconnect.bible.model;

public interface Window {
    public BibleText getBibleText();
    public boolean isFavourite();
	int getId();
	void setId(int id);
	void setBibleText(BibleText bibleText);
}

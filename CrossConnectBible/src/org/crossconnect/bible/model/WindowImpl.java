package org.crossconnect.bible.model;



public class WindowImpl implements Window{

    BibleText bibleText;
    int id;
    
    /**
     * Unique id constructor - no bibletext placeholder
     */
    public WindowImpl(int id) {
    	this.id = id;
        this.bibleText = new SwordBibleText();
    }

    /**
     * Unique id and bibletext constructor
     */
    public WindowImpl(BibleText bibleText, int id) {
    	this.id = id;
        this.bibleText = bibleText;
    }
    
    @Override
    public BibleText getBibleText() {
        // TODO Auto-generated method stub
        return bibleText;
    }

    @Override
    public void setBibleText(BibleText bibleText) {
    	this.bibleText = bibleText;
    }

    
    @Override
    public boolean isFavourite() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
	public int getId() {
		return id;
	}


    @Override
	public void setId(int id) {
		this.id = id;
	}

}

package org.crossconnect.bible.model;

/**
 * Class that represents the contents of the online audio resource
 * 
 * @author Gary Lo
 * 
 */
public class Note {

	private int id;
	private String text;
	private String book;
	private int chapter;

	public Note() {
	}

	public Note(int id, String book, int chapter, String text) {
		this.id = id;
		this.book = book;
		this.chapter = chapter;
		this.text = text;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public int getChapter() {
		return chapter;
	}

	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
}
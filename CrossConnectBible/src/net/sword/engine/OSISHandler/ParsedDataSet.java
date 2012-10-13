package net.sword.engine.OSISHandler;




import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;

/**
 * A class to build the spannable string to be displayed. Used by SAX parser.
 * 
 * @author garylo
 *
 */
public class ParsedDataSet {
	
	private SpannableStringBuilder sb = new SpannableStringBuilder();
	private String reference = null;
	private String previous = null;
	private String next = null;
	private String current = null;
	private List<Integer> boundaries;
	
	public ParsedDataSet() {
		boundaries = new ArrayList<Integer>();
	}

	public String getCurrent() {
		return current;
	}

	public String getNext() {
		return next;
	}

	public String getPrevious() {
		return previous;
	}

	public String getReferemce() {
		return reference;
	}

	public SpannableStringBuilder getVerse() {
		return sb;
	}

	public void trim() {
		if (sb.length() > 0) {
			if (Character.isSpace(sb.charAt(0))) {
				int i = 0;
				while (Character.isSpace(sb.charAt(i))) {
					i++;
				}
				sb.delete(0, i);
			}
			if (Character.isSpace(sb.charAt(sb.length() - 1))) {
				int i = sb.length() - 1;
				while (Character.isSpace(sb.charAt(i))) {
					i--;
				}
				if (sb.length() > i + 1) {
					sb.delete(i + 1, sb.length());
				}
			}

		}

	}

	public void setNext(String next) {
		this.next = next;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public void setHeading(String heading) {
		if (heading != null) {
			if (sb.length() == 0) {
				sb.append(heading);
			} else {
				if (sb.charAt(0) != '\'' && sb.charAt(sb.length() - 1) != '\'') {
					// Special case of apostrophe dont want extra space in front
					if (heading.equals("'")) {
						sb.append(heading);
					} else {
						sb.append(' ' + heading);
					}
				} else {
					sb.append(heading);
				}
			}
		}
		int length = heading.length();
		sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - length , sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	
	private StringBuilder headingBuilder = new StringBuilder();;
	
	/**
	 * Append the heading to the heading buffer.
	 * 
	 * Reason doing this is because often there may be a <divine> </divine> tag in middle of
	 * heading which will cause it to be on a new line or verse number in middle
	 * 
	 * Buffer and write to the end
	 * 
	 * @param heading part of the heading to buffer call writeSwordHeading to write
	 */
	public void setSwordHeading(String heading) {
		headingBuilder.append(heading);
	}
	
	/**
	 * Write the heading from the headingBuilder into the actual builder
	 */
	public void writeSwordHeading() {
		
		if (headingBuilder.toString().trim().length() > 0) {
		
			String heading = headingBuilder.append("\n").toString();
	
			if (sb.length() == 0) {
					//First header
					sb.append(heading);
				} else if (sb.charAt(0) == '\'' || sb.charAt(sb.length() - 1) == '\'') {
					//Last character is apostrophe special case sometimes it will stuff up because header is split into two
					sb.append(heading);
				} else if (heading.equals("'")) {
					//The case by which the first section of header is apostrophe
						sb.append(heading);
				} else if (sb.charAt(sb.length() - 1) != '\n'){
					// If not new line then start new line because it is supposed to be like H1
					sb.append("\n ");
					sb.append(heading);
				} else {
					sb.append(' ');
					sb.append(heading);
				}
			int length = heading.length();
			sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			headingBuilder.delete(0, headingBuilder.length());
		}
	}

	
	

	public void setVerseNum(String num) {
		
		//Add a space after the verse number
		num = num + " ";
		//Log.d("setVerseNum", "boundary added in: " +  sb.length());
		//Log.d("setVerseNum", num + "sb: " + sb.length());
		if (num != null) {
			if (sb.length() == 0) {
				sb.append(num);
			} else {
				if (sb.charAt(0) != '\'' && sb.charAt(sb.length() - 1) != '\'') {
					sb.append(' ' + num);
				} else {
					sb.append(num);
				}
			}
		}
		int length = num.length();
		sb.setSpan(new RelativeSizeSpan((float) 0.5), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sb.setSpan(new ForegroundColorSpan(Color.RED), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sb.setSpan(new SuperscriptSpan(), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

	}
	
	public void markBoundary() {
		boundaries.add(sb.length());
	}
	
//	public void setSwordVerseNum(String num) {
//		if (num != null) {
//			sb.append(' ' + num);
//			int length = num.length();
//			sb.setSpan(new AbsoluteSizeSpan(12), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//			sb.setSpan(new ForegroundColorSpan(Color.RED), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//			sb.setSpan(new SuperscriptSpan(), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		}
//
//	}

	public void setVerse(String verse) {
		sb.append(verse);
		//all these weird error catching here - not sure what it was for
//		if (verse != null) {
//			if (sb.length() == 0) {
//				sb.append(verse);
//			} else {
//				if (sb.charAt(0) != '\'' && sb.charAt(sb.length() - 1) != '\'') {
//					if (verse.equals("'")) {
//						sb.append(verse);
//					} else {
//						sb.append(' ' + verse);
//					}
//				} else {
//					sb.append(verse);
//				}
//			}
//		}
	}
	
	
//	/**
//	 * Sometimes the sword verse starts with a new line so must append the verse num above that
//	 * @param verse
//	 * @param num
//	 */
//	public void setSwordVerse(String verse, String num) {
//		//Log.d("SetSwordVerse", verse);
//		if (verse != null && num != null) {
//			if(verse.length() > 0 && verse.charAt(0) == '\n'){
//				sb.append('\n');
//				setSwordVerseNum(num);
//				sb.append(verse.substring(1));
//			} else {
//				setSwordVerseNum(num);
//				sb.append(verse);
//			}
//		}
//	}

	@Override
	public String toString() {
		return "sb=" + sb + "\nreference= " + reference;
	}

	public void verseNewline() {
//		Log.d("ParsedDataSet", "new line called");
		sb.append('\n');
	}

	public void verseBR() {
//		Log.d("ParsedDataSet", "new line called");
		sb.append(' ');
	}

	
	/**
	 * Return the chapter based on current verse reference
	 * @return returns the current chapter
	 */
	public String getCurrentChapter() {
		String[] split = current.split(" ");
		if (current.charAt(0) == '1' || current.charAt(0) == '2') {
			return split[2];
		} else {
			return split[1];
		}
	}
	
	/**
	 * Return the current book based on current verse reference
	 * @return returns the current chapter
	 */
	public String getCurrentBook() {
		String[] split = current.split(" ");
		if (current.charAt(0) == '1' || current.charAt(0) == '2' || current.charAt(0) == '3') {
			return split[0] + " " + split[1];
		} else {
			return split[0];
		}
	}
	
	public void endBoundary() {
		trim();
		boundaries.add(sb.length());
	}

	public List<Integer> getBoundaries() {
		return boundaries;
	}
	
	public int getLength() {
		return sb.length();
	}

	public void setQuote(String quote) {
		sb.append(quote);
	}

	/**
	 * Set a red quote
	 * @param string 
	 */
	public void setJesusQuote(String quote) {
		if (quote != null && quote.length() > 0) {
			sb.append(quote);
			sb.setSpan(new ForegroundColorSpan(Color.RED), sb.length() - 1, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public void setJesusVerse(String verse) {
		setVerse(verse);
		int length = verse.length();
		sb.setSpan(new ForegroundColorSpan(Color.RED), sb.length() - length, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public void indent() {
		sb.append("\n   ");
	}

	public void setParagraph() {
		sb.append("\n\n");
	}
}
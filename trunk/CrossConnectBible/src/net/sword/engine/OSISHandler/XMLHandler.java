package net.sword.engine.OSISHandler;


import java.util.Stack;

import org.crosswire.jsword.book.OSISUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class XMLHandler extends DefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_reference = false;
	private boolean in_verseUnit = false;
	private boolean in_verseNum = false;
	// private boolean out_verseNum = false;
	private boolean in_footnote = false;
	private boolean in_heading = false;
	private boolean in_nextChapter = false;
	private boolean in_prevChapter = false;
	private boolean in_current = false;
	public boolean include_verse_num = true;
	public boolean include_headings = true;
	private boolean verseNumberQueued = false;
	private boolean in_jesus_red_text = false;

	private ParsedDataSet parsedDataSet = new ParsedDataSet();
	
	private enum LType {indent, br, end_br, ignore};

	private Stack<LType> stack = new Stack<LType>();
	private Stack<String> quoteStack = new Stack<String>();
	
	

	
	int verseNumber = -1;
	
	
	//Settings
	
	private boolean jesusRedText = true;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		 String debug = new String(ch, start, length);

		if (in_heading == true) {
			if (include_headings) {
				parsedDataSet.setSwordHeading(new String(ch, start, length));				
			}
		} else if (this.in_verseUnit == true && in_footnote == false) {
			Log.e("Verse", new String(ch, start, length));

			//If theres a verse number to be added add it before the verse
			if (verseNumberQueued) {
				parsedDataSet.setVerseNum(String.valueOf(verseNumber));
				verseNumberQueued = false;
			}
			
			if (jesusRedText && in_jesus_red_text) {
				parsedDataSet.setJesusVerse(new String(ch, start, length));
			} else {
				parsedDataSet.setVerse(new String(ch, start, length));
			}
			
		} else if (this.in_reference) {
			parsedDataSet.setReference(new String(ch, start, length));
		} else if (this.in_nextChapter) {
			parsedDataSet.setNext(new String(ch, start, length));
		} else if (this.in_prevChapter) {
			parsedDataSet.setPrevious(new String(ch, start, length));
		} else if (this.in_current) {
			parsedDataSet.setCurrent(new String(ch, start, length));
		}
	}

	@Override
	public void endDocument() throws SAXException {
		parsedDataSet.endBoundary();
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		
		if (localName.equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
			this.in_verseUnit = false;
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_VERSE);
//			verseHandler.startAndUpdateVerse(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_TITLE)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_TITLE);
			//If theres a verse number to be added add it after header
			parsedDataSet.writeSwordHeading();
			if (verseNumberQueued) {
				parsedDataSet.setVerseNum(String.valueOf(verseNumber));
				verseNumberQueued = false;
			}
			in_heading = false;
//			titleHandler.start(attrs);		
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_NOTE)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_NOTE);
			in_footnote = false;
//			noteAndReferenceHandler.startNote(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_REFERENCE)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_REFERENCE);
//			noteAndReferenceHandler.startReference(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_LB)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_LB);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_L)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_L);
			LType type = stack.pop();
			if (LType.end_br.equals(type)) {
				Log.e("XMLHandler", "Pop new line");
				parsedDataSet.verseNewline();
			}
			//			lHandler.startL(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_P)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_P);
//			write("<p />");
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_Q)) {
			Log.e("EndXMLHandler", OSISUtil.OSIS_ELEMENT_Q);
			//TODO: need to find apropriate place to terminate Jesus red text need to have a stack
//			String who = atts.getValue(OSISUtil.ATTRIBUTE_Q_WHO);
//			if (who != null && who.length() > 0) {
//				parsedDataSet.setQuote();
//			} else {
				String who = quoteStack.pop();

				if (jesusRedText && in_jesus_red_text && "Jesus".equals(who)) {
					in_jesus_red_text = false;
				}
//					parsedDataSet.setJesusQuote();
//					// esv uses q for red-letter and for quote mark
//					//TODO:set span for Jesus red text
//				}
//			}
//			qHandler.start(attrs);
		} else if (localName.equals("milestone")) {
			Log.e("EndXMLHandler", "milestone");
//			String type = attrs.getValue(OSISUtil.OSIS_ATTR_TYPE);
//			if (StringUtils.isNotEmpty(type)) {
//				if (type.equals("line") || type.equals("x-p")) {
//					//e.g. NETtext Mt 4:14
//					write(HTML.BR);
//				}
//			}
		} else if (localName.equals("transChange")) {
			Log.e("XMLHandler", "LOLOLOL");
//			write("<span class='transChange'>");
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_W)) {
			Log.e("XMLHandler", "LOLOLOL");
//			strongsHandler.start(attrs);
		} else {
			Log.e("XMLHandler", localName + " is an unhandled tag");
		}
	}

	public ParsedDataSet getParsedData() {
		return this.parsedDataSet;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.parsedDataSet = new ParsedDataSet();
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		
		//From And Bible
		if (localName.equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
			if(verseNumber == -1 || atts != null) {
				verseNumber = TagHandlerHelper.osisIdToVerseNum(atts.getValue("", OSISUtil.OSIS_ATTR_OSISID));
			} else {
				verseNumber++;
			}
			
			//Flag verse number needs be added, not done here because if header want to add it after header
			verseNumberQueued = true;
			this.in_verseUnit = true;
			
			//Mark the start of highlight area - reason not done by versenum is so highlight
			//includes header
			Log.e("XMLHandler", "Boundary marked");
			parsedDataSet.markBoundary();
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_VERSE);
			
			
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_TITLE)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_TITLE);
			in_heading = true;
			
			//Dont include if is the chapter title i.e. Philippians 1
			include_headings = !(atts.getLength()==1 && OSISUtil.GENERATED_CONTENT.equals(atts.getValue(OSISUtil.OSIS_ATTR_TYPE)));
			
//			titleHandler.start(attrs);		
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_NOTE)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_NOTE);
			in_footnote = true;
//			noteAndReferenceHandler.startNote(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_REFERENCE)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_REFERENCE);
//			noteAndReferenceHandler.startReference(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_LB)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_LB + " added new line");
			parsedDataSet.verseNewline();
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_L)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_L);

			// Refer to Gen 3:14 in ESV for example use of type=x-indent
			String type = atts.getValue(OSISUtil.OSIS_ATTR_TYPE);
			LType ltype = LType.ignore;
			if (type != null && type.length() > 0) {
				if (type.contains("indent")) {
					Log.e("XMLHandler", "Indents - added new line and indent");
					parsedDataSet.indent();
					ltype = LType.ignore;
				} else if (type.contains("br")) {
					Log.e("XMLHandler", "BR Tag - added space");
					parsedDataSet.verseBR();
					ltype = LType.ignore;
				} else {
					ltype = LType.ignore;
					Log.e("XMLHandler", "Unknown <l> tag type:"+type);
				}			
			} else if (atts.getValue(OSISUtil.OSIS_ATTR_SID) != null && atts.getValue(OSISUtil.OSIS_ATTR_SID).length() > 0) {
				Log.e("XMLHandler", "Added new line sid");
				parsedDataSet.verseNewline();
				ltype = LType.br;
			} else if (atts.getValue(OSISUtil.OSIS_ATTR_EID) != null && atts.getValue(OSISUtil.OSIS_ATTR_EID).length() > 0) {
				Log.e("XMLHandler", "Added new line eid");
				// e.g. Isaiah 40:12
				parsedDataSet.verseNewline();
				ltype = LType.br;
			} else {
				//simple <l>
				Log.e("XMLHandler", "End BR tag");
				ltype = LType.end_br;
			}
			stack.push(ltype);

			
			
			//			lHandler.startL(attrs);
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_P)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_P);
			parsedDataSet.setParagraph();
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_Q)) {
			Log.e("XMLHandler", OSISUtil.OSIS_ELEMENT_Q + "quotation marks or red letter Jesus");
			String who = atts.getValue(OSISUtil.ATTRIBUTE_Q_WHO);
			quoteStack.push(who);

			if (jesusRedText && "Jesus".equals(who)) {
					in_jesus_red_text = true;
					// esv uses q for red-letter and for quote mark
					//TODO:set span for Jesus red text
				} 
			
			if (in_jesus_red_text) {
				parsedDataSet.setJesusQuote(atts.getValue("marker"));
			} else {
				parsedDataSet.setQuote(atts.getValue("marker"));
			}

//			}
		} else if (localName.equals("milestone")) {
			Log.e("XMLHandler", "milestone");
//			String type = attrs.getValue(OSISUtil.OSIS_ATTR_TYPE);
//			if (StringUtils.isNotEmpty(type)) {
//				if (type.equals("line") || type.equals("x-p")) {
//					//e.g. NETtext Mt 4:14
//					write(HTML.BR);
//				}
//			}
		} else if (localName.equals("transChange")) {
			Log.e("XMLHandler", "LOLOLOL");
//			write("<span class='transChange'>");
		} else if (localName.equals(OSISUtil.OSIS_ELEMENT_W)) {
			Log.e("XMLHandler", "LOLOLOL");
//			strongsHandler.start(attrs);
		} else {
			Log.e("XMLHandler", localName + " is an unhandled tag");
//			log.info("Verse "+currentVerseNo+" unsupported OSIS tag:"+name);
		}
		
	}
	
	public String toString() {
        return parsedDataSet.toString();
    }

	
}
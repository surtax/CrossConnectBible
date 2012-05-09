package net.sword.engine.sword;

import java.util.Iterator;

import net.sword.engine.OSISHandler.ParsedDataSet;
import net.sword.engine.OSISHandler.XMLHandler;

import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;
import org.crossconnect.bible.utility.Utils;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;

import android.util.Log;


/** JSword facade
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class SwordContentFacade {
	private static final String TAG = "SwordContentApi";
	private static SwordContentFacade singleton;
	private static String NIGHT_MODE_STYLESHEET = "night_mode.css";


	// set to false for testing
	public static boolean isAndroid = true; //CommonUtils.isAndroid();
	

	public static SwordContentFacade getInstance() {
		if (singleton==null) {
			synchronized(SwordContentFacade.class)  {
				if (singleton==null) {
					SwordContentFacade instance = new SwordContentFacade();
					singleton = instance;
				}
			}
		}
		return singleton;
	}

	private SwordContentFacade() {
	}
	
//	/** top level method to fetch html from the raw document data
//	 * 
//	 * @param book
//	 * @param key
//	 * @param maxKeyCount
//	 * @return
//	 * @throws NoSuchKeyException
//	 * @throws BookException
//	 * @throws IOException
//	 * @throws URISyntaxException
//	 * @throws ParserConfigurationException
//	 */
//	public synchronized FormattedDocument readHtmlText(Book book, Key key, int maxKeyCount) throws ParseException
//	{
//		FormattedDocument retVal = new FormattedDocument();
//		if (book==null || key==null) {
//			//TODO this should include css to change to night mode if necessary
//			retVal.setHtmlPassage("");
//		} else if (!book.contains(key)) {
//			//TODO this should include css to change to night mode if necessary
//			Log.w(TAG, "KEY:"+key+" not found in doc:"+book);
//			retVal.setHtmlPassage("Not found in document");
//		} else {
//			// we have a fast way of handling OSIS zText docs but WEB/HNV needs the superior JSword error recovery for mismatching tags 
//			if ("OSIS".equals(book.getBookMetaData().getProperty("SourceType")) &&
//				"zText".equals(book.getBookMetaData().getProperty("ModDrv")) &&
//				!"FreCrampon".equals(book.getInitials()) &&
//				!"AB".equals(book.getInitials()) &&
//				!"FarsiOPV".equals(book.getInitials()) &&
//				!"WEB".equals(book.getInitials()) &&
//				!"HNV".equals(book.getInitials())) {
//				retVal = readHtmlTextOptimizedZTextOsis(book, key, maxKeyCount);
//			} else {
//				retVal = readHtmlTextStandardJSwordMethod(book, key, maxKeyCount);
//			}
//		}
//		return retVal;
//	}
//
//	private FormattedDocument readHtmlTextOptimizedZTextOsis(Book book, Key key, int maxKeyCount) throws ParseException
//	{
//		log.debug("Using fast method to fetch document data");
//		InputStream is = new OSISInputStream(book, key);
//
//		OsisToHtmlSaxHandler osisToHtml = getSaxHandler(book, key);
//		XMLHandler xcHandler = new XMLHandler();
//		
//		SAXParser parser = getSAXParser();
//		try {
//			parser.parse(is, osisToHtml);
//			parser.parse(is, xcHandler);
//		} catch (Exception e) {
//			log.error("Parsing error", e);
//			throw new ParseException("Parsing error", e);
//		}
//		
//		FormattedDocument retVal = new FormattedDocument();
//		retVal.setHtmlPassage(osisToHtml.toString());
//		retVal.setNotesList(osisToHtml.getNotesList());
//		
//        return retVal;
//	}

	private ParsedDataSet readSpannableStringBuilderJsword(Book book, Key key, int maxKeyCount) 
	{
		try {
			XMLHandler xmlhandler = new XMLHandler();
			BookData data = new BookData(book, key);		
			SAXEventProvider osissep = data.getSAXEventProvider();
			if (osissep == null) {
				Log.e(TAG, "No osis SEP returned");
			} else {

				osissep.provideSAXEvents(xmlhandler);
				
//				Log.e("xmlhandler text", xmlhandler.toString());
			}
	        return xmlhandler.getParsedData();
		} catch (Exception e) {
			
		}
		return null;
	}

	/**
	 * Obtain a SAX event provider for the OSIS document representation of one
	 * or more book entries.
	 * 
	 * @param bookInitials
	 *            the book to use
	 * @param reference
	 *            a reference, appropriate for the book, of one or more entries
	 */
	public SAXEventProvider getOSIS(Book book, String reference, int maxKeyCount)
			throws BookException, NoSuchKeyException {
		Key key = null;
		if (BookCategory.BIBLE.equals(book.getBookCategory())) {
			key = book.getKey(reference);
			((Passage) key).trimVerses(maxKeyCount);
		} else {
			key = book.createEmptyKeyList();

			Iterator<Key> iter = book.getKey(reference).iterator();
			int count = 0;
			while (iter.hasNext()) {
				if (++count >= maxKeyCount) {
					break;
				}
				key.addAll(iter.next());
			}
		}

		BookData data = new BookData(book, key);
		return data.getSAXEventProvider();
	}

	/**
	 * Gets the text for the given verse reference and also sets the finished product into the model this will always be CHAPTER
	 * @param bibleText the verse reference
	 * @return
	 */
	public void injectChapterFromJsword(BibleText bibleText) {

		Key key = Utils.currentChapter(((SwordBibleText) bibleText).getKey());

		if (bibleText.getTranslation().getBookCategory().equals(BookCategory.COMMENTARY)) {
			key = Utils.getVerseKey(key, 1);
		}
		
		
		ParsedDataSet dataSet = readSpannableStringBuilderJsword(bibleText.getTranslation(), key, 200);
		bibleText.setBoundaries(dataSet.getBoundaries());
		bibleText.setSpannableStringBuilder(dataSet.getVerse());
	}

	
	/**
	 * Gets the text for the given verse reference and also sets the finished product into the model can be single verse
	 * @param bibleText the verse reference
	 * @return
	 */
	public void injectKeyFromJsword(BibleText bibleText) {
		ParsedDataSet dataSet = readSpannableStringBuilderJsword(bibleText.getTranslation(), ((SwordBibleText) bibleText).getKey(), 200);
		bibleText.setBoundaries(dataSet.getBoundaries());
		bibleText.setSpannableStringBuilder(dataSet.getVerse());
	}

	
	
//    /**
//     * Get just the canonical text of one or more book entries without any
//     * markup.
//     * 
//     * @param bookInitials
//     *            the book to use
//     * @param reference
//     *            a reference, appropriate for the book, of one or more entries
//     */
//    public String getCanonicalText(Book book, Key key) throws NoSuchKeyException, BookException, ParseException {
//    	try {
//			BookData data = new BookData(book, key);
//			SAXEventProvider osissep = data.getSAXEventProvider();
//		
//			ContentHandler osisHandler = new OsisToCanonicalTextSaxHandler();
//		
//			osissep.provideSAXEvents(osisHandler);
//		
//			return osisHandler.toString();
//    	} catch (Exception e) {
//    		Log.e(TAG, "Error getting text from book" , e);
//    		return BibleApplication.getApplication().getString(R.string.error_occurred);
//    	}
//    }
//
//    private SAXParser saxParser;
//    private SAXParser getSAXParser() throws ParseException {
//    	try {
//	    	if (saxParser==null) {
//	    		SAXParserFactory spf = SAXParserFactory.newInstance();
//	    		spf.setValidating(false);
//	   			saxParser = spf.newSAXParser();
//	    	}
//		} catch (Exception e) {
//			log.error("SAX parser error", e);
//			throw new ParseException("SAX parser error", e);
//		}
//		return saxParser;
//    }
//    /**
//     * Get just the canonical text of one or more book entries without any
//     * markup.
//     * 
//     * @param bookInitials
//     *            the book to use
//     * @param reference
//     *            a reference, appropriate for the book, of one or more entries
//     */
//    public String getPlainText(Book book, String reference, int maxKeyCount) throws BookException, NoSuchKeyException {
//    	String plainText = "";
//    	try {
//    		if (book != null) {
//		        Key key = book.getKey(reference);
//		        BookData data = new BookData(book, key);
//		        plainText = OSISUtil.getCanonicalText(data.getOsisFragment());
//    		}
//    	} catch (Exception e) {
//    		Log.e(TAG, "Error getting plain text", e);
//    	}
//    	return plainText;
//    }
//
//	public Key search(Book bible, String searchText) throws BookException {
//// 		  example of fetching Strongs ref - only works with downloaded indexes!
////        Book book = getDocumentByInitials("KJV");
////        Key key1 = book.find("strong:h3068");
////        System.out.println("*** h3068 result count:"+key1.getCardinality());
//
//		Log.d(TAG,	"Searching:"+bible+" Search term:" + searchText);
//		
//		// This does a standard operator search. See the search
//		// documentation for more examples of how to search
//		Key key = bible.find(searchText); //$NON-NLS-1$
//
//		Log.d(TAG,	"There are "+key.getCardinality()+" verses containing " + searchText);
//
//		return key;
//
//	}
//
//	private OsisToHtmlSaxHandler getSaxHandler(Book book, Key key) {
//		OsisToHtmlParameters osisToHtmlParameters = new OsisToHtmlParameters();
//		BookMetaData bmd = book.getBookMetaData();
//		osisToHtmlParameters.setLeftToRight(bmd.isLeftToRight());
//		osisToHtmlParameters.setLanguageCode(book.getLanguage().getCode());
//		
//		// a basis for partial references
//		osisToHtmlParameters.setBasisRef(key);
//		
//		try {
//			String plainText = getPlainText(book, key.getOsisRef(), 99);
//		} catch (BookException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if (isAndroid) {
//			// size of padding at bottom depends on screen size
//			osisToHtmlParameters.setNumPaddingBrsAtBottom(BibleApplication.getApplication().getResources().getInteger(R.integer.br_count_at_bottom));
//	    	
//	    	// use old style discreet references if viewing a bible
//	    	osisToHtmlParameters.setBibleStyleNotesAndRefs(BookCategory.BIBLE.equals(book.getBookCategory()));
//	    	
//			SharedPreferences preferences = CommonUtils.getSharedPreferences();
//			if (preferences!=null) {
//				// show verse numbers if user has selected to show verse numbers AND the book is a bible (so don't even try to show verses in a Dictionary)
//				if (BookCategory.BIBLE.equals(book.getBookCategory())) {
//					osisToHtmlParameters.setShowVerseNumbers(preferences.getBoolean("show_verseno_pref", true) && book.getBookCategory().equals(BookCategory.BIBLE));
//					osisToHtmlParameters.setVersePerline(preferences.getBoolean("verse_per_line_pref", false));
//					osisToHtmlParameters.setShowNotes(preferences.getBoolean("show_notes_pref", true));
//					osisToHtmlParameters.setShowStrongs(preferences.getBoolean("show_strongs_pref", true));
//					osisToHtmlParameters.setShowMorphology(preferences.getBoolean("show_morphology_pref", false));
//					osisToHtmlParameters.setShowTitles(preferences.getBoolean("section_title_pref", true));
//					osisToHtmlParameters.setRedLetter(preferences.getBoolean("red_letter_pref", false));
//				}
//				if (preferences.getBoolean("night_mode_pref", false)) {
//					osisToHtmlParameters.setExtraStylesheet(NIGHT_MODE_STYLESHEET);
//				}
//				if (book.getBookCategory().equals(BookCategory.DICTIONARY)) {
//					if (book.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
//						//add allHebrew refs link
//						String prompt = BibleApplication.getApplication().getString(R.string.all_hebrew_occurrences);
//						osisToHtmlParameters.setExtraFooter("<br /><a href='"+Constants.ALL_HEBREW_OCCURRENCES_PROTOCOL+":"+key.getName()+"' class='allStrongsRefsLink'>"+prompt+"</a>");
//					} else if (book.hasFeature(FeatureType.GREEK_DEFINITIONS)) {
//						//add allGreek refs link
//						String prompt = BibleApplication.getApplication().getString(R.string.all_greek_occurrences);
//						osisToHtmlParameters.setExtraFooter("<br /><a href='"+Constants.ALL_GREEK_OCCURRENCES_PROTOCOL+":"+key.getName()+"' class='allStrongsRefsLink'>"+prompt+"</a>");
//					}
//				}
//				
//				//TODO **** do not reload every time ***
//				//keep reloading properties file for now to allow user edits to be actioned without a restart 
//				FontControl.getInstance().reloadProperties();
//				// which font, if any
//				osisToHtmlParameters.setFont(FontControl.getInstance().getFontForBook(book));				
//			}
//		}
//		OsisToHtmlSaxHandler osisToHtml = new OsisToHtmlSaxHandler(osisToHtmlParameters);
//		
//		return osisToHtml;
//	}
//	
//	public static void setAndroid(boolean isAndroid) {
//		SwordContentFacade.isAndroid = isAndroid;
//	}
}

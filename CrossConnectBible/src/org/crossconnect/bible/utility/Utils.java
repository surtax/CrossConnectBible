package org.crossconnect.bible.utility;

import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleBook;

import android.content.SharedPreferences;
import android.util.Log;


/**
 * @author glo1
 *
 */
public class Utils {
	
	public static boolean isVerse(Key key){
		return Verse.class.isInstance(key);
	}

	public static boolean isVerseRange(Key key){
		return VerseRange.class.isInstance(key);
	}
	
	public static Key prevChapter(Key key) {
		Verse verse;
		//Get verse to work with
		if(Utils.isVerse(key)) {
			verse = (Verse) key;
		} else if (Utils.isVerseRange(key)) {
			verse = ((VerseRange) key).getStart();
		} else {
			throw new RuntimeException("Unsupported Type for next chapter key");			
		}
		
		//Check if start of Bible
		if ((verse.getChapter() == 1 && verse.getBook().equals(BibleBook.GEN))){
			return key;				
		} else {
			return new VerseRange(verse.getFirstVerseInChapter().subtract(1).getFirstVerseInChapter(), verse.getFirstVerseInChapter().subtract(1));
		}

		
		
		
//		if(TextUtility.isVerse((key))){
//			//Check if it is the first book of Genesis
//			if (((Verse) key).getChapter() == 1 && ((Verse) key).getBook().equals(BibleBook.GEN)){
//				return key;				
//			}
//			return new VerseRange(((Verse) key).getFirstVerseInChapter().subtract(1).getFirstVerseInChapter(), ((Verse) key).getFirstVerseInChapter().subtract(1));
//		} else if (TextUtility.isVerseRange((key))) {
//			//TODO: what if the book is not a bible book?
//			
//			//Check if it is the first book of Genesis
//			if (((VerseRange) key).getStart().getChapter() == 1 && ((VerseRange) key).getStart().getBook().equals(BibleBook.GEN)){
//				return key;				
//			}
//			//Else just the chapter reference
//			return new VerseRange(((VerseRange) key).getStart().getFirstVerseInChapter().subtract(1).getFirstVerseInChapter(),((VerseRange) key).getStart().getFirstVerseInChapter().subtract(1));
//		} else {
//			throw new RuntimeException("Unsupported Type for next chapter key");
//		}
	}
	
	public static Key nextChapter(Key key) {
		Verse verse;
		//Get verse to work with
		if(Utils.isVerse(key)) {
			verse = (Verse) key;
		} else if (Utils.isVerseRange(key)) {
			verse = ((VerseRange) key).getEnd();
		} else {
			throw new RuntimeException("Unsupported Type for next chapter key");			
		}
		
		//Check if end of Bible
		if (verse.getLastVerseInBook().equals(verse.getLastVerseInChapter()) && verse.getBook().equals(BibleBook.REV)){
			return key;				
		} else {
			return new VerseRange(verse.getLastVerseInChapter().add(1).getFirstVerseInChapter(), verse.getLastVerseInChapter().add(1).getLastVerseInChapter());
		}

		
		
		
		
		
		
//		if(TextUtility.isVerse((key))){
//			//Does not return next but simply returns current chapter
//			return new VerseRange(((Verse) key).getFirstVerseInChapter(), ((Verse) key).getLastVerseInChapter());
//		} else if (TextUtility.isVerseRange((key))) {
//			//Else just the chapter reference
//			if (((VerseRange) key).getEnd().getLastVerseInBook().equals(((VerseRange) key).getEnd())  && ((VerseRange) key).getEnd().getBook().equals(BibleBook.REV)){
//				return key;				
//			}
//			return new VerseRange(((VerseRange) key).getEnd().getLastVerseInChapter().add(1),((VerseRange) key).getEnd().getLastVerseInChapter().add(1).getLastVerseInChapter());
//		} else {
//			throw new RuntimeException("Unsupported Type for next chapter key");
//		}
	}
	
	/**
	 * Give back a verse number given a key - if a chapter ref will give back verse 1
	 * @param key
	 * @return
	 */
	public static int getVerse(Key key) {
		if(Utils.isVerse((key))){
			return ((Verse) key).getVerse();
		} else if (Utils.isVerseRange((key))) {
			return ((VerseRange) key).getStart().getVerse();
		} else {
			throw new RuntimeException("Unsupported Type for next chapter key");
		}
	}

	
	
	/**
	 * 
	 * Get the key representing the whole chapter
	 * @param key assumption is key verseranges are always WHOLE CHAPTERS
	 * @return key that represents the whole chapter
	 */
	public static Key currentChapter(Key key) {
		if(Utils.isVerse((key))){
			//Returns current chapter
			return new VerseRange(((Verse) key).getFirstVerseInChapter(), ((Verse) key).getLastVerseInChapter());
		} else if (Utils.isVerseRange((key))) {
			return key;
		} else {
			throw new RuntimeException("Unsupported Type for next chapter key");
		}
	}

	/**
	 * Get the Key object of the current key's chapter given a specific verse
	 * @param key
	 * @param verse
	 * @return
	 */
	public static Verse getVerseKey(Key key, int verse) {
		try {
		if(Utils.isVerse((key))){
			//Returns current chapter
			Verse currentVerse = (Verse) key;
			return new Verse(currentVerse.getBook(), currentVerse.getChapter(), verse);
		} else if (Utils.isVerseRange((key))) {
			Verse currentVerse = ((VerseRange) key).getStart();
			return new Verse(currentVerse.getBook(), currentVerse.getChapter(), verse);
		} else {
		}
		} catch (NoSuchVerseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Unsupported Type for next chapter key");		
	}
	
	public static String getBookChapter(Key key) {
		//Default it to New for new windows 
		String referenceTxt = "New";
		try {
			//If singular verse just get whole chapter
			if(Utils.isVerse((key))){
				referenceTxt = ((Verse) key).getBook().getBookName() + " " + ((Verse) key).getChapter();
			} else if (Utils.isVerseRange((key))) {
				//Else just the chapter reference
				referenceTxt = ((VerseRange) key).getStart().getBook().getBookName() + " " + ((VerseRange) key).getStart().getChapter();
			}
		} catch (Exception e) {
			
		}
		return referenceTxt;
	}
	
	public static String getBook(Key key) {
		try {
			if(Utils.isVerse((key))){
				return ((Verse) key).getBook().getLongName();
			} else if (Utils.isVerseRange((key))) {
				//Else just the chapter reference
				return ((VerseRange) key).getStart().getBook().getLongName();
			} else {
				Log.e("BibleBook is zero", "Should never occur");
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getShortBook(Key key) {
		try {
			if(Utils.isVerse((key))){
				return ((Verse) key).getBook().getShortName() + " " + ((Verse) key).getChapter() + ":" + ((Verse) key).getVerse();
			} else if (Utils.isVerseRange((key))) {
				//Else just the chapter reference
				return ((VerseRange) key).getStart().getBook().getShortName();
			} else {
				Log.e("BibleBook is zero", "Should never occur");
			}
		} catch (Exception e) {
		}
		return null;
	}


	public static int getChapter(Key key) {
		if(Utils.isVerse((key))){
			return ((Verse) key).getChapter();
		} else if (Utils.isVerseRange((key))) {
			//Else just the chapter reference
			return ((VerseRange) key).getStart().getChapter();
		} else {
			Log.e("Chapter is zero", "Should never occur");
			return 0;
		}
	}

   public static int getNumVerses(Key key) {
        try {
            if(Utils.isVerse((key))){
                return ((Verse) key).getLastVerseInBook().getChapter();
            } else if (Utils.isVerseRange((key))) {
                //Else just the chapter reference
                return ((VerseRange) key).getStart().getLastVerseInBook().getChapter();
            } else {
                Log.e("BibleBook is zero", "Should never occur");
            }
        } catch (Exception e) {
        }
        return -1;
    }
   
   /**
    * Load most recent BibleText from preferences
    */
   public static BibleText loadBibleText(SharedPreferences pref) {
       //Load last opened verse
       String bibleBook = pref.getString(SharedPreferencesHelper.CURRENT_BOOK, "Philipiians");
       int chapter = Integer.valueOf(pref.getString(SharedPreferencesHelper.CURRENT_CHAPTER, "1"));
       int verse = Integer.valueOf(pref.getString(SharedPreferencesHelper.CURRENT_VERSE, "1"));
       String translation = pref.getString(SharedPreferencesHelper.CURRENT_TRANSLATION, "ESV");
       return new SwordBibleText(bibleBook, chapter, verse, translation);
   }

   public static void saveCurrentPassage(BibleText bibleText, SharedPreferences settings, int verseNumber) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SharedPreferencesHelper.CURRENT_BOOK, bibleText.getBook());
        editor.putString(SharedPreferencesHelper.CURRENT_CHAPTER, String.valueOf(bibleText.getChapter()));
        editor.putString(SharedPreferencesHelper.CURRENT_VERSE, String.valueOf(verseNumber));
        editor.putString(SharedPreferencesHelper.CURRENT_TRANSLATION, bibleText.getTranslation().getInitials());
        editor.commit();
    }
   
	//
//    private static TextUtility swordScv;
//    
//    private SpannableStringBuilder sb = new SpannableStringBuilder();
//    
//    private List<Integer> textBoundaries = new ArrayList<Integer>();
//    
//    private TextUtility(){
//    }
//    
//    public static TextUtility getInstance(){
//        if (swordScv == null) {
//            swordScv = new TextUtility();
//        }
//        
//        return swordScv;
//    }
//    
//    /**
//     * This method should return the final output of what needs to be displayed
//     * 
//     * This will include appending the disclaimers as required, should be fed straight
//     * into the BibleTextViewImpl
//     * 
//     * @return the text for output
//     */
//    public SpannableStringBuilder getText(BibleText verseReference){
//        return sb;
//    }
//    
//    public List<Integer> getTxtBoundaries(){
//    	
//    	List<Integer> textBoundaries = new ArrayList<Integer>();
////    	0, 46, 92, 138, 229, 365, 412, 780, 920, 1014]
//    	textBoundaries.add(0);
//    	textBoundaries.add(46);
//    	textBoundaries.add(92);
//    	textBoundaries.add(138);
//    	textBoundaries.add(229);
//    	textBoundaries.add(365);
//    	textBoundaries.add(412);
//    	textBoundaries.add(780);
//    	textBoundaries.add(920);
//
//    	//THE LAST BOUNDARY IS ALWAYS THE END
//    	textBoundaries.add(1014);
//    	
//    	
//        return textBoundaries;
//    }
//    
//    public void setTextBoundaries(List<Integer> boundaries) {
//		textBoundaries = boundaries;
//    }
//    
//    /**
//     * Get which verse the given character is part of
//     * @return which verse this character belongs to
//     */
//    public int getVerseFromCharacterPos(int characterPos) {
//        Log.e("Selected Char Pos", "CharPos is: " +  characterPos);
//
//    	int verse = 0;
//        
//        List<Integer> boundaries = getTxtBoundaries();
//        
//        Iterator<Integer> it = boundaries.iterator();
//        
//        //Loop through the boundaries to find out which verse was selected and highlight it
//        while (it.hasNext()) {
//            Integer currentBoundary = (Integer) it.next();
//            if (characterPos < currentBoundary) {
//                Log.e("SelectedVerse", "Verse is: " +  verse);
//                return verse;
//            } else if (it.hasNext() == false) {
//                return ++verse;
//            }
//            verse++;
//
//        }
//        // TODO: Character is out of bounds should throw exception?
//        return -1;
//    }
}

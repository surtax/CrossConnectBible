package com.crossconnect.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BibleDataHelper {
    
    public static final int  LAW_BOOKS = 5;
    public static final int  LAW_POSITION = 1;
    
    public static final int  FORMER_PROPHETS_BOOKS = 13;
    public static final int  FORMER_PROPHETS_POSITION = FORMER_PROPHETS_BOOKS + LAW_BOOKS;

    public static final int  WRITINGS_BOOKS = 4;
    public static final int  WRITINGS_POSITION = FORMER_PROPHETS_POSITION + WRITINGS_BOOKS;

    
    public static final int LATER_PROPHETS_BOOKS = 17; 
    public static final int  LATER_PROPHETS_POSITION = WRITINGS_POSITION + LATER_PROPHETS_BOOKS;

    public static final int  GOSPELS_BOOKS = 5;
    public static final int  GOSPELS_POSITION = LATER_PROPHETS_POSITION + GOSPELS_BOOKS;

    public static final int PAUL_BOOKS = 9;
    public static final int  PAUL_POSITION = GOSPELS_POSITION + PAUL_BOOKS;

    public static final int PASTORAL_BOOKS = 5; 
    public static final int  PASTORAL_POSITION = PAUL_POSITION + PASTORAL_BOOKS;

    public static final int GENERAL_BOOKS = 8;
    public static final int  GENERAL_POSITION = PASTORAL_POSITION + GENERAL_BOOKS;
    
    public static final String GENESIS ="Genesis";
    public static final String EXODUS ="Exodus";
    public static final String LEVITICUS ="Leviticus";
    public static final String NUMBERS ="Numbers";
    public static final String DEUTERONOMY ="Deuteronomy";
    public static final String JOSHUA ="Joshua";
    public static final String JUDGES ="Judges";
    public static final String RUTH ="Ruth";
    public static final String ONE_SAMUEL ="1 Samuel";
    public static final String TWO_SAMUEL ="2 Samuel";
    public static final String ONE_KINGS ="1 Kings";
    public static final String TWO_KINGS ="2 Kings";
    public static final String ONE_CHRONICLES="1 Chronicles";
    public static final String TWO_CHRONICLES ="2 Chronicles";
    public static final String EZRA ="Ezra";
    public static final String NEHEMIAH ="Nehemiah";
    public static final String ESTHER ="Esther";
    public static final String JOB ="Job";
    public static final String PSALMS="Psalms";
    public static final String PROVERBS ="Proverbs";
    public static final String ECCLESIASTES ="Ecclesiastes";
    public static final String SONGS ="Song of Solomon";
    public static final String ISAIAH ="Isaiah";
    public static final String JEREMIAH ="Jeremiah";
    public static final String LAMENTATIONS ="Lamentations";
    public static final String EZEKIEL ="Ezekiel";
    public static final String DANIEL ="Daniel";
    public static final String HOSEA ="Hosea";
    public static final String JOEL ="Joel";
    public static final String AMOS ="Amos";
    public static final String OBADIAH ="Obadiah";
    public static final String JONAH ="Jonah";
    public static final String MICAH ="Micah";
    public static final String NAHUM ="Nahum";
    public static final String HABAKUK ="Habakkuk";
    public static final String ZEPHANIAH ="Zephaniah";
    public static final String HAGGAI ="Haggai";
    public static final String ZECHARIAH ="Zechariah";
    public static final String MALACHI ="Malachi";
    public static final String MATTHEW ="Matthew";
    public static final String MARK ="Mark";
    public static final String LUKE ="Luke";
    public static final String JOHN ="John";
    public static final String ACTS ="Acts";
    public static final String ROMANS ="Romans";
    public static final String ONE_CORINTHIANS ="1 Corinthians";
    public static final String TWO_CORINTHIANS ="2 Corinthians";
    public static final String GALATIONS ="Galatians";
    public static final String EPHESIANS ="Ephesians";
    public static final String PHILIPPIANS ="Philippians";
    public static final String COLOSSIANS ="Colossians";
    public static final String ONE_THESSALONIANS ="1 Thessalonians";
    public static final String TWO_THESSALONIANS ="2 Thessalonians";
    public static final String ONE_TIMOTHY ="1 Timothy";
    public static final String TWO_TIMOTHY ="2 Timothy";
    public static final String TITUS ="Titus";
    public static final String PHILEMON ="Philemon";
    public static final String HEBREWS ="Hebrews";
    public static final String JAMES ="James";
    public static final String ONE_PETER ="1 Peter";
    public static final String TWO_PETER ="2 Peter";
    public static final String ONE_JOHN ="1 John";
    public static final String TWO_JOHN ="2 John";
    public static final String THREE_JOHN="3 John";
    public static final String JUDE ="Jude";
    public static final String REVELATION ="Revelation of John";
    
    public static final String WHOLE_BIBLE = "Whole Bible";
    public static final String NT = "New Testament";
    public static final String OT = "Old Testament";

    private static List<String> books = new ArrayList<String>();
    private static List<String> lowerCaseBooks = new ArrayList<String>();

    private static List<Integer> chapters = new ArrayList<Integer>();
    
//    public static String bookChapterRegex;

    static {
        books.add("Genesis");
        books.add("Exodus");
        books.add("Leviticus");
        books.add("Numbers");
        books.add("Deuteronomy");
        books.add("Joshua");
        books.add("Judges");
        books.add("Ruth");
        books.add("1 Samuel");
        books.add("2 Samuel");
        books.add("1 Kings");
        books.add("2 Kings");
        books.add("1 Chronicles");
        books.add("2 Chronicles");
        books.add("Ezra");
        books.add("Nehemiah");
        books.add("Esther");
        books.add("Job");
        books.add(PSALMS);
        books.add("Proverbs");
        books.add("Ecclesiastes");
        books.add(SONGS);
        books.add("Isaiah");
        books.add("Jeremiah");
        books.add("Lamentations");
        books.add("Ezekiel");
        books.add("Daniel");
        books.add("Hosea");
        books.add("Joel");
        books.add("Amos");
        books.add("Obadiah");
        books.add("Jonah");
        books.add("Micah");
        books.add("Nahum");
        books.add("Habakkuk");
        books.add("Zephaniah");
        books.add("Haggai");
        books.add("Zechariah");
        books.add("Malachi");
        books.add("Matthew");
        books.add("Mark");
        books.add("Luke");
        books.add("John");
        books.add("Acts");
        books.add("Romans");
        books.add("1 Corinthians");
        books.add("2 Corinthians");
        books.add("Galatians");
        books.add("Ephesians");
        books.add("Philippians");
        books.add("Colossians");
        books.add("1 Thessalonians");
        books.add("2 Thessalonians");
        books.add("1 Timothy");
        books.add("2 Timothy");
        books.add("Titus");
        books.add("Philemon");
        books.add("Hebrews");
        books.add("James");
        books.add("1 Peter");
        books.add("2 Peter");
        books.add("1 John");
        books.add("2 John");
        books.add("3 John");
        books.add("Jude");
        books.add(REVELATION);
        
//        StringBuilder sb = new StringBuilder("(");
//        
//        for (String book: books) {
//            sb.append(book);
//            sb.append("|");
//            lowerCaseBooks.add(book.toLowerCase());
//        }
//        sb.setCharAt(sb.length()-1, ')');
//        sb.append(" \\d*");
//        bookChapterRegex = sb.toString();
        
        
        // Chapters per book
        chapters.add(50);
        chapters.add(40);
        chapters.add(27);
        chapters.add(36);
        chapters.add(34);
        chapters.add(24);
        chapters.add(21);
        chapters.add(4);
        chapters.add(31);
        chapters.add(24);
        chapters.add(22);
        chapters.add(25);
        chapters.add(29);
        chapters.add(36);
        chapters.add(10);
        chapters.add(13);
        chapters.add(10);
        chapters.add(42);
        chapters.add(150);
        chapters.add(31);
        chapters.add(12);
        chapters.add(8);
        chapters.add(66);
        chapters.add(52);
        chapters.add(5);
        chapters.add(48);
        chapters.add(12);
        chapters.add(14);
        chapters.add(3);
        chapters.add(9);
        chapters.add(1);
        chapters.add(4);
        chapters.add(7);
        chapters.add(3);
        chapters.add(3);
        chapters.add(3);
        chapters.add(2);
        chapters.add(14);
        chapters.add(4);
        chapters.add(28);
        chapters.add(16);
        chapters.add(24);
        chapters.add(21);
        chapters.add(28);
        chapters.add(16);
        chapters.add(16);
        chapters.add(13);
        chapters.add(6);
        chapters.add(6);
        chapters.add(4);
        chapters.add(4);
        chapters.add(5);
        chapters.add(3);
        chapters.add(6);
        chapters.add(4);
        chapters.add(3);
        chapters.add(1);
        chapters.add(13);
        chapters.add(5);
        chapters.add(5);
        chapters.add(3);
        chapters.add(5);
        chapters.add(1);
        chapters.add(1);
        chapters.add(1);
        chapters.add(22);
    }
    
    
    
//    public static void main(String[] args) {
//
//        System.out.println(String.format(KjvAudioMap().get("Genesis"), threeDigitNumber("25")));
//    }

    public static String threeDigitNumber(String numbersOnly) {
        int chapter = Integer.parseInt(numbersOnly);
        if (chapter < 10) {
            return "00" + chapter;
        } else if (chapter < 100) {
            return "0" + chapter;
        } else {
            return "" + chapter;
        }
    }

    /**
     * Url mapping used by mp3bible.org
     * @return the map containing the template urls for all mp3 mappings
     */
    public static Map<String, String> KjvAudioMap() {
        final String AUDIO_HOST = "http://mp3bible.org/kjv/audio/";
        final String MP3_EXT = "%s.mp3";
        // 62_1Jo/621Jo
        Map<String, String> books = new HashMap<String, String>();
        books.put("Genesis", AUDIO_HOST + "01_Gen/01Gen" + MP3_EXT);
        books.put("Exodus", AUDIO_HOST + "02_Exo/02Exo" + MP3_EXT);
        books.put("Leviticus", AUDIO_HOST + "03_Levi/03Levi" + MP3_EXT);
        books.put("Numbers", AUDIO_HOST + "04_Num/04Num" + MP3_EXT);
        books.put("Deuteronomy", AUDIO_HOST + "05_Deu/05Deu" + MP3_EXT);
        books.put("Joshua", AUDIO_HOST + "06_Jos/06Jos" + MP3_EXT);
        books.put("Judges", AUDIO_HOST + "07_Jdg/07Jdg" + MP3_EXT);
        books.put("Ruth", AUDIO_HOST + "08_Rth/08Rth" + MP3_EXT);
        books.put("1Samuel", AUDIO_HOST + "09_1Sa/091Sa" + MP3_EXT);
        books.put("2Samuel", AUDIO_HOST + "10_2Sa/102Sa" + MP3_EXT);
        books.put("1Kings", AUDIO_HOST + "11_1Ki/111Ki" + MP3_EXT);
        books.put("2Kings", AUDIO_HOST + "12_2Ki/122Ki" + MP3_EXT);
        books.put("1Chronicles", AUDIO_HOST + "13_1Ch/131Ch" + MP3_EXT);
        books.put("2Chronicles", AUDIO_HOST + "14_2Ch/142Ch" + MP3_EXT);
        books.put("Ezra", AUDIO_HOST + "15_Ezr/15Ezr" + MP3_EXT);
        books.put("Nehemiah", AUDIO_HOST + "16_Neh/16Neh" + MP3_EXT);
        books.put("Esther", AUDIO_HOST + "17_Est/17Est" + MP3_EXT);
        books.put("Job", AUDIO_HOST + "18_Job/18Job" + MP3_EXT);
        books.put(PSALMS, AUDIO_HOST + "19_Psa/19Psa" + MP3_EXT);
        books.put("Proverbs", AUDIO_HOST + "20_Pro/20Pro" + MP3_EXT);
        books.put("Ecclesiastes", AUDIO_HOST + "21_Ecc/21Ecc" + MP3_EXT);
        books.put(SONGS, AUDIO_HOST + "22_Son/22Son" + MP3_EXT);
        books.put("Isaiah", AUDIO_HOST + "23_Isa/23Isa" + MP3_EXT);
        books.put("Jeremiah", AUDIO_HOST + "24_Jer/24Jer" + MP3_EXT);
        books.put("Lamentations", AUDIO_HOST + "25_Lam/25Lam" + MP3_EXT);
        books.put("Ezekiel", AUDIO_HOST + "26_Eze/26Eze" + MP3_EXT);
        books.put("Daniel", AUDIO_HOST + "27_Dan/27Dan" + MP3_EXT);
        books.put("Hosea", AUDIO_HOST + "28_Hos/28Hos" + MP3_EXT);
        books.put("Joel", AUDIO_HOST + "29_Joe/29Joe" + MP3_EXT);
        books.put("Amos", AUDIO_HOST + "30_Amo/30Amo" + MP3_EXT);
        books.put("Obadiah", AUDIO_HOST + "31_Oba/31Oba" + MP3_EXT);
        books.put("Jonah", AUDIO_HOST + "32_Jon/32Jon" + MP3_EXT);
        books.put("Micah", AUDIO_HOST + "33_Mic/33Mic" + MP3_EXT);
        books.put("Nahum", AUDIO_HOST + "34_Nah/34Nah" + MP3_EXT);
        books.put("Habakkuk", AUDIO_HOST + "35_Hab/35Hab" + MP3_EXT);
        books.put("Zephaniah", AUDIO_HOST + "36_Zep/36Zep" + MP3_EXT);
        books.put("Haggai", AUDIO_HOST + "37_Hag/37Hag" + MP3_EXT);
        books.put("Zechariah", AUDIO_HOST + "38_Zec/38Zec" + MP3_EXT);
        books.put("Malachi", AUDIO_HOST + "39_Mal/39Mal" + MP3_EXT);
        books.put("Matthew", AUDIO_HOST + "40_Mat/40Mat" + MP3_EXT);
        books.put("Mark", AUDIO_HOST + "41_Mar/41Mar" + MP3_EXT);
        books.put("Luke", AUDIO_HOST + "42_Luk/42Luk" + MP3_EXT);
        books.put("John", AUDIO_HOST + "43_Joh/43Joh" + MP3_EXT);
        books.put("Acts", AUDIO_HOST + "44_Act/44Act" + MP3_EXT);
        books.put("Romans", AUDIO_HOST + "45_Rom/45Rom" + MP3_EXT);
        books.put("1Corinthians", AUDIO_HOST + "46_1Co/461Co" + MP3_EXT);
        books.put("2Corinthians", AUDIO_HOST + "47_2Co/472Co" + MP3_EXT);
        books.put("Galatians", AUDIO_HOST + "48_Gal/48Gal" + MP3_EXT);
        books.put("Ephesians", AUDIO_HOST + "49_Eph/49Eph" + MP3_EXT);
        books.put("Philippians", AUDIO_HOST + "50_Php/50Php" + MP3_EXT);
        books.put("Colossians", AUDIO_HOST + "51_Col/51Col" + MP3_EXT);
        books.put("1Thessalonians", AUDIO_HOST + "52_1Th/521Th" + MP3_EXT);
        books.put("2Thessalonians", AUDIO_HOST + "53_2Th/532Th" + MP3_EXT);
        books.put("1Timothy", AUDIO_HOST + "54_1Ti/541Ti" + MP3_EXT);
        books.put("2Timothy", AUDIO_HOST + "55_2Ti/552Ti" + MP3_EXT);
        books.put("Titus", AUDIO_HOST + "56_Tts/56Tts" + MP3_EXT);
        books.put("Philemon", AUDIO_HOST + "57_Phm/57Phm" + MP3_EXT);
        books.put("Hebrews", AUDIO_HOST + "58_Heb/58Heb" + MP3_EXT);
        books.put("James", AUDIO_HOST + "59_Jam/59Jam" + MP3_EXT);
        books.put("1Peter", AUDIO_HOST + "60_1Pe/601Pe" + MP3_EXT);
        books.put("2Peter", AUDIO_HOST + "61_2Pe/612Pe" + MP3_EXT);
        books.put("1John", AUDIO_HOST + "62_1Jo/621Jo" + MP3_EXT);
        books.put("2John", AUDIO_HOST + "63_2Jo/632Jo" + MP3_EXT);
        books.put("3John", AUDIO_HOST + "64_3Jo/643Jo" + MP3_EXT);
        books.put("Jude", AUDIO_HOST + "65_Jde/65Jde" + MP3_EXT);
        books.put("Revelation", AUDIO_HOST + "66_Rev/66Rev" + MP3_EXT);

        return books;
    }
    
    public static String[] getSearchScope() {
    	List<String> books = getBooks();
    	books.add(0, OT);
    	books.add(0, NT);
    	books.add(0, WHOLE_BIBLE);
    	String[] scope = books.toArray(new String[0]);
    	return scope;
    }

    public static List<String> getBooks() {
        return books;
    }
    
    public static List<String> getLowerCaseBooks() {
        return lowerCaseBooks;
    }


    public static List<Integer> getChapters() {
        return chapters;
    }
    
    /**
     * Convert to XC Standard naming conventions
     * @param book any book name
     * @return XC Standard named book
     */
    public static String bookstringToXCBook(String book){
        if(book.equals("Psalms")){
            return PSALMS;
        } else if (book.equals("I Corinthians")){
            return ONE_CORINTHIANS;
        } else if (book.equals("II Corinthians")){
            return TWO_CORINTHIANS;
        } else if (book.equals("I Timothy")){
            return ONE_TIMOTHY;
        } else if (book.equals("II Timothy")){
            return TWO_TIMOTHY;
        } else if (book.equals("I Peter")){
            return ONE_PETER;
        } else if (book.equals("II Peter")){
            return TWO_PETER;
        } else if (book.equals("I John")){
            return ONE_JOHN;
        } else if (book.equals("II John")){
            return TWO_JOHN;
        } else if (book.equals("III John")){
            return THREE_JOHN;
        } else if (book.equals("I Kings")){
            return ONE_KINGS;
        } else if (book.equals("II Kings")){
            return TWO_KINGS;
        } else if (book.equals("I Chronicles")){
            return ONE_CHRONICLES;
        } else if (book.equals("II Chronicles")){
            return TWO_CHRONICLES;
        } else if (book.equals("I Samuel")){
            return ONE_SAMUEL;
        } else if (book.equals("II Samuel")){
            return TWO_SAMUEL;
        } else if (book.equals("I Thessalonians")){
            return ONE_THESSALONIANS;
        } else if (book.equals("II Thessalonians")){
            return TWO_THESSALONIANS;
        } 
        return book;
    }
    
    public static final int LAW_SECTION = 0;
    public static final int FORMER_PROPHET_SECTION = 1;
    public static final int WRITINGS_SECTION = 2;
    public static final int LATER_PROPHETS_SECTION = 3;
    public static final int GOSPELS_SECTION = 4;
    public static final int PAUL_SECTION = 5;
    public static final int PASTORAL_SECTION = 6;
    public static final int GENERAL_SECTION = 7;
    
    public static final int TOTAL_SECTIONS = 8;
    
    /**
     * Give as a decimal the position of the section of the bible 
     * @param position the book position
     * @return the category position as a decimal
     */
    public static double getSectionPosition (int position){
        int bookType;
        double sectionPosition; 
        if (position < LAW_BOOKS) {
            bookType = LAW_SECTION;
            sectionPosition = (position/(double) LAW_BOOKS);
        } else if (position < FORMER_PROPHETS_POSITION) {
            bookType = FORMER_PROPHET_SECTION;
            sectionPosition = ((position - LAW_BOOKS)/(double) FORMER_PROPHETS_BOOKS);
        } else if (position < WRITINGS_POSITION) {
            bookType = WRITINGS_SECTION;
            sectionPosition = ((position - FORMER_PROPHETS_POSITION)/(double) WRITINGS_BOOKS);
        } else if (position < LATER_PROPHETS_POSITION){
            bookType = LATER_PROPHETS_SECTION;
            sectionPosition = ((position - WRITINGS_POSITION)/(double) LATER_PROPHETS_BOOKS);
        } else if (position < GOSPELS_POSITION) {
            bookType = GOSPELS_SECTION;
            sectionPosition = ((position - LATER_PROPHETS_POSITION)/(double) GOSPELS_BOOKS);
        } else if (position < PAUL_POSITION){
            bookType = PAUL_SECTION;
            sectionPosition = ((position - GOSPELS_POSITION)/(double) PAUL_BOOKS);
        } else if (position < PASTORAL_POSITION) {
            bookType = PASTORAL_SECTION;
            sectionPosition = ((position - PAUL_POSITION)/(double) PASTORAL_BOOKS);
        } else {
            bookType = GENERAL_SECTION;
            sectionPosition = ((position - PASTORAL_POSITION)/(double) GENERAL_BOOKS);
        }
        //Need to check not negative 1?
        return sectionPosition  + bookType;

    }
    
    /**
     * Calculate which book we are up to given a section in decimal
     * @param section section of the bible we are up to i.e. half way through former prophets would be 1.5
     * @return the book position which we are up to
     */
    public static int getBookPosition (double section){
        int book = 0;
        
        //Should use a map to map from Section to Book position - i.e. PASTORAL_SECTIOn to PASTORAL_BOOK_POSITION        

        Map<Integer,Integer> sectionToBookPosition = new HashMap<Integer, Integer>();
        sectionToBookPosition.put(LAW_SECTION, 0);
        sectionToBookPosition.put(FORMER_PROPHET_SECTION, LAW_BOOKS);
        sectionToBookPosition.put(WRITINGS_SECTION, FORMER_PROPHETS_POSITION);
        sectionToBookPosition.put(LATER_PROPHETS_SECTION, WRITINGS_POSITION);
        sectionToBookPosition.put(GOSPELS_SECTION, LATER_PROPHETS_POSITION);
        sectionToBookPosition.put(PAUL_SECTION, GOSPELS_POSITION);
        sectionToBookPosition.put(PASTORAL_SECTION, PAUL_POSITION);
        sectionToBookPosition.put(GENERAL_SECTION, PASTORAL_POSITION);
        
        Map<Integer,Integer> sectionToNumBooks = new HashMap<Integer, Integer>();
        sectionToNumBooks.put(LAW_SECTION, LAW_BOOKS);
        sectionToNumBooks.put(FORMER_PROPHET_SECTION, FORMER_PROPHETS_BOOKS);
        sectionToNumBooks.put(WRITINGS_SECTION, WRITINGS_BOOKS);
        sectionToNumBooks.put(LATER_PROPHETS_SECTION, LATER_PROPHETS_BOOKS);
        sectionToNumBooks.put(GOSPELS_SECTION, GOSPELS_BOOKS);
        sectionToNumBooks.put(PAUL_SECTION, PAUL_BOOKS);
        sectionToNumBooks.put(PASTORAL_SECTION, PASTORAL_BOOKS);
        sectionToNumBooks.put(GENERAL_SECTION, GENERAL_BOOKS);
        

        //Formula would be book = SECTION-->SECTION_BOOK_POSITION + floor(partialSelection*partialSelection -->numBooks)
        //i.e. if section 2.34 --> get section 2         

        //Prevent it from going out of bounds
        int numSection = (int) Math.floor(section);
        if (numSection < 0) {
            return 0;
        } else if (numSection > 7) {
            //Last book
            return GENERAL_POSITION-1;
        }
        int bookPosition = sectionToBookPosition.get(numSection);
        int numBooks = sectionToNumBooks.get(numSection);
        double partialSection = section-numSection;
        book = bookPosition + (int) Math.floor(partialSection * numBooks);
        return book;
    }

}

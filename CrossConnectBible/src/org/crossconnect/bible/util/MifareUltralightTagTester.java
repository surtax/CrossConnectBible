package org.crossconnect.bible.util;

import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;
import java.io.IOException;
import java.nio.charset.Charset;

import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;
import org.crossconnect.bible.utility.Utils;



public class MifareUltralightTagTester {

    private static final String TAG = MifareUltralightTagTester.class.getSimpleName();

    public void writeTag(Tag tag, String tagText) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
        	// Smallest tag only has 64 bytes
        	if (tagText.length() / 4  < 12) {
                ultralight.connect();
        		for (int i = 0; i < ((tagText.length() / 4)); i ++ ) {
        			int end = (i+1) * 4 > tagText.length() ? tagText.length() : (i+1) * 4;
        			//Clear out the existing data
                    ultralight.writePage(i+4, "    ".getBytes(Charset.forName("US-ASCII")));
                    
                    //Write new data
        			ultralight.writePage(i+4, tagText.substring(i*4, end).getBytes(Charset.forName("US-ASCII")));
        			
                    Log.d(TAG, "NFC Writte:" + tagText.substring(i*4, end));
        		}
        	}
        } catch (IOException e) {
            Log.e(TAG, "IOException while closing MifareUltralight...", e);
        } finally {
            try {
                ultralight.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }
    }

    public String readTag(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(4);
            Log.d(TAG, "NFC Read:" + new String(payload, Charset.forName("US-ASCII")));
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
               try {
                   mifare.close();
               }
               catch (IOException e) {
                   Log.e(TAG, "Error closing tag...", e);
               }
            }
        }
        return null;
    }
    
    
    /**
     * Write bible verse will always use 4 pages starting from offset of 4
     * @param tag
     * @param tagText
     */
    public void writeBibleVerse(Tag tag, BibleText bibleText) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
        	// Clean out the data in the pages 4-8;
            ultralight.writePage(4, "    ".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(5, "    ".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(6, "    ".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(7, "    ".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(8, "    ".getBytes(Charset.forName("US-ASCII")));
            Log.d(TAG, "Existing data cleared");
        	
        	//Write the short name
            //Pad to 4 bytes
            ultralight.writePage(4,Utils.getShortBook(bibleText.getKey()).concat(" ").getBytes(Charset.forName("US-ASCII")));
            Log.d(TAG, "NFC Write:" + bibleText.getKey());

            //Write the chapter and verse as two bytes
            byte[] chapterVerseBytes = {Integer.valueOf(bibleText.getChapter()).byteValue(), Integer.valueOf(bibleText.getVerse()).byteValue(), 0, 0};
            ultralight.writePage(5,chapterVerseBytes);
            Log.d(TAG, "NFC Write:" + bibleText.getChapter() + ":" + bibleText.getVerse());

            //Pad to 4 bytes
            String translation = bibleText.getTranslation().getInitials();
            if (translation.length() < 4) {
            	while (translation.length() < 4) {
            		translation += " ";
            	}
            }
            
            ultralight.writePage(6,translation.getBytes(Charset.forName("US-ASCII")));
            Log.d(TAG, "NFC Write:" + bibleText.getTranslation().getInitials());
            
        } catch (IOException e) {
            Log.e(TAG, "IOException while closing MifareUltralight...", e);
        } finally {
            try {
                ultralight.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }
    }

    
    /**
     * Bible Verse will always be stored on the first 
     * @param tag
     * @return
     */
    public BibleText readBibleVerse(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(4);
            byte[] bookBytes = {payload[0],payload[1],payload[2],payload[3]};
            Byte chapterBytes= payload[4];
            Byte verseBytes= payload[5];
            byte[] translationBytes = {payload[8],payload[9],payload[10],payload[11]};
            
            
            
            
            String book = new String(bookBytes, Charset.forName("US-ASCII")).trim();
            String translation = new String(translationBytes, Charset.forName("US-ASCII")).trim();

            Log.d(TAG, "NFC Read: " + book + " " + chapterBytes.intValue() + ":" + verseBytes.intValue() + " " + translation); 

            return new SwordBibleText(book, chapterBytes.intValue(), verseBytes.intValue(), translation);
            
        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
               try {
                   mifare.close();
               }
               catch (IOException e) {
                   Log.e(TAG, "Error closing tag...", e);
               }
            }
        }
        return null;
    }

}

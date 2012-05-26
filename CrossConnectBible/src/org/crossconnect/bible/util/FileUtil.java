package org.crossconnect.bible.util;

import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.OnlineAudioResource;

/**
 * Util method for saving file name conventions
 */
public class FileUtil {
	
	public static final String META_SEPERATOR = "__";
	
	/**
	 * Construct from filename but this object will not have audio url available or read url
	 * @param fileName
	 */
	public static OnlineAudioResource getOnlineAudioResourceFromFileName(String fileName) {
		String[] metaData = fileName.replace(".mp3", "").split(META_SEPERATOR);
		
		//Make sure that there is 3 bits of meta data as expected
		if (metaData.length == 5) {
			// Empty String values allowed
//			this.resourceName = metaData[1];
//			// Empty Sting values allowed
//			this.resourceVerse = metaData[2];

		}
		return null;
	}
	
	
	/**
	 * Construct from filename but this object will not have audio url available or read url
	 * @param fileName
	 */
	public static BibleText getBiblETextFromFileName(String fileName) {
		String[] metaData = fileName.replace(".mp3", "").split(META_SEPERATOR);
		
		//Make sure that there is 3 bits of meta data as expected
		if (metaData.length == 5) {
			// Empty String values allowed
//			this.resourceName = metaData[1];
//			// Empty Sting values allowed
//			this.resourceVerse = metaData[2];

		}
		return null;
	}

	
	/**
	 * Utility method to get a method to store the filename in a standard format
	 * @param bibleText 
	 * @return
	 */
	public static String getFileName(OnlineAudioResource resource, BibleText bibleText) {
		return (bibleText.getBook() + META_SEPERATOR + bibleText.getChapter() + META_SEPERATOR + resource.getResourceRepo().getChurchName() + META_SEPERATOR + resource.getResourceName() + META_SEPERATOR + resource.getResourceVerse() + ".mp3").replaceAll(" ", "_");		
	}
}

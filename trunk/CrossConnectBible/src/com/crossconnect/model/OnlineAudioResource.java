package com.crossconnect.model;

/**
 * Class that represents the contents of the online audio resource
 * 
 * @author Gary Lo
 * 
 */
public class OnlineAudioResource {

	private ResourceRespository resourceRepo;
	
	private String resourceName;
	private String resourceVerse;

	// Nulls means no audio url exists
	private String audioURL;

	// Nulls mean no read url exists
	private String readURL;

	public OnlineAudioResource() {
	}

	public OnlineAudioResource(String resourceName, String resourceVerse, String audioURL, String readURL) {
		// Empty String values allowed
		this.resourceName = resourceName;
		// Empty Sting values allowed
		this.resourceVerse = resourceVerse;

		// Null represents no audioURL exists
		this.audioURL = (audioURL.length() == 0) ? null : audioURL;
		// Null represents no readURL exists
		this.readURL = (readURL.length() == 0) ? null : readURL;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceVerse() {
		return resourceVerse;
	}

	public void setResourceVerse(String resourceVerse) {
		this.resourceVerse = resourceVerse;
	}

	/**
	 * @return the audioURL
	 */
	public String getAudioURL() {
		return audioURL;
	}

	/**
	 * @param audioURL
	 *            the audioURL to set
	 */
	public void setAudioURL(String audioURL) {
		this.audioURL = audioURL;
	}

	/**
	 * @return the readURL
	 */
	public String getReadURL() {
		return readURL;
	}

	/**
	 * @param readURL
	 *            the readURL to set
	 */
	public void setReadURL(String readURL) {
		this.readURL = readURL;
	}
}
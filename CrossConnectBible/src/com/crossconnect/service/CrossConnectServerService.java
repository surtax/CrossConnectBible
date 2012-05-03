package com.crossconnect.service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.crossconnect.model.OnlineAudioResource;
import com.crossconnect.model.ResourceRepository;


public class CrossConnectServerService {
	private static final String SERVER_URL = "http://xconnectbible.appspot.com/AudioIndex3/";
	
	private static final String TAG = "CrossConnectServerService";

	public CrossConnectServerService() {
	}

	public List<OnlineAudioResource> getOnlineResource(String book) {
	    
	    Log.d(TAG, "Retreiving resources for book: " + book);

		book = book.replaceAll(" ", "");

		String rawJSON;
		try {
			rawJSON = crossConnectConnector(SERVER_URL + book + ".json");
		} catch (Exception e) {
			//Likely to be no internet but if any connection issue just return null
			return null; 
		}

		
		List<OnlineAudioResource> audios = new ArrayList<OnlineAudioResource>();

		try {

			JSONObject myResults = new JSONObject(rawJSON);

			JSONArray results = myResults.getJSONArray("audioList");
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = (JSONObject) results.get(i);
				audios.add(new OnlineAudioResource(result.getString("resourceName"), result.getString("resourceVerse"), result.getString("audioURL"),
						result.getString("readURL")));
			}
		} catch (Exception e) {
			// TODO:need to exception handle here
			Log.e("CrossConnectServer","JSON Exception", e);
		}

		return audios;
	}
	
	public List<ResourceRepository> getResourceRepos() {
	    
	    Log.d(TAG, "Retreiving resources repos");
	    List<ResourceRepository> repos = new ArrayList<ResourceRepository>();
	    
		repos.add(new ResourceRepository("Desiring God", "Description"));
		repos.add(new ResourceRepository("Coming Soon...", "Description"));
		repos.add(new ResourceRepository("Coming Soon...", "Description"));

		return repos;
	}


	private static String crossConnectConnector(String url) throws UnknownHostException {
		StringBuilder sb = new StringBuilder();
		try {
			URL yahoo = new URL(url);
			URLConnection yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				sb.append(inputLine);
			in.close();
		} catch (UnknownHostException e) {
			Log.e("CrossConnectServer","Invalid URL or No Internet", e);
			throw e;
		} catch (Exception e) {
			Log.e("CrossConnectServer","Connection Issue", e);
		}
		return sb.toString();

	}
}
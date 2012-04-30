package com.crossconnect.service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.crossconnect.model.OnlineAudioResource;


public class CrossConnectServerService {
	private static final String SERVER_URL = "http://xconnectbible.appspot.com/AudioIndex3/";
	
	private static final String TAG = "CrossConnectServerService";

	public CrossConnectServerService() {
	}

	public List<OnlineAudioResource> getOnlineResource(String book) {
	    
	    Log.d(TAG, "Retreiving resources for book: " + book);

		book = book.replaceAll(" ", "");

		String rawJSON = crossConnectConnector(SERVER_URL + book + ".json");
		// System.out.println(contents);

		
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
		}

		return audios;
	}

	private static String crossConnectConnector(String url) {
		StringBuilder sb = new StringBuilder();
		try {
			URL yahoo = new URL(url);
			URLConnection yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				sb.append(inputLine);
			in.close();
		} catch (Exception e) {

		}
		return sb.toString();

	}
}
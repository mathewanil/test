/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.online;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 */
public class WebService extends AsyncTask<String, Void, Void> {
	private static final String LOG_TAG = "WebService";
	
	public TreeMap<String, String> queryParams = new TreeMap<String, String>();
	
	private String getQueryString(boolean urlEncode)
	{

		StringBuilder sb = new StringBuilder();

		for (String key : queryParams.keySet()) {
			sb.append(key).append("=").append(queryParams.get(key));
			if (urlEncode)
				sb.append("&");
		}
		return sb.toString();
	}
	/**
	 * Actual download method.
	 */
	@Override
	protected Void doInBackground(String... params) {
		
		queryParams.put("method", "auth.getVoidSession");
		queryParams.put("api_key", "android_json_ght");
		queryParams.put("call_id", String.valueOf(System.currentTimeMillis()));
		String url = "http://onlinepizza.se/api/1.3/rest?";
		
		try
		{

			String response = call(url + getQueryString(true) + "sig=" + sig(getQueryString(false)));
			JSONObject jsonObject = new JSONObject(response);
			Log.i(LOG_TAG, jsonObject.toString());
			Log.i(LOG_TAG, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String sig(String q) throws Exception
	{
		String s = q + "q3FnWEUS7";
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(s.getBytes());
		byte messageDigest[] = md.digest();
	            
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<messageDigest.length;i++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}
		
		return hexString.toString();
	}
	private void print(JSONArray jsonArray)
	{
		try {
			Log.i(LOG_TAG, "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.i(LOG_TAG, jsonObject.getString("text"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 */

	private String call(String url) {
		Log.i(LOG_TAG, url);
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				// Log.e(ParseJSON.class.toString(),
				// "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
}

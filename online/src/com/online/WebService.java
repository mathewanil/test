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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This helper class download images from the Internet and binds those with the
 * provided ImageView.
 * 
 * <p>
 * It requires the INTERNET permission, which should be added to your
 * application's manifest file.
 * </p>
 * 
 * A local cache of downloaded images is maintained internally to improve
 * performance.
 */
public class WebService {
	private static final String LOG_TAG = "WebService";

	/**
	 * The actual AsyncTask
	 */
	class WebServiceTask extends AsyncTask<String, Void, Bitmap> {

		public WebServiceTask() {
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			String readTwitterFeed = readTwitterFeed();
			try {
				JSONArray jsonArray = new JSONArray(readTwitterFeed);
				Log.i(LOG_TAG, "Number of entries " + jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Log.i(LOG_TAG, jsonObject.getString("text"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
		}

		private String readTwitterFeed() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"http://twitter.com/statuses/user_timeline/vogella.json");
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
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

}

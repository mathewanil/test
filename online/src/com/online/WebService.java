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
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 */
public class WebService extends AsyncTask<String, Void, JSONObject> {
	private static final String LOG_TAG = "WebService";

	private TreeMap<String, String> queryParams = new TreeMap<String, String>();

	public void setQueryParam(String key, String value) {
		queryParams.put(key, value);
	}

	private String getQueryString(boolean urlEncode) {

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
	protected JSONObject doInBackground(String... params) {

		queryParams.put("method", params[0]);
		queryParams.put("api_key", "android_json_ght"); // TODO - property file
		queryParams.put("call_id", String.valueOf(System.currentTimeMillis()));
		String url = "http://onlinepizza.se/api/1.3/rest?"; // TODO - property file

		try {

			String response = call(url + getQueryString(true) + "sig=" + sig(getQueryString(false)));
			Log.i(LOG_TAG, response);
			return new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(JSONObject jsonObject) {
	}

	private String sig(String q) throws Exception {
		String s = q + "q3FnWEUS7";
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(s.getBytes());
		byte messageDigest[] = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			String h = Integer.toHexString(0xFF & messageDigest[i]);
			while (h.length() < 2)
				h = "0" + h;
			hexString.append(h);
		}

		return hexString.toString();
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

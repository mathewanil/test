package com.online.test;

import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.online.WebService;

public class WebServiceTest extends AndroidTestCase {
	// private final static String LOG_TAG = "WebServiceTest";
	WebService ws;

	protected void setUp() {
		ws = new WebService();
	}

	public void test_auth_getVoidSession() {
		ws.execute("auth.getVoidSession");
		try {
			JSONObject jsonObject = ws.get();
			assertEquals("ok", jsonObject.getJSONObject("@attributes").get("status"));
		} catch (Exception e) {
		}
	}

	public void test_auth_getLatestCallId() {
		try {
			ws.execute("auth.getVoidSession");
			String session = ws.get().getString("session");

			ws.setQueryParam("session_key", session);
			ws.execute("auth.getLatestCallId");

			JSONObject jsonObject = ws.get();
			assertEquals("ok", jsonObject.getJSONObject("@attributes").get("status"));
		} catch (Exception e) {
		}
	}

	public void test_server_getDayAndTime() {
		try {
			ws.execute("auth.getVoidSession");
			String session = ws.get().getString("session");

			ws.setQueryParam("session_key", session);
			ws.setQueryParam("country_code", "SE");
			ws.execute("server.getDayAndTime");

			JSONObject jsonObject = ws.get();
			assertEquals("ok", jsonObject.getJSONObject("@attributes").get("status"));
		} catch (Exception e) {
		}
	}
	
}

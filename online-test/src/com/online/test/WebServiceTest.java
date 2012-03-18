package com.online.test;

import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.online.WebService;

public class WebServiceTest extends AndroidTestCase {
	//private final static String LOG_TAG = "WebServiceTest";
	WebService ws;

	protected void setUp() {
		ws = new WebService();
	}

	public void test_auth_getVoidSession() {
		ws.execute("auth.getVoidSession");
		try
		{
			JSONObject jsonObject = ws.get();
			assertEquals("ok", jsonObject.getJSONObject("@attributes").get("status"));
		} catch (Exception e) {}
	}

}

package com.online.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;

import com.online.WebService;

public class WebServiceTest extends AndroidTestCase {
	private final static String LOG_TAG = "WebServiceTest";
	WebService ws;

	private String getFirstRestaurantsName(JSONObject jsonObject) throws JSONException
	{
		Object resObj = jsonObject.getJSONObject("restaurants").get("restaurant") ;
		JSONObject res = (resObj instanceof JSONObject ? (JSONObject)resObj : ((JSONArray)resObj).getJSONObject(0));
		return res.getJSONObject("@attributes").getString("name");		
	}
	private void setSession() {
		try {
			WebService wss = new WebService();
			wss.execute("auth.getVoidSession");
			String sessionKey = wss.get().getString("session_key");
			
			ws.setQueryParam("session_key", sessionKey);
		} catch (Exception e) {
		}
	}

	private void assertStatusOK() {
		try {
			JSONObject jsonObject = ws.get();
			assertEquals(jsonObject.toString(), "ok", jsonObject.getJSONObject("@attributes").get("status"));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	protected void setUp() {
		ws = new WebService();
	}

	public void test_auth_getVoidSession() {
		ws.execute("auth.getVoidSession");
		assertStatusOK();
	}

	public void test_auth_getLatestCallId() {
		setSession();
		ws.execute("auth.getLatestCallId");
		assertStatusOK();
	}

	public void test_server_getDayAndTime() {
		setSession();
		ws.setQueryParam("country_code", "SE");
		ws.execute("server.getDayAndTime");
		assertStatusOK();
	}

	public void test_library_getZipcodeByCoordinates() {
		setSession();
		ws.setQueryParam("latitude", "59.339343");
		ws.setQueryParam("longitude", "18.009206");
		ws.execute("library.getZipcodeByCoordinates");
		assertStatusOK();
		try {
			JSONObject jsonObject = ws.get();
			assertEquals("11251", jsonObject.getString("zipcode"));
			assertEquals("SE", jsonObject.getString("country_code"));
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	public void test_library_getStreets() {
		setSession();
		ws.setQueryParam("country_code", "SE");
		ws.setQueryParam("search_string", "Sad");
		ws.setQueryParam("limit", "1");
		ws.execute("library.getStreets");
		assertStatusOK();
		try {
			JSONObject jsonObject = ws.get();
			assertEquals("Sadkowska", jsonObject.getJSONArray("streets").getJSONObject(0).getString("name"));
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	public void test_library_getRestaurantsByCountryCodeAndZipcode() {
		setSession();
		ws.setQueryParam("country_code", "SE");
		ws.setQueryParam("zipcode", "12345");
		ws.execute("library.getRestaurantsByCountryCodeAndZipcode");
		assertStatusOK();
		try {
			assertEquals("Demorestaurang", getFirstRestaurantsName(ws.get()));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		
	}
	
	public void test_library_getRestaurantsByCountryCodeAndCity() {
		setSession();
		ws.setQueryParam("country_code", "SE");
		ws.setQueryParam("city", "Teststad");
		ws.execute("library.getRestaurantsByCountryCodeAndCity");
		assertStatusOK();
		try {
			assertEquals("Demorestaurang", getFirstRestaurantsName(ws.get()));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		
	}

	public void test_library_getRestaurantsByCoordinates() {
		setSession();
		ws.setQueryParam("latitude", "59.339343");
		ws.setQueryParam("longitude", "18.009206");
		ws.setQueryParam("max_distance", "100");
		ws.execute("library.getRestaurantsByCoordinates");
		assertStatusOK();
		try {
			assertEquals("Kebab House", getFirstRestaurantsName(ws.get()));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		
	}

	public void test_library_getOpinionsByRestaurantId() {
		setSession();
		ws.setQueryParam("restaurant_id", "1999");
		ws.execute("library.getOpinionsByRestaurantId");
		assertStatusOK();
	
	}
	
	public void test_restaurant_getMenu() {
		setSession();
		ws.setQueryParam("restaurant_id", "1999");
		ws.execute("restaurant.getMenu");
		assertStatusOK();
		try {
			JSONObject jsonObject = ws.get();
			assertTrue(jsonObject.getJSONObject("restaurant").
					getJSONObject("menu").getJSONObject("sections").
					getJSONArray("section").length() > 0) ;
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}
	public void test_restaurant_getDeliveryConditions() {
		setSession();
		ws.setQueryParam("restaurant_id", "1999");
		ws.execute("restaurant.getDeliveryConditions");
		assertStatusOK();
		try {
			JSONObject jsonObject = ws.get();
			assertTrue(jsonObject.getJSONObject("delivery").getJSONArray("deliveryArea").length() > 0) ;
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}
	public void test_restaurant_getDeliveryConditionsByZipcode() {
		setSession();
		ws.setQueryParam("restaurant_id", "1999");
		ws.setQueryParam("zipcode", "12345");
		ws.execute("restaurant.getDeliveryConditionsByZipcode");
		assertStatusOK();
		try {
			JSONObject jsonObject = ws.get();
			assertNotNull(jsonObject.toString(), jsonObject.getJSONObject("delivery"));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}

	public void test_restaurant_getModificationDataByIDProduct() {
		setSession();
		ws.setQueryParam("product_id", "70488");
		ws.execute("restaurant.getModificationDataByIDProduct");
		assertStatusOK();
		try {
			JSONObject jsonObject = ws.get();
			assertNotNull(jsonObject.toString(), jsonObject.getJSONObject("category"));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}

}

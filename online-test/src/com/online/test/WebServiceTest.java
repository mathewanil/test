package com.online.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;

import com.online.WebService;

public class WebServiceTest extends AndroidTestCase {
	private final static String LOG_TAG = "WebServiceTest";
	String sessionKey ;
	String orderId ;

	private String getFirstRestaurantsName(JSONObject jsonObject) throws JSONException
	{
		Object resObj = jsonObject.getJSONObject("restaurants").get("restaurant") ;
		JSONObject res = (resObj instanceof JSONObject ? (JSONObject)resObj : ((JSONArray)resObj).getJSONObject(0));
		return res.getJSONObject("@attributes").getString("name");		
	}

	private void assertStatusOK(WebService ws) {
		try {
			JSONObject jsonObject = ws.get();
			assertEquals(jsonObject.toString(), "ok", jsonObject.getJSONObject("@attributes").get("status"));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		WebService ws = new WebService();
		ws.execute("auth.getVoidSession");
		sessionKey = ws.get().getString("session_key");
		
	}

	public void test_auth_getVoidSession() {
		WebService ws = new WebService();
		ws.execute("auth.getVoidSession");
		assertStatusOK(ws);
	}

	public void test_auth_getLatestCallId() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.execute("auth.getLatestCallId");
		assertStatusOK(ws);
	}

	public void test_server_getDayAndTime() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("country_code", "SE");
		ws.execute("server.getDayAndTime");
		assertStatusOK(ws);
	}

	public void test_library_getZipcodeByCoordinates() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("latitude", "59.339343");
		ws.setQueryParam("longitude", "18.009206");
		ws.execute("library.getZipcodeByCoordinates");
		assertStatusOK(ws);
		try {
			JSONObject jsonObject = ws.get();
			assertEquals("11251", jsonObject.getString("zipcode"));
			assertEquals("SE", jsonObject.getString("country_code"));
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	public void test_library_getStreets() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("country_code", "SE");
		ws.setQueryParam("search_string", "Sad");
		ws.setQueryParam("limit", "1");
		ws.execute("library.getStreets");
		assertStatusOK(ws);
		try {
			JSONObject jsonObject = ws.get();
			assertEquals("Sadkowska", jsonObject.getJSONArray("streets").getJSONObject(0).getString("name"));
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	public void test_library_getRestaurantsByCountryCodeAndZipcode() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("country_code", "SE");
		ws.setQueryParam("zipcode", "12345");
		ws.execute("library.getRestaurantsByCountryCodeAndZipcode");
		assertStatusOK(ws);
		try {
			assertEquals("Demorestaurang", getFirstRestaurantsName(ws.get()));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		
	}
	
	public void test_library_getRestaurantsByCountryCodeAndCity() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("country_code", "SE");
		ws.setQueryParam("city", "Teststad");
		ws.execute("library.getRestaurantsByCountryCodeAndCity");
		assertStatusOK(ws);
		try {
			assertEquals("Demorestaurang", getFirstRestaurantsName(ws.get()));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		
	}

	public void test_library_getRestaurantsByCoordinates() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("latitude", "59.339343");
		ws.setQueryParam("longitude", "18.009206");
		ws.setQueryParam("max_distance", "100");
		ws.execute("library.getRestaurantsByCoordinates");
		assertStatusOK(ws);
		try {
			assertEquals("Kebab House", getFirstRestaurantsName(ws.get()));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		
	}

	public void test_library_getOpinionsByRestaurantId() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("restaurant_id", "1999");
		ws.execute("library.getOpinionsByRestaurantId");
		assertStatusOK(ws);
	
	}
	
	public void test_restaurant_getMenu() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("restaurant_id", "1999");
		ws.execute("restaurant.getMenu");
		assertStatusOK(ws);
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
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("restaurant_id", "1999");
		ws.execute("restaurant.getDeliveryConditions");
		assertStatusOK(ws);
		try {
			JSONObject jsonObject = ws.get();
			assertTrue(jsonObject.getJSONObject("delivery").getJSONArray("deliveryArea").length() > 0) ;
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}
	public void test_restaurant_getDeliveryConditionsByZipcode() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("restaurant_id", "1999");
		ws.setQueryParam("zipcode", "12345");
		ws.execute("restaurant.getDeliveryConditionsByZipcode");
		assertStatusOK(ws);
		try {
			JSONObject jsonObject = ws.get();
			assertNotNull(jsonObject.toString(), jsonObject.getJSONObject("delivery"));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}

	public void test_restaurant_getModificationDataByIDProduct() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("product_id", "70488");
		ws.execute("restaurant.getModificationDataByIDProduct");
		assertStatusOK(ws);
		try {
			JSONObject jsonObject = ws.get();
			Log.i(LOG_TAG, jsonObject.toString());
			assertNotNull(jsonObject.toString(), jsonObject.getJSONObject("category"));
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	
	}
	public void test_cart_add() {
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("product_id", "70488");
		ws.execute("cart.add");
		assertStatusOK(ws);
	}

	public void test_cart_show() {
		test_cart_add();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.execute("cart.show");
		try
		{
			Log.i(LOG_TAG, ws.get().toString());
		} catch (Exception e) {}
		assertStatusOK(ws);
	}

	public void test_cart_showItem() {
		test_cart_add();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("idx", "0");
		ws.execute("cart.showItem");
		assertStatusOK(ws);
	}

	public void test_cart_modifyItem() {
		test_cart_add();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("idx", "0");
		ws.setQueryParam("selectedIngr[0]", "14894");
		ws.setQueryParam("selectedIngr[1]", "14895");
		
		ws.execute("cart.modifyItem");
		assertStatusOK(ws);
	}

	public void test_cart_remove() {
		test_cart_add();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("idx", "0");
		ws.execute("cart.remove");
		assertStatusOK(ws);
	}

	public void test_cart_clean() {
		test_cart_add();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.execute("cart.clean");
		assertStatusOK(ws);
	}
	
	public void test_cart_update() {
		test_cart_add();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("customer[name]", "name");
		ws.setQueryParam("customer[phone]", "0771-908070");
		ws.setQueryParam("customer[email]", "email@email.com");
		ws.setQueryParam("customer[delivery_address][address]", "address");
		ws.setQueryParam("customer[delivery_address][entrance_code]", "1");
		ws.setQueryParam("customer[delivery_address][floor]", "1");
		ws.setQueryParam("customer[delivery_address][apartment]", "1");
		ws.setQueryParam("customer[delivery_address][zipcode]", "12345");
		ws.setQueryParam("customer[delivery_address][country]", "SE");
		ws.setQueryParam("transport[type]", "delivery");
		ws.setQueryParam("payment[method]", "cash");
		ws.setQueryParam("message", "message");
		ws.execute("cart.update");
		assertStatusOK(ws);
	}

	public void test_order_send() {
		test_cart_add();
		test_cart_update();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.execute("order.send");
		try
		{
			JSONObject jsonObject = ws.get();
			orderId = jsonObject.getJSONObject("order").getJSONObject("@attributes").getString("id");
		} catch (Exception e){
		}
		
		assertStatusOK(ws);
	}
	public void test_order_status() {
		test_order_send();
		WebService ws = new WebService();
		ws.setQueryParam("session_key", sessionKey);
		ws.setQueryParam("order_id", orderId);
		ws.execute("order.status");
		assertStatusOK(ws);
	}
}

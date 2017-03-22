/*
 * Copyright (C) 2014 Saurabh Rane
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.monash.infotech.health.fatsecret.platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

//import com.fatsecret.platform.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

/**
 * This class helps in calling requests to FatSecret REST API to search food items and recipes.
 *
 * @author Saurabh Rane
 * @version 2014-07-30
*/

public class FatSecretAPI {

	/**
	 * A value FatSecret API issues to you which helps this API identify you
	 */
	final private String APP_KEY;

	/**
	 * A secret FatSecret API issues to you which helps this API establish that it really is you
	 */
	final private String APP_SECRET;

	/**
	 * Request URL
	 * <p>
	 * The URL to make API calls is http://platform.fatsecret.com/rest/server.api
	 */
	final private String APP_URL = "http://platform.fatsecret.com/rest/server.api";

	/**
	 * The signature method allowed by FatSecret API
	 * <p>
	 * They only support "HMAC-SHA1"
	 */
	final private String APP_SIGNATURE_METHOD = "HmacSHA1";

	/**
	 * The HTTP Method supported by FatSecret API
	 * <p>
	 * This API only supports GET method
	 */
	final private String HTTP_METHOD = "GET";

	/**
	 * Constructor to set values for APP_KEY and APP_SECRET
	 *
	 * @param APP_KEY
	 *			A value FatSecret API issues to you which helps this API identify you
	 * @param APP_SECRET
	 *			A secret FatSecret API issues to you which helps this API establish that it really is you
	 */
	public FatSecretAPI(String APP_KEY, String APP_SECRET) {
		this.APP_KEY = APP_KEY;
		this.APP_SECRET = APP_SECRET;
	}

	/**
	 * Returns randomly generated nonce value for calling the request.
	 *
	 * @return the randomly generated value for nonce.
	 */
	public String nonce() {
		Random r = new Random();
		StringBuffer n = new StringBuffer();
		for (int i = 0; i < r.nextInt(8) + 2; i++) {
			n.append(r.nextInt(26) + 'a');
		}
		return n.toString();
	}

	/**
	 * Get all the oauth parameters and other parameters.
	 *
	 * @return an array of parameter values as "key=value" pair
	 */
	public String[] generateOauthParams() {
		String[] a = {
				"oauth_consumer_key=" + APP_KEY,
				"oauth_signature_method=HMAC-SHA1",
				"oauth_timestamp=" + new Long(System.currentTimeMillis() / 1000).toString(),
				"oauth_nonce=" + nonce(),
				"oauth_version=1.0",
				"format=json"
		};
		return a;
	}

	/**
	 * Returns string generated using params and separator
	 *
	 * @param params
	 * 			An array of parameter values as "key=value" pair
	 * @param separator
	 * 			A separator for joining
	 *
	 * @return the string by appending separator after each parameter from params except the last.
	 */
	public String join(String[] params, String separator) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				b.append(separator);
			}
			b.append(params[i]);
		}
		return b.toString();
	}

	/**
	 * Returns string generated using params and "&" for signature base and normalized parameters
	 *
	 * @param params
	 * 			An array of parameter values as "key=value" pair
	 *
	 * @return the string by appending separator after each parameter from params except the last.
	 */
	public String paramify(String[] params) {
		String[] p = Arrays.copyOf(params, params.length);
		Arrays.sort(p);
		return join(p, "&");
	}

	/**
	 * Returns the percent-encoded string for the given url
	 *
	 * @param url
	 * 			URL which is to be encoded using percent-encoding
	 *
	 * @return encoded url
	 */
	public String encode(String url) {
		if (url == null)
			return "";

		try {
			return URLEncoder.encode(url, "utf-8")
					.replace("+", "%20")
					.replace("!", "%21")
					.replace("*", "%2A")
					.replace("\\", "%27")
					.replace("(", "%28")
					.replace(")", "%29");
		}
		catch (UnsupportedEncodingException wow) {
			throw new RuntimeException(wow.getMessage(), wow);
		}
	}

	/**
	 * Returns signature generated using signature base as text and consumer secret as key
	 *
	 * @param method
	 * 			Http method
	 * @param uri
	 * 			Request URL - http://platform.fatsecret.com/rest/server.api (Always remains the same)
	 * @param params
	 * 			An array of parameter values as "key=value" pair
	 *
	 * @return oauth_signature which will be added to request for calling fatsecret api
	 */
	public String sign(String method, String uri, String[] params) throws UnsupportedEncodingException {

		String encodedURI = encode(uri);
		String encodedParams = encode(paramify(params));

		String[] p = {method, encodedURI, encodedParams};

		String text = join(p, "&");
		String key = APP_SECRET + "&";
	    SecretKey sk = new SecretKeySpec(key.getBytes(), APP_SIGNATURE_METHOD);
	    String sign = "";
	    try {
	      Mac m = Mac.getInstance(APP_SIGNATURE_METHOD);
	      m.init(sk);
	      sign = encode(new String(Base64.encode(m.doFinal(text.getBytes()), Base64.DEFAULT)).trim());
	    } catch(java.security.NoSuchAlgorithmException e) {

	    } catch(java.security.InvalidKeyException e) {

	    }
		return sign;
	}

	/**
	 * Returns JSONObject associated with the food items depending on the search query
	 *
	 * @param query
	 * 			Search terms for querying fatsecret api
	 *
	 * @return food items at page number '0' based on the query
	 */
	public JSONObject getFoodItems(String query) throws UnsupportedEncodingException {
		JSONObject result = new JSONObject();

		List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
		String[] template = new String[1];
		params.add("method=foods.search");
		params.add("max_results=5");
		params.add("search_expression=" + encode(query));
		params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

		try {
			URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
			URLConnection api = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			while ((line = reader.readLine()) != null) builder.append(line);

			JSONObject json = new JSONObject(builder.toString());

			result.put("result", json);

		} catch (Exception e) {
			e.printStackTrace();
//			JSONObject error = new JSONObject();
//			error.put("message", "There was an error in processing your request. Please try again later.");
//			result.put("error", error);
		}

		return result;
	}

	/**
	 * Returns JSONObject associated with the food items depending on the search query and page number
	 *
	 * @param query
	 * 			Search terms for querying fatsecret api
	 * @param pageNumber
	 * 			Page Number to search the food items
	 *
	 * @return food items at a particular page number based on the query
	 */
	public JSONObject getFoodItemsAtPageNumber(String query, int pageNumber) throws UnsupportedEncodingException {
		JSONObject result = new JSONObject();

		List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
		String[] template = new String[1];
		params.add("method=foods.search");
		params.add("max_results=50");
		params.add("page_number="+pageNumber);
		params.add("search_expression=" + encode(query));
		params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

		try {
			URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
			URLConnection api = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			while ((line = reader.readLine()) != null) builder.append(line);

			JSONObject json = new JSONObject(builder.toString());

			result.put("result", json);

		} catch (Exception e) {
			e.printStackTrace();
//			JSONObject error = new JSONObject();
//			error.put("message", "There was an error in processing your request. Please try again later.");
//			result.put("error", error);
		}

		return result;
	}

	/**
	 * Returns JSONObject associated with the food id with nutritional information
	 *
	 * @param id
	 * 			The ID of the food to retrieve.
	 *
	 * @return food items based on the food id
	 */
	public JSONObject getFoodItem(long id) throws UnsupportedEncodingException {
		JSONObject result = new JSONObject();

		List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
		String[] template = new String[1];
		params.add("method=food.get");
		params.add("food_id=" + encode(""+id));
		params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

		try {
			URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
			URLConnection api = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			while ((line = reader.readLine()) != null) builder.append(line);

			JSONObject json = new JSONObject(builder.toString());

			result.put("result", json);

		} catch (Exception e) {
			e.printStackTrace();
//			JSONObject error = new JSONObject();
//			error.put("message", "There was an error in processing your request. Please try again later.");
//			result.put("error", error);
		}

		return result;
	}

	/**
	 * Returns JSONObject associated with the recipes depending on the search query
	 *
	 * @param query
	 * 			Search terms for querying fatsecret api
	 *
	 * @return recipes at page number '0' based on the query
	 */
	public JSONObject getRecipes(String query) throws UnsupportedEncodingException {
		JSONObject result = new JSONObject();
		List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
		String[] template = new String[1];
		params.add("method=recipes.search");
		params.add("max_results=50");
		params.add("search_expression=" + encode(query));
		params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

		try {
			URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
			URLConnection api = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			while ((line = reader.readLine()) != null) builder.append(line);

			JSONObject json = new JSONObject(builder.toString());
			result.put("result", json);

		} catch (Exception e) {
			e.printStackTrace();
//			JSONObject error = new JSONObject();
//			error.put("message", "There was an error in processing your request. Please try again later.");
//			result.put("error", error);
		}
		return result;
	}

	/**
	 * Returns JSONObject associated with the recipes depending on the search query
	 *
	 * @param query
	 * 			Search terms for querying fatsecret api
	 * @param pageNumber
	 * 			Page Number to search the food items
	 *
	 * @return recipes at a particular page number based on the query
	 */
	public JSONObject getRecipesAtPageNumber(String query, int pageNumber) throws UnsupportedEncodingException {
		JSONObject result = new JSONObject();
		List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
		String[] template = new String[1];
		params.add("method=recipes.search");
		params.add("max_results=50");
		params.add("page_number="+pageNumber);
		params.add("search_expression=" + encode(query));
		params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

		try {
			URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
			URLConnection api = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			while ((line = reader.readLine()) != null) builder.append(line);

			JSONObject json = new JSONObject(builder.toString());
			result.put("result", json);

		} catch (Exception e) {
			e.printStackTrace();
//			JSONObject error = new JSONObject();
//			error.put("message", "There was an error in processing your request. Please try again later.");
//			result.put("error", error);
		}
		return result;
	}

	/**
	 * Returns JSONObject associated with general information about the recipe item with detailed nutritional information for the standard serving.
	 *
	 * @param id
	 * 			The ID of the recipe to retrieve.
	 *
	 * @return recipe based on the recipe id
	 */
	public JSONObject getRecipe(long id) throws UnsupportedEncodingException {
		JSONObject result = new JSONObject();

		List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
		String[] template = new String[1];
		params.add("method=recipe.get");
		params.add("recipe_id=" + encode(""+id));
		params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

		try {
			URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
			URLConnection api = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			while ((line = reader.readLine()) != null) builder.append(line);

			JSONObject json = new JSONObject(builder.toString());

			result.put("result", json);

		} catch (Exception e) {
			e.printStackTrace();
//			JSONObject error = new JSONObject();
//			error.put("message", "There was an error in processing your request. Please try again later.");
//			result.put("error", error);
		}
		return result;
	}
}

package org.solution.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.solution.entities.ProductCategory;

public class GloboMartHttpClientTest {


	public static final String putProductUrl = "http://localhost:8080/GloboMart/engine/products";
	public static final String getPriceUrl = "http://localhost:8080/GloboMart/engine/price";
	
	public static void main(String[] args) {
		
		GloboMartHttpClientTest testObject = new GloboMartHttpClientTest();
		testObject.addItem("Helmet");
		
	}
	
	public String addItem(String itemName){
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;
		String itemID = "";
		JSONObject itemJSON = new JSONObject();
		try {
			itemJSON.put("name", itemName);
			itemJSON.put("category", ProductCategory.HEALTHCARE.name());
			itemJSON.put("price", 10.0);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpPut putUser = new HttpPut(putProductUrl);
		putUser.addHeader("Content-Type", "application/json");
		putUser.addHeader("Accept", "application/json");
		StringEntity input;
		try {
			input = new StringEntity(itemJSON.toString());
			putUser.setEntity(input);
			response = client.execute(putUser);
			responseBody = readFromStream(response.getEntity().getContent());
			JSONObject responseJSON = new JSONObject(responseBody);
			itemID = responseJSON.getString("productId");
			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemID;
	}
	
	public String readFromStream(InputStream is) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));;
		String str = null;
		StringBuilder sbuilder = new StringBuilder();
		while((str = br.readLine()) != null){
			sbuilder.append(str);
		}
		return sbuilder.toString();
	}
}
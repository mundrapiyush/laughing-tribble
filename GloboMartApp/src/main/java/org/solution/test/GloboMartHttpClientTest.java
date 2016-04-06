package org.solution.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class GloboMartHttpClientTest {

	private enum ProductCategory {
		ELECTRONICS,
		BOOKS,
		HOME_APPLIANCES,
		SPORTS_AND_FITNESS,
		HEALTHCARE
	}
	
	public static final String putProductUrl = "http://globomartelb-1135890954.ap-southeast-1.elb.amazonaws.com/GloboMartApp/engine/products";
	public static final String getProductUrl = "http://globomartelb-1135890954.ap-southeast-1.elb.amazonaws.com/GloboMartApp/engine/products";
	public static final String getPriceUrl = "http://globomartelb-1135890954.ap-southeast-1.elb.amazonaws.com/GloboMartApp/engine/price";
	
//	public static final String putProductUrl = "http://localhost:8080/GloboMartApp/engine/products";
//	public static final String getProductUrl = "http://localhost:8080/GloboMartApp/engine/products";
//	public static final String getPriceUrl = "http://localhost:8080/GloboMartApp/engine/price";
	
	private static List<String> listOfProductsURL = new ArrayList<>();
	
	public static void main(String[] args) {
		
		GloboMartHttpClientTest testObject = new GloboMartHttpClientTest();
		
		System.out.println("Adding 50 new items . . . . ");
		for(int count = 0; count < 50; count++ ){
			Random rand = new Random();
			int productCategoryIdx = rand.nextInt(5);
			double productPrice = rand.nextFloat() * 100;
			String productURL = testObject.addItem("Product_" +count, ProductCategory.values()[productCategoryIdx], productPrice);
			listOfProductsURL.add(productURL);
		}
		
		List<String> chosenProducts = new ArrayList<>();
		System.out.println("Fetching 10 random items . . . ");
		for(int count = 0; count < 10; count++ ){
			Random rand = new Random();
			int productIDIdx = rand.nextInt(50);
			String productURL = listOfProductsURL.get(productIDIdx);
			chosenProducts.add(productURL);
			System.out.println("URL:  " +productURL);
			testObject.getItem(productURL);
		}
		
		System.out.println("Fetching 10 items again . . . ");
		System.out.println("Catalog resources must be read as Cached Content on the Edge Cache . . . ");
		for(String productURL : chosenProducts){
			System.out.println("URL:  " +productURL);
			testObject.getItem(productURL);
		}
		
		System.out.println("Fetching prices of the 10 items . . . ");
		System.out.println("Pricing Resources must be read from the server and not from the Edge Cache  . . . ");
		for(String productURL : chosenProducts){
			String priceURL = productURL.replace("products", "price");
			System.out.println("URL:  " +priceURL);
			testObject.getPrice(priceURL);
		}		
	}
	
	public String addItem(String itemName, ProductCategory category, double price){
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;
		String productID = "";
		String productURL = "";
		
		JSONObject itemJSON = new JSONObject();
		try {
			itemJSON.put("name", itemName);
			itemJSON.put("category", category.name());
			itemJSON.put("price", price);

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
			productID = responseJSON.getString("productID");
			productURL = responseJSON.getString("itemURL");
			System.out.println("Product: " +itemName+ " Category: " +category.name()+ " Price: " +price+ " ID: " +productID+ " URL: " +productURL);			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productURL;
	}

	public void getItem(String itemURL){
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;

		HttpGet getItem = new HttpGet(itemURL);
		getItem.addHeader("Accept", "application/json");
		try {
			long startTime = System.currentTimeMillis();
			response = client.execute(getItem);
			long endTime = System.currentTimeMillis();
			responseBody = readFromStream(response.getEntity().getContent());
			JSONObject responseJSON = new JSONObject(responseBody);
			String productName = responseJSON.getString("name");
			String productCategory = responseJSON.getString("category");
			String price = responseJSON.getString("price");
			System.out.println("Product: " +productName+ " Category: " +productCategory+ " Price: " +price);
			System.out.println("Time Consumed: (ms) " +(endTime - startTime));
			StringBuilder headerBuilder = new StringBuilder();
			Header[] headers  = response.getAllHeaders();
			for(Header header : headers){
				headerBuilder.append(header.getName() +" : "+ header.getValue() + ", ");
			}
			System.out.println(headerBuilder.toString());
			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public void getPrice(String priceURL){
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;

		HttpGet getPrice = new HttpGet(priceURL);
		getPrice.addHeader("Accept", "application/json");
		try {
			response = client.execute(getPrice);
			responseBody = readFromStream(response.getEntity().getContent());
			JSONObject responseJSON = new JSONObject(responseBody);
			String productID = responseJSON.getString("productId");
			String price = responseJSON.getString("price");
			System.out.println("ProductID: " +productID+ "  Price: " +price);
			
			StringBuilder headerBuilder = new StringBuilder();
			Header[] headers  = response.getAllHeaders();
			for(Header header : headers){
				headerBuilder.append(header.getName() +" : "+ header.getValue() + ", ");
			}
			System.out.println(headerBuilder.toString());
			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
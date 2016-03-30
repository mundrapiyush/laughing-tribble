package org.solution.database.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.solution.entities.Product;
import org.solution.entities.ProductCategory;

public class Database {

	private final ConcurrentHashMap<UUID, Product> dataStore = new ConcurrentHashMap<>();
	private static Database instance = null;
	
	private Database(){}
	
	public static Database getInstance(){
		if(instance == null){
			synchronized (Database.class) {
				if(instance == null){
					instance = new Database();
				}
				
			}
		}
		return instance;
	}
	
	public boolean addItem(Product product){
		UUID productId = product.getProductId();
		if(dataStore.containsKey(productId))
			return false;
		else{
			dataStore.put(productId, product);
			return true;
		}
	}
	
	public Product getItem(UUID productId){
		
		if(dataStore.containsKey(productId))
			return dataStore.get(productId);
		else{
			return null;
		}
	}
	
	public List<Product> getItems(){
		
		Set<UUID> keySetView = dataStore.keySet();
		Iterator<UUID> keys = keySetView.iterator();
		List<Product> products = new ArrayList<>();
		while(keys.hasNext()){
			products.add(dataStore.get(keys.next()));
		}
		return products;
	}
	
	public List<Product> getItemsByType(ProductCategory category){
		
		Set<UUID> keySetView = dataStore.keySet();
		Iterator<UUID> keys = keySetView.iterator();
		List<Product> products = new ArrayList<>();
		while(keys.hasNext()){
			Product product = dataStore.get(keys.next());
			if(product.getCategory().equals(category))
				products.add(product);
		}
		return products;
	}
	
	public boolean remItem(UUID productId){
		
		if(dataStore.containsKey(productId)){
			dataStore.remove(productId);
			return true;
		}
		else{
			return false;
		}
	}
}

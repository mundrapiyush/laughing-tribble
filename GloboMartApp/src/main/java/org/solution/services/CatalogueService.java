package org.solution.services;

import java.util.List;
import java.util.UUID;

import org.solution.database.services.Database;
import org.solution.entities.Product;
import org.solution.entities.ProductCategory;

public class CatalogueService {
	
	private Database dbInstance = null;
	
	public CatalogueService(Database dbInstance){
		this.dbInstance = dbInstance;
	}

	public List<Product> getProducts() {
		return dbInstance.getItems();
	}
	
	public List<Product> getProductsByType(String type) {
		return dbInstance.getItemsByType(ProductCategory.valueOf(type));
	}
	
	public Product getProduct(String productId){
		
		Product product = dbInstance.getItem(UUID.fromString(productId));
		return product;
	}
	
	public Product addProduct(String name, String category, double priceInDollar){
		
		ProductCategory productCategory = ProductCategory.valueOf(category);
		Product newProduct = new Product(name, productCategory, priceInDollar);
		boolean isSuccess = dbInstance.addItem(newProduct);
		if(isSuccess)
			return newProduct;
		else
			return null;
	}
	
	public boolean removeProduct(String productId){
		
		boolean isSuccess = dbInstance.remItem(UUID.fromString(productId));
		return isSuccess;
	}
	
	

}

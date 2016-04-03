package org.solution.services;

import java.sql.SQLException;
import java.util.List;
import org.solution.database.services.Database;
import org.solution.entities.Product;
import org.solution.entities.ProductCategory;

public class CatalogueService {
	
	private Database dbInstance = null;
	
	public CatalogueService(Database dbInstance){
		this.dbInstance = dbInstance;
	}

	public List<Product> getProducts() {
		List<Product> productList = null;
		try {
			productList = dbInstance.getItems();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productList;
	}
	
	public List<Product> getProductsByType(String type) {
		
		List<Product> productList = null;
		try {
			productList = dbInstance.getItemsByType(ProductCategory.valueOf(type));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productList;
	}
	
	public Product getProduct(String productId){
		
		Product product = null;
		try {
			product = dbInstance.getItem(Integer.parseInt(productId));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product;
	}
	
	public Product addProduct(String name, String category, double priceInDollar){
		
		ProductCategory productCategory = ProductCategory.valueOf(category);
		Product newProduct = new Product(-1, name, productCategory, priceInDollar);
		try {
			newProduct = dbInstance.addItem(newProduct);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newProduct;
	}
	
	public boolean removeProduct(String productId){
		
		boolean isSuccess = false;
		try {
			isSuccess = dbInstance.remItem(Integer.parseInt(productId));
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
}

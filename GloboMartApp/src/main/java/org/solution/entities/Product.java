package org.solution.entities;

public class Product {

	private final long productId;
	private final String name;
	private final ProductCategory category;
	private final double priceInDollar;
	
	public Product(long productId, String name, ProductCategory category, double priceInDollar){
		this.productId = productId;
		this.name = name;
		this.category = category;
		this.priceInDollar = priceInDollar;
	}	
	public String getName() {
		return name;
	}
	public ProductCategory getCategory() {
		return category;
	}
	public double getPriceInDollar() {
		return priceInDollar;
	}
	public long getProductId() {
		return productId;
	}	
}

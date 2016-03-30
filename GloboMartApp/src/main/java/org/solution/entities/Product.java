package org.solution.entities;

import java.util.UUID;

public class Product {

	private final UUID productId;
	private final String name;
	private final ProductCategory category;
	private final double priceInDollar;
	
	public Product(String name, ProductCategory category, double priceInDollar){
		this.productId = UUID.randomUUID();
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
	public UUID getProductId() {
		return productId;
	}	
}

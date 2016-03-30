package org.solution.services;

import java.util.UUID;

import org.solution.database.services.Database;
import org.solution.entities.Product;

public class PricingService {

	private Database dbInstance = null;
	
	public PricingService(Database dbInstance) {
		this.dbInstance = dbInstance;
	}
	
	public double getPrice(String productId) {
		
		Product product = dbInstance.getItem(UUID.fromString(productId));
		double priceInDollar = 0.0;
		if(product != null)
			priceInDollar = product.getPriceInDollar();	
		return priceInDollar;
	}
}

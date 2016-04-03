package org.solution.services;

import java.sql.SQLException;
import org.solution.database.services.Database;
import org.solution.entities.Product;

public class PricingService {

	private Database dbInstance = null;
	
	public PricingService(Database dbInstance) {
		this.dbInstance = dbInstance;
	}
	
	public double getPrice(String productId) {
		
		Product product = null;
		double priceInDollar = 0.0;
		try {
			product = dbInstance.getItem(Integer.parseInt(productId));
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(product != null)
			priceInDollar = product.getPriceInDollar();	
		return priceInDollar;
	}
}

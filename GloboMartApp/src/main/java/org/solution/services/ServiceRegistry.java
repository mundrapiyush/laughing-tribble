
package org.solution.services;

import java.util.concurrent.ConcurrentHashMap;

import org.solution.database.services.Database;
import org.solution.entities.ServiceType;

public class ServiceRegistry {
	
	private static ConcurrentHashMap<ServiceType, Object> registry = new ConcurrentHashMap<>();
	private static Database dbInstance = Database.getInstance();
	
	public ServiceRegistry() {
				
		CatalogueService catalogueService = new CatalogueService(dbInstance);
		PricingService pricingService = new PricingService(dbInstance);
				
		registry.put(ServiceType.CATALOGUE_SERVICE, catalogueService);
		registry.put(ServiceType.PRICING_SERVICE, pricingService);
	}
	
	public static Object getService(ServiceType type){
		return registry.get(type);
	}	
}
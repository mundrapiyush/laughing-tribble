package org.solution.web.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.solution.entities.ServiceType;
import org.solution.services.PricingService;
import org.solution.services.ServiceRegistry;

@Path("/price")
public class PricingServiceEndpoint {

private PricingService pricingService;
	
	public PricingServiceEndpoint() {
		pricingService = (PricingService)ServiceRegistry.getService(ServiceType.PRICING_SERVICE);
	}
	
	@GET
	@Path("/{productID}")
	@Produces("application/json")
	public Response getPrice(@PathParam("productID") String productId) {
		
		ResponseBuilder response = new ResponseBuilderImpl();
		double price = pricingService.getPrice(productId);
		try {
			JSONObject responseObject = new JSONObject();
			responseObject.put("productId", productId);
			responseObject.put("price", price);
			response.entity(responseObject);
			response.status(Response.Status.OK);

		} catch (JSONException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}
}

package org.solution.web.services;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.solution.entities.Product;
import org.solution.entities.ServiceType;
import org.solution.services.CatalogueService;
import org.solution.services.ServiceRegistry;


@Path("/products")
public class CatalogueServiceEndpoint {

	private CatalogueService catalogueService;
	
	public CatalogueServiceEndpoint() {
		catalogueService = (CatalogueService)ServiceRegistry.getService(ServiceType.CATALOGUE_SERVICE);
	}
	
	@GET
	@Produces("application/json")
	public Response getProducts(@QueryParam("category") String category) {
		
		ResponseBuilder response = new ResponseBuilderImpl();
		List<Product> productList = null;
		if(category != null)
			productList = catalogueService.getProductsByType(category);
		else
			productList = catalogueService.getProducts();
		
		ObjectMapper mapper = new ObjectMapper();
		JSONArray productJSONArray = new JSONArray();
		try {
			for(Product product : productList){
				String jsonString = mapper.writeValueAsString(product);
				productJSONArray.put(new JSONObject(jsonString));
			}
			JSONObject responseObject = new JSONObject();
			responseObject.put("products", productJSONArray);
			response.entity(responseObject);
			response.status(Response.Status.OK);

		} catch (IOException | JSONException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}
	
	@GET
	@Path("/{productId}")
	@Produces("application/json")
	public Response getProduct(@PathParam("productId") String productId) {
		
		ResponseBuilder response = new ResponseBuilderImpl();
		Product product = catalogueService.getProduct(productId);
	    
		CacheControl cacheControl = new CacheControl();
	    cacheControl.setMaxAge(3600);
	    cacheControl.setPrivate(false);
		
	    try {
			JSONObject responseObject = new JSONObject();
			responseObject.put("productId", product.getProductId());
			responseObject.put("price", product.getPriceInDollar());
			responseObject.put("category", product.getCategory().name());
			responseObject.put("name", product.getName());
			response.entity(responseObject);
			response.status(Response.Status.OK);
			response.cacheControl(cacheControl);

		} catch (JSONException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}
	
	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response addProduct(JSONObject product, @Context UriInfo uriInfo){
		
		ResponseBuilder response = new ResponseBuilderImpl();
		String name = null;
		String category = null;
		float price = -1f;
		
		try{
			name = product.getString("name");
			category = product.getString("category");
			price = new Float(product.getString("price")).floatValue();
		} catch(JSONException e){
			response.entity(e.getMessage());
			response.status(Response.Status.BAD_REQUEST);
			return response.build();
		}
		
		try {
			Product newProduct = catalogueService.addProduct(name, category, price);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("productID", newProduct.getProductId());
			jsonObject.put("itemURL", uriInfo.getRequestUri() + "/" + newProduct.getProductId());
			response.entity(jsonObject);
			response.status(Response.Status.OK);
			
		} catch (JSONException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
			return response.build();
		}
		return response.build();
	}
	
	@DELETE
	@Path("/{productID}")
	@Produces("application/json")
	public Response deleteProduct(@PathParam("productID") String productID){

		ResponseBuilder response = new ResponseBuilderImpl();
		boolean isSuccess = catalogueService.removeProduct(productID);

		if(isSuccess)
			response.status(Response.Status.OK);
		else
			response.status(Response.Status.NOT_FOUND);
		
		return response.build();
	}
}
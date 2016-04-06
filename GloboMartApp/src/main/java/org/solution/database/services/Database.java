package org.solution.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.solution.entities.Product;
import org.solution.entities.ProductCategory;

public class Database {

	private static Database instance = null;
	private Connection conn;
	
	private static final String DB_TRUNCATE_SCHEMA = "DROP SCHEMA PUBLIC CASCADE";
	private static final String DB_CREATE_PRODUCT_TAB_QRY = "CREATE TABLE PRODUCTS ("
														  + "PRODUCT_ID INTEGER IDENTITY PRIMARY KEY,"
														  + "PRODUCT_NAME VARCHAR(50)  NOT NULL,"
														  + "PRODUCT_CATEGORY VARCHAR(50),"
														  + "PRICE  DOUBLE);";
	
	private static final String DB_INSERT_PRODUCT_TAB_QRY = "INSERT INTO PRODUCTS"
													      + "(PRODUCT_ID, PRODUCT_NAME, PRODUCT_CATEGORY, PRICE)"
													      + " VALUES (NULL,?,?,?)";
	
	private static final String DB_SELECT_PRODUCT_TAB_QRY = "SELECT * FROM PRODUCTS";
	private static final String DB_SELECT_PRODUCT_FRM_PRODUCTID_TAB_QRY = "SELECT * FROM PRODUCTS WHERE PRODUCT_ID=?";
	private static final String DB_SELECT_PRODUCT_FRM_CATEGORY_TAB_QRY = "SELECT * FROM PRODUCTS WHERE PRODUCT_CATEGORY=?";
	
	private static final String DB_DELETE_PRODUCT_FRM_PRODUCTID_TAB_QRY = "DELETE FROM PRODUCTS WHERE PRODUCT_ID=?";
	private static final String DB_SHUTDOWN_DATABASE_QRY = "SHUTDOWN";
	
	private Database(){
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			
			//conn = DriverManager.getConnection("jdbc:hsqldb:globomartDB","SA","");
			conn = DriverManager.getConnection("jdbc:hsqldb:hsql://ec2-54-255-136-38.ap-southeast-1.compute.amazonaws.com/xdb", "SA", "");
			conn.createStatement().executeUpdate(DB_TRUNCATE_SCHEMA);
			conn.createStatement().executeUpdate(DB_CREATE_PRODUCT_TAB_QRY);
			conn.commit();
			
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
	
	public Product addItem(Product product) throws SQLException{
		int productId = -1;

		PreparedStatement prepStatement = conn.prepareStatement(DB_INSERT_PRODUCT_TAB_QRY,Statement.RETURN_GENERATED_KEYS);
		prepStatement.setString(1, product.getName());
		prepStatement.setString(2, product.getCategory().name());
		prepStatement.setDouble(3, product.getPriceInDollar());
		prepStatement.executeUpdate();
		conn.commit();
		
		try (ResultSet generatedKeys = prepStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                productId = generatedKeys.getInt(1);
            }
        }
		Product newProduct = new Product(productId, product.getName(), product.getCategory(), product.getPriceInDollar());
		return newProduct;
	}
	
	public Product getItem(long productId) throws SQLException{
		
		Product newProduct = null;
		PreparedStatement prepStatement = conn.prepareStatement(DB_SELECT_PRODUCT_FRM_PRODUCTID_TAB_QRY);
		prepStatement.setInt(1, (int)productId);
		prepStatement.execute();
		
		try (ResultSet generatedKeys = prepStatement.getResultSet()) {
            if (generatedKeys.next()) {
                productId = generatedKeys.getInt(1);
                String productName = generatedKeys.getString(2);
                String productCategory = generatedKeys.getString(3);
                double price = generatedKeys.getDouble(4);
                newProduct = new Product(productId, productName, ProductCategory.valueOf(productCategory), price);
            }
        }
		return newProduct;
	}
	
	public List<Product> getItems() throws SQLException{
		
		List<Product> products = new ArrayList<>();
		PreparedStatement prepStatement = conn.prepareStatement(DB_SELECT_PRODUCT_TAB_QRY);
		prepStatement.execute();
		
		try (ResultSet generatedKeys = prepStatement.getResultSet()) {
            while (generatedKeys.next()) {
                long productId = generatedKeys.getInt(1);
                String productName = generatedKeys.getString(2);
                String productCategory = generatedKeys.getString(3);
                double price = generatedKeys.getDouble(4);
                Product product = new Product(productId, productName, ProductCategory.valueOf(productCategory), price);
                products.add(product);
            }
        }		
		return products;
	}
	
	public List<Product> getItemsByType(ProductCategory category) throws SQLException{
		
		List<Product> products = new ArrayList<>();
		PreparedStatement prepStatement = conn.prepareStatement(DB_SELECT_PRODUCT_FRM_CATEGORY_TAB_QRY);
		prepStatement.setString(1, category.name());
		prepStatement.execute();
		
		try (ResultSet generatedKeys = prepStatement.getResultSet()) {
            while (generatedKeys.next()) {
                long productId = generatedKeys.getInt(1);
                String productName = generatedKeys.getString(2);
                String productCategory = generatedKeys.getString(3);
                double price = generatedKeys.getDouble(4);
                Product product = new Product(productId, productName, ProductCategory.valueOf(productCategory), price);
                products.add(product);
            }
        }		
		return products;
	}
	
	public boolean remItem(long productId) throws SQLException{
		
		PreparedStatement prepStatement = conn.prepareStatement(DB_DELETE_PRODUCT_FRM_PRODUCTID_TAB_QRY);
		prepStatement.setInt(1, (int)productId);
		prepStatement.executeUpdate();
		conn.commit();
		return true;
	}
}

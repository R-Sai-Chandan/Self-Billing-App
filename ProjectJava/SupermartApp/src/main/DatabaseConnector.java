package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    private final String url = "jdbc:mysql://localhost:3306/supermarket";
    private final String user = "root";
    private final String password = "Apple@123";

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT id, name, price, quantity, image_url, description, type FROM products";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String imageUrl = resultSet.getString("image_url");
                String description = resultSet.getString("description");
                String type = resultSet.getString("type");

                Product product = new Product(id, name, price, quantity, imageUrl, description, type);
                products.add(product);

                System.out.println("Fetched product: " + product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
}

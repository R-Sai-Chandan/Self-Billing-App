package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SelectedProductsDatabase {
    private final String url = "jdbc:mysql://localhost:3306/supermarket";
    private final String user = "root";
    private final String password = "Apple@123";

    // Update quantity of the product in the product table
    private void updateProductQuantity(int productId, int quantity) {
        String query = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProductQuantity(int productId) {
        String query = "UPDATE products SET quantity = quantity + 1 WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add or update product in selected_products
    public void addProduct(Product product) {
        String query = "INSERT INTO selected_products (id, name, price, quantity, image, description, type) VALUES (?, ?, ?, ?, ?, ?, ?) "
                +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1";
        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getId());
            statement.setString(2, product.getName());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, 1); // Always inserting quantity as 1
            statement.setString(5, product.getImageUrl());
            statement.setString(6, product.getDescription());
            statement.setString(7, product.getType());
            statement.executeUpdate();
            updateProductQuantity(product.getId(), 1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Delete product from selected_products
    public void deleteProduct(Product product) {
        String query = "UPDATE selected_products SET quantity = quantity - 1 WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getId());
            int rowsAffected = statement.executeUpdate();
            updateProductQuantity(product.getId());
            if (rowsAffected > 0) {
                // Check the updated quantity
                String checkQuery = "SELECT quantity FROM selected_products WHERE id = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, product.getId());
                    ResultSet resultSet = checkStmt.executeQuery();
                    if (resultSet.next()) {
                        int quantity = resultSet.getInt("quantity");
                        if (quantity <= 0) {
                            // Remove the product if quantity is zero or less
                            String deleteQuery = "DELETE FROM selected_products WHERE id = ?";
                            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                                deleteStmt.setInt(1, product.getId());
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getSelectedProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM selected_products";

        try (Connection connection = DriverManager.getConnection(url, user, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String imageUrl = resultSet.getString("image");
                String description = resultSet.getString("description");
                String type = resultSet.getString("type");

                Product product = new Product(id, name, price, quantity, imageUrl, description, type);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public void clearSelectedProducts() {
        String query = "DELETE FROM selected_products";

        try (Connection connection = DriverManager.getConnection(url, user, password);
                Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

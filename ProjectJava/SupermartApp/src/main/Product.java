package main;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;
    private String description;
    private String type;

    public Product(int id, String name, double price, int quantity, String imageUrl, String description, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.description = description;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Price: " + price + ", Quantity: " + quantity + ", Description: " + description + ", Type: " + type;
    }
}

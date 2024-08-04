package main;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Callback;

import java.util.List;

public class Main extends Application {
    private DatabaseConnector dbConnector = new DatabaseConnector();
    private SelectedProductsDatabase selectedProductsDB = new SelectedProductsDatabase();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setFullScreen(true);

        Text welcomeText = new Text("Welcome to Sai's Supermarket. Hope you have a great time shopping!");
        welcomeText.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-alignment: center;");
        welcomeText.setFont(Font.font("Jokerman", 24));

        Button fetchBtn = new Button("Fetch Products");
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(welcomeText, fetchBtn);

        Scene scene1 = new Scene(root, 1280, 720);
        primaryStage.setTitle("Supermarket App");
        primaryStage.setScene(scene1);
        primaryStage.show();

        fetchBtn.setOnAction(e -> showProductScene(primaryStage));
    }

    private void showProductScene(Stage primaryStage) {
        List<Product> products = dbConnector.getProducts();

        TableView<Product> productTable = new TableView<>();
        TableColumn<Product, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(param -> {
            ImageView imageView = new ImageView(new Image(param.getValue().getImageUrl()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return new ReadOnlyObjectWrapper<>(imageView);
        });
        imageColumn.setCellFactory(col -> new TableCell<Product, ImageView>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(ImageView image, boolean empty) {
                super.updateItem(image, empty);
                setGraphic(image);
            }
        });

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        productTable.getColumns().addAll(imageColumn, nameColumn, priceColumn, quantityColumn, descriptionColumn,
                typeColumn);
        productTable.getItems().addAll(products);

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");

        addButton.setOnAction(e -> {
            for (Product product : productTable.getSelectionModel().getSelectedItems()) {
                selectedProductsDB.addProduct(product);
            }
        });

        deleteButton.setOnAction(e -> {
            for (Product product : productTable.getSelectionModel().getSelectedItems()) {
                selectedProductsDB.deleteProduct(product);
            }
        });

        Button productListButton = new Button("Product List");
        Button totalBillButton = new Button("Total Bill");

        productListButton.setOnAction(e -> showProductScene(primaryStage));
        totalBillButton.setOnAction(e -> showTotalBillScene(primaryStage));

        VBox rightButtons = new VBox(10, addButton, deleteButton);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        VBox leftButtons = new VBox(10, productListButton, totalBillButton);
        leftButtons.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setCenter(productTable);
        root.setRight(rightButtons);
        root.setLeft(leftButtons);

        Scene productScene = new Scene(root, 1280, 720);

        applyTransition(primaryStage.getScene(), productScene);
        primaryStage.setScene(productScene);
    }

    private void showTotalBillScene(Stage primaryStage) {
        List<Product> selectedProducts = selectedProductsDB.getSelectedProducts();

        Button fetchBtn = new Button("Product List");
        fetchBtn.setOnAction(e -> showProductScene(primaryStage));
        Button deleteButton = new Button("Delete");

        TableView<Product> productTable = new TableView<>();
        deleteButton.setOnAction(e -> {
            for (Product product : productTable.getSelectionModel().getSelectedItems()) {
                selectedProductsDB.deleteProduct(product);
            }
            showTotalBillScene(primaryStage);
        });

        TableColumn<Product, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(param -> {
            ImageView imageView = new ImageView(new Image(param.getValue().getImageUrl()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return new ReadOnlyObjectWrapper<>(imageView);
        });
        imageColumn.setCellFactory(col -> new TableCell<Product, ImageView>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(ImageView image, boolean empty) {
                super.updateItem(image, empty);
                setGraphic(image);
            }
        });

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        productTable.getColumns().addAll(imageColumn, nameColumn, priceColumn, quantityColumn, descriptionColumn,
                typeColumn);
        productTable.getItems().addAll(selectedProducts);

        double totalBill = selectedProducts.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();

        Text totalBillText = new Text("Total Bill: $" + totalBill);
        Button payButton = new Button("Pay");
        payButton.setOnAction(e -> {
            selectedProductsDB.clearSelectedProducts();
            start(primaryStage);
        });

        VBox tableAndBillBox = new VBox(10, productTable, totalBillText, payButton);
        tableAndBillBox.setPadding(new Insets(20));
        tableAndBillBox.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(10, fetchBtn, deleteButton);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);

        HBox root = new HBox(20, buttonBox, tableAndBillBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene totalBillScene = new Scene(root, 1280, 720);
        applyTransition(primaryStage.getScene(), totalBillScene);
        primaryStage.setScene(totalBillScene);
    }

    private void applyTransition(Scene fromScene, Scene toScene) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), fromScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), toScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }
}

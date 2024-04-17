package com.surya.shopcart;

public class Product {
    String image;
    String title;
    String description;
    double price;
    String id;
    String path;

    public byte getQuantity() {
        return quantity;
    }

    public void setQuantity(byte quantity) {
        this.quantity = quantity;
    }

    byte quantity;

    public Product(String id, String image, String title, String description, double price, String path) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.price = price;
        this.path = path;
    }

    public Product(String id, String image, String title, String description, double price, String path, byte quantity) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.price = price;
        this.path = path;
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }
}

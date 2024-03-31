package com.surya.shopcart;

public class Product {
    String image;
    String title;
    String description;
    double price;

    public Product(String image, String title, String description, double price) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.price = price;
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
}

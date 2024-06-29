package com.surya.shopngo

data class Product (
    var id: String,
    var image: String,
    var title: String,
    var description: String,
    var price: Double,
    var path: String?,
    var quantity: Byte = 0
) {
    fun getImage(): String {
        return image
    }
    fun getPrice(): Double {
        return price
    }
    fun getTitle(): String {
        return title
    }
    fun getDescription(): String {
        return description
    }
    fun getId(): String {
        return id;
    }
}

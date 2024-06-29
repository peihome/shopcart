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
    fun fetchId(): String {
        return id
    }
    fun fetchImage(): String {
        return image
    }
    fun fetchPrice(): Double {
        return price
    }
    fun fetchTitle(): String {
        return title
    }
    fun fetchDescription(): String {
        return description
    }
}

package com.surya.shopngo

class Product {
    @JvmField
    var image: String
    @JvmField
    var title: String
    @JvmField
    var description: String
    @JvmField
    var price: Double
    @JvmField
    var id: String
    var path: String
    @JvmField
    var quantity: Byte = 0

    constructor(
        id: String,
        image: String,
        title: String,
        description: String,
        price: Double,
        path: String
    ) {
        this.id = id
        this.image = image
        this.title = title
        this.description = description
        this.price = price
        this.path = path
    }

    constructor(
        id: String,
        image: String,
        title: String,
        description: String,
        price: Double,
        path: String,
        quantity: Byte
    ) {
        this.id = id
        this.image = image
        this.title = title
        this.description = description
        this.price = price
        this.path = path
        this.quantity = quantity
    }
}

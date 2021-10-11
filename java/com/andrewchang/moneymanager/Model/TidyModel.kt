package com.andrewchang.moneymanager.Model

class TidyModel {
    var type: String = ""
    var price: Int = 0

    constructor(type: String, price: Int) : super() {
        this.type = type
        this.price = price
    }
}
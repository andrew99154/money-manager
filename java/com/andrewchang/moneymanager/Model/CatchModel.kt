package com.andrewchang.moneymanager.Model

class CatchModel {
    var type: String = ""
    var name: String = ""
    var price: String = ""
    var isPay: Boolean = true
    var recordDownTime: String = ""
    var chooseTimeWhenAdd: String = ""

    constructor(
        type: String,
        name: String,
        price: String,
        isPay: Boolean,
        recordDownTime: String,
        chooseTimeWhenAdd: String
    ) : super() {
        this.type = type
        this.name = name
        this.price = price
        this.isPay = isPay
        this.recordDownTime = recordDownTime
        this.chooseTimeWhenAdd = chooseTimeWhenAdd
    }
}
package com.andrewchang.moneymanager.Model

class MonthModel {
    var month: String = ""
    var click: Boolean = false

    constructor(month: String, click: Boolean) : super() {
        this.month = month
        this.click = click
    }
}
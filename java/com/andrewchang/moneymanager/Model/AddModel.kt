package com.andrewchang.moneymanager.Model

class AddModel {
    var icon: Int? = -1
    var name: String? = ""


    constructor(icon: Int, name: String) : super() {
        this.icon = icon
        this.name = name
    }
}
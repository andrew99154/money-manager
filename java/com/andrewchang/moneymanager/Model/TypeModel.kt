package com.andrewchang.moneymanager.Model

class TypeModel {
    var type: Int = 0
    var catchModel: CatchModel? = null

    constructor(type: Int, catchModel: CatchModel?) : super() {
        this.type = type
        this.catchModel = catchModel
    }
}
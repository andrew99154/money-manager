package com.andrewchang.moneymanager.ItemCell

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.andrewchang.moneymanager.Model.CatchModel
import com.andrewchang.moneymanager.Model.DataHelper
import com.andrewchang.moneymanager.Model.TidyModel
import com.andrewchang.moneymanager.R

class ItemCell: RelativeLayout {
    var imageView: ImageView? = null
    var typeName:TextView? = null
    var name:TextView? = null
    var price:TextView? = null

    constructor(context: Context?) : super(context) {
        createView()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        createView()
    }

    fun createView() {
        var view = LayoutInflater.from(context).inflate(R.layout.item_cell, null, true)
        this.imageView = view.findViewById(R.id.image)
        this.typeName = view.findViewById(R.id.typeName)
        this.name = view.findViewById(R.id.name)
        this.price = view.findViewById(R.id.price)
        this.addView(view)
    }
    fun setHolder(catchModel: CatchModel){
        var dataHelper = DataHelper()
        name?.text = catchModel.name
        if (catchModel.isPay == false) {
            price?.text = catchModel.price
        }else{
            price?.text = "-"+catchModel.price
        }
        typeName?.text = catchModel.type
        dataHelper.addAllInfo(3)
        var icon = dataHelper.getIcon(catchModel.type)
        if (icon != 0) {
            imageView?.setBackgroundResource(icon)
        }
    }
    fun setPieHolder(tidyModel: TidyModel){
        var dataHelper = DataHelper()
        typeName?.text = tidyModel.type
        price?.text = tidyModel.price.toString()
        dataHelper.addAllInfo(3)
        var icon = dataHelper.getIcon(tidyModel.type)
        if (icon != 0) {
            imageView?.setBackgroundResource(icon)
        }

    }
    fun reset(){
        typeName?.text = ""
        price?.text = ""
        typeName?.text = ""
        imageView?.setBackgroundResource(0)
    }
}
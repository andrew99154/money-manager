package com.andrewchang.moneymanager.ItemCell

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import com.andrewchang.moneymanager.Model.CatchModel
import com.andrewchang.moneymanager.R

class TextCell: RelativeLayout {
    var monthTitle : TextView? = null

    constructor(context: Context?) : super(context) {
        createView()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        createView()
    }

    fun createView() {
        var view = LayoutInflater.from(context).inflate(R.layout.text_cell, null, true)
        this.monthTitle= view.findViewById(R.id.month_title)
        this.addView(view)
    }
    fun setHolder(catchModel: CatchModel){
       monthTitle?.text = catchModel.chooseTimeWhenAdd
    }
    fun reset(){
        monthTitle?.text = ""
    }
}
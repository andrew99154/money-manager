package com.andrewchang.moneymanager.Model

import android.content.Context
import com.andrewchang.moneymanager.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var finalData: ArrayList<ArrayList<CatchModel>> = ArrayList()

class DataHelper {
    var array: ArrayList<AddModel> = ArrayList()
    var dayArray: ArrayList<String> = ArrayList()


    constructor() : super() {
        addAllInfo(1)
    }

    fun addAllInfo(number: Int) {
        array.clear()
        if (number == 1)
            addPayInfo()
        else if (number == 2)
            addSallaryInfo()
        else if (number == 3) {
            addSallaryInfo()
            addPayInfo()
        }
    }


    private fun addPayInfo() {
        array.add(AddModel(R.drawable.ic_cutlery, "餐飲"))
        array.add(AddModel(R.drawable.ic_hydro_power, "水電費"))
        array.add(AddModel(R.drawable.ic_bus, "交通"))
        array.add(AddModel(R.drawable.ic_home, "家"))
        array.add(AddModel(R.drawable.ic_car, "車"))
        array.add(AddModel(R.drawable.ic_joystick, "娛樂"))
        array.add(AddModel(R.drawable.ic_shopping_bag, "購物"))
        array.add(AddModel(R.drawable.ic_shirt, "服裝"))
        array.add(AddModel(R.drawable.ic_security, "保險"))
        array.add(AddModel(R.drawable.ic_tax, "稅"))
        array.add(AddModel(R.drawable.ic_phone, "電話費"))
        array.add(AddModel(R.drawable.ic_cigarette, "香菸"))
        array.add(AddModel(R.drawable.ic_medical_kit, "健康"))
        array.add(AddModel(R.drawable.ic_gym, "運動"))
        array.add(AddModel(R.drawable.ic_baby, "孩子"))
        array.add(AddModel(R.drawable.ic_dog, "寵物"))
        array.add(AddModel(R.drawable.ic_lipstick, "美容"))
        array.add(AddModel(R.drawable.ic_smartphone, "電子"))
        array.add(AddModel(R.drawable.ic_pen, "文具"))
        array.add(AddModel(R.drawable.ic_wine, "酒"))
        array.add(AddModel(R.drawable.ic_vegetables, "蔬菜"))
        array.add(AddModel(R.drawable.ic_ice_cream, "甜點"))
        array.add(AddModel(R.drawable.ic_gift, "禮品"))
        array.add(AddModel(R.drawable.ic_friends, "社交"))
        array.add(AddModel(R.drawable.ic_travel, "旅行"))
        array.add(AddModel(R.drawable.ic_mortarboard, "教育"))
        array.add(AddModel(R.drawable.ic_apple, "水果"))
        array.add(AddModel(R.drawable.ic_book, "書籍"))
        array.add(AddModel(R.drawable.ic_folder, "辦公"))
        array.add(AddModel(R.drawable.ic_clipboards, "其他"))
    }

    private fun addSallaryInfo() {
        array.add(AddModel(R.drawable.ic_wallet, "薪水"))
        array.add(AddModel(R.drawable.ic_money, "獎金"))
        array.add(AddModel(R.drawable.ic_goodgift, "捐贈"))
        array.add(AddModel(R.drawable.ic_label, "買賣"))
        array.add(AddModel(R.drawable.ic_rental, "出租"))
        array.add(AddModel(R.drawable.ic_refund, "退款"))
        array.add(AddModel(R.drawable.ic_coupon, "優惠券"))
        array.add(AddModel(R.drawable.ic_lottery_machine, "彩票"))
        array.add(AddModel(R.drawable.ic_line_chart, "股息"))
        array.add(AddModel(R.drawable.ic_piggy_bank, "投資"))
        array.add(AddModel(R.drawable.ic_clipboards, "其他"))
    }


    fun getAllData(): ArrayList<AddModel> {
        return array
    }

    fun saveData(array: ArrayList<CatchModel>, context: Context) {
        val profilePreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
        val profileEditor = profilePreferences.edit()
        profileEditor.putString("array", Gson().toJson(array))
        profileEditor.commit()
    }

    fun getLocalStorageData(context: Context): ArrayList<CatchModel> {
        val profilePreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
        val type = object : TypeToken<ArrayList<CatchModel>>() {}.type
        val arrayString = profilePreferences.getString("array", null)
        if (arrayString != null) {
            val array = Gson().fromJson<ArrayList<CatchModel>>(arrayString, type)
            return array
        } else {
            return ArrayList()
        }
    }

    fun getIcon(name: String): Int {
        for (value in array) {
            if (name == value.name) {
                return value.icon!!
            }
        }
        return 0
    }

    fun getSingleYearData(context: Context, year: String): ArrayList<CatchModel> {
        var temporaryRecordArray: ArrayList<CatchModel> = ArrayList()
        for (value in getLocalStorageData(context)) {
            var splitModelTime = value.chooseTimeWhenAdd.split("-")
            if (year == splitModelTime[0]) {
                temporaryRecordArray.add(value)
            }
        }
        return temporaryRecordArray
    }

    fun getSingleMonthData(context: Context, year: String, month: String):ArrayList<CatchModel>{
        var temporaryRecordArray: ArrayList<CatchModel> = ArrayList()
        for (value in getLocalStorageData(context)){
            var splitModelTime = value.chooseTimeWhenAdd.split("-")
            if (year + "-" + month == splitModelTime[0] + "-" + splitModelTime[1]) {
                temporaryRecordArray.add(value)
            }
        }
        return temporaryRecordArray
    }

    fun getNowTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    }
}
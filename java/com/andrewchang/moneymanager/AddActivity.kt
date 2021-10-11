package com.andrewchang.moneymanager

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.andrewchang.moneymanager.Model.AddModel
import com.andrewchang.moneymanager.Model.CatchModel
import com.andrewchang.moneymanager.Model.DataHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.pdog.dimension.dp
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.add_things.view.*
import kotlinx.android.synthetic.main.dialog_activity.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.app.DatePickerDialog as DatePickerDialog1

private lateinit var firebaseAnalytics: FirebaseAnalytics

class AddActivity : Activity() {
    var gridAdaptor: GridAdapter? = null
    var dataHelper = DataHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val typeArray = arrayOf("支出", "收入")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            typeArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        changeSpinner.adapter = adapter;
        changeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    dataHelper.addAllInfo(1)
                } else {
                    dataHelper.addAllInfo(2)
                }
                gridAdaptor!!.addArray = dataHelper.getAllData()
                gridAdaptor!!.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

        go_back_botton_at_add_activity.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        //支出、收入下拉選單
        var addThings: GridView = findViewById(R.id.txt_addThings)
        var layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        addThings.layoutParams = layoutParams

        addThings.setOnItemClickListener { adapterView, view, i, l ->
            val dialog = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_activity, null)

            dialog.setContentView(view)
            dialog.show()
            view.choose_num_date.text =
                SimpleDateFormat("yyyy.M.d").format(System.currentTimeMillis())


            view.add_choose_date_btn.setOnClickListener {
                var time = view.choose_num_date.text.split(".")
                DatePickerDialog1(this, { _, year, month, day ->
                    run {
                        view.choose_num_date.text = setDateFormat(year, month, day)
                    }
                }, time[0].toInt(), time[1].toInt() - 1, time[2].toInt()).show()
            }

            view.dialogContinueBtn.setOnClickListener {

                if (view.dialogNameEt.text.isNullOrEmpty() || view.dialogMoneyEt.text.isNullOrEmpty()) {
                    Toast.makeText(this, "請輸入完整資訊!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else if (view.dialogMoneyEt.text.toString().startsWith('0') || view.dialogMoneyEt.text.toString() == "0") {
                    Toast.makeText(this, "請輸入正確金額!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                var splitGetNow = getNow().split("-")
                var writeDateTV = view.choose_num_date.text.split(".")
                var resultSelectTime = ""

                if (writeDateTV[1] == "10" || writeDateTV[1] == "11" || writeDateTV[1] == "12") {
                    if (writeDateTV[2].length == 1) {
                        resultSelectTime =
                            writeDateTV[0] + "-" + writeDateTV[1] + "-" + "0" + writeDateTV[2]
                    } else {
                        resultSelectTime =
                            writeDateTV[0] + "-" + writeDateTV[1] + "-" + writeDateTV[2]
                    }
                } else {
                    if (writeDateTV[2].length == 1) {
                        resultSelectTime =
                            writeDateTV[0] + "-" + "0" + writeDateTV[1] + "-" + "0" + writeDateTV[2]
                    } else {
                        resultSelectTime =
                            writeDateTV[0] + "-" + "0" + writeDateTV[1] + "-" + writeDateTV[2]
                    }

                }

                var catchModel = CatchModel(
                    view.text.text.toString(),
                    view.dialogNameEt.text.toString(),
                    view.dialogMoneyEt.text.toString(),
                    checkIsPay(changeSpinner.selectedItem.toString()),
                    getNow(),
                    resultSelectTime
                )

                val oldData = dataHelper.getLocalStorageData(this)
                if (oldData.size != 0) {
                    oldData.add(0, catchModel)
                    dataHelper.saveData(oldData, this)
                } else {
                    var array: ArrayList<CatchModel> = ArrayList()
                    array.add(catchModel)
                    dataHelper.saveData(array, this)
                }
                val bundle = Bundle()
                if (resultSelectTime == splitGetNow[0] + "-" + splitGetNow[1]
                ) {
                    bundle.putString("storage_data_time_type", "newData")
                } else {
                    if (resultSelectTime.split("-")[0] > splitGetNow[0]) {
                        bundle.putString("storage_data_time_type", "newData")
                    } else if (resultSelectTime.split("-")[0] == splitGetNow[0] && resultSelectTime.split(
                            "-"
                        )[1] > splitGetNow[1]
                    ) {
                        bundle.putString("storage_data_time_type", "newData")
                    } else {
                        bundle.putString("storage_data_time_type", "oldData")
                    }
                }
                firebaseAnalytics.logEvent("storage_data", bundle)
                dialog.dismiss()
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            view.dialogCancelBtn.setOnClickListener {

                dialog.dismiss()
            }
            var image: ImageView = view.image
            if (gridAdaptor != null && gridAdaptor!!.addArray[i].icon != -1) {
                image.setBackgroundResource(gridAdaptor!!.addArray[i].icon!!)
            }
            var text: TextView = view.text
            if (gridAdaptor != null && gridAdaptor!!.addArray[i].icon != -1)
                text.setText(gridAdaptor!!.addArray[i].name)
            text.textSize = 7.dp.toFloat()
            var dialogMoneyEt: EditText = view.dialogMoneyEt
            dialogMoneyEt.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
            view.dialogNameEt.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))


        }

        gridAdaptor = GridAdapter(this@AddActivity, dataHelper.getAllData())
        addThings.adapter = gridAdaptor
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    class GridAdapter : BaseAdapter {
        var addArray: ArrayList<AddModel> = ArrayList()
        var context: Context? = null

        constructor(context: Context, array: ArrayList<AddModel>) : super() {
            this.context = context
            this.addArray = array
        }

        override fun getCount(): Int {
            return addArray.size
        }

        override fun getItem(position: Int): Any {
            return addArray[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val addModel = addArray[position]
            var inflator =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var addView = inflator.inflate(R.layout.add_things, null)
            if (addModel.icon != -1) {
                addView.imageThings.setBackgroundResource(addModel.icon!!)
            }
            addView.txt_Name.text = addModel.name!!

            return addView
        }
    }

    fun checkIsPay(check: String): Boolean {
        if (check == "支出")
            return true
        else return false
    }

    fun getNow(): String {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        } else {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        }
    }

    fun setDateFormat(year: Int, month: Int, day: Int): String {
        return "$year.${month + 1}.$day"
    }

}

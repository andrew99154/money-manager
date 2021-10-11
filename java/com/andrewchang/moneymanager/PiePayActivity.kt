package com.andrewchang.moneymanager

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrewchang.moneymanager.Model.CatchModel
import com.andrewchang.moneymanager.Model.DataHelper
import com.andrewchang.moneymanager.Model.MonthModel
import com.andrewchang.moneymanager.Model.TidyModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.pdog.dimension.dp
import kotlinx.android.synthetic.main.activity_pie_pay.*
import kotlinx.android.synthetic.main.color_text_under_pie_type.view.*
import kotlinx.android.synthetic.main.list_pie.view.*
import kotlinx.android.synthetic.main.month_text.view.*

var pieFinalData : ArrayList<CatchModel> = ArrayList()
var gridAdapterForDate: PiePayActivity.GridAdapterForMonth? = null

class PiePayActivity : Activity() {
    var recyclerAdapter: RecyclerAdapter? = null
    var getMonth = ""
    var getYear = ""
    var selectExsist = true
    var entries = ArrayList<PieEntry>()
    var arrayTidy: ArrayList<TidyModel> = ArrayList()
    var gridAdapter: GridAdapter? = null
    var now = DataHelper().getNowTime()
    var pieRecordYear = now.split("-")[0]
    var pieRecordMonth = now.split("-")[1]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_pay)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        pie_arrow_down.visibility = View.VISIBLE
        pie_arrow_up.visibility = View.INVISIBLE
        pie_mask.visibility = View.GONE
        pie_custom_year.visibility = View.GONE

        pieList_rv.layoutManager = LinearLayoutManager(this)
        var layoutParams = RelativeLayout.LayoutParams(
            (metrics.widthPixels / 100 * 99.0).toInt(),
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParams.addRule(RelativeLayout.BELOW, pie_layout.id)
        pieList_rv.layoutParams = layoutParams

        var colorTextLayout: RelativeLayout = findViewById(R.id.color_text_layout)
        layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            (metrics.heightPixels / 100 * 3.0).toInt()
        )
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, pie_layout.id)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        colorTextLayout.layoutParams = layoutParams

        go_back_botton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        var isPayOrSallary = ""
        getMonth = this.intent.getStringExtra("month")
        pieRecordMonth = this.intent.getStringExtra("month")
        getYear = this.intent.getStringExtra("year")
        pieRecordYear = this.intent.getStringExtra("year")
        selectExsist = this.intent.getBooleanExtra("selectExsist", true)
        var getTopTime = this.intent.getStringExtra("topTitle")
        if (this.intent.getBooleanExtra("isPay", true) == true) {
            time_title_in_pie.setText(getTopTime + "(支出)")
            isPayOrSallary = "(支出)"
        } else {
            time_title_in_pie.setText(getTopTime + "(收入)")
            isPayOrSallary = "(支出)"

        }
        time_title_in_pie.textSize = metrics.widthPixels / 1000 * 20.toFloat()

        var mLastClickTime: Long = 0
        pie_mask.setOnClickListener {
            var nowTime = System.currentTimeMillis()
            if (nowTime - mLastClickTime > 500L) {
                animationToChangeVisibility(pie_custom_year)
                pie_arrow_up.visibility = View.GONE
                pie_arrow_down.visibility = View.VISIBLE
                maskAlphaAnimation(pie_mask)
                pie_mask.visibility = View.GONE
                mLastClickTime = nowTime
            }
        }

        var catchYearText = 0
        var catchYearText2 = 0
        var yearBox: RelativeLayout = findViewById(R.id.pie_year_box)
        var lastYear: RelativeLayout = pie_left_year_botton
        var nextYear: RelativeLayout = pie_right_year_botton
        var nowYearText = now.split("-")

        pie_year_text.text = nowYearText[0]

        lastYear.setOnClickListener {
            yearBox.background = null
            if ((pie_year_text.text.toString().toInt() - 1).toString() == pieRecordYear && selectMonthAlreadyExsist == false) {
                yearBox.setBackgroundResource(R.drawable.select_type)
            }
            catchYearText = pie_year_text.text.toString().toInt()
            catchYearText2 = catchYearText - 1
            pie_year_text.text = catchYearText2.toString()
            for (value in gridAdapterForDate!!.monthArray) {
                value.click = false
            }
            if (pie_year_text.text.toString() == pieRecordYear && selectMonthAlreadyExsist == true) {
                for (value in gridAdapterForDate!!.monthArray) {
                    if (ChineseDateToNumber(value.month) == pieRecordMonth) {
                        value.click = true
                    }
                }
            }
            gridAdapterForDate!!.notifyDataSetChanged()
        }
        nextYear.setOnClickListener {
            yearBox.background = null
            if ((pie_year_text.text.toString().toInt() + 1).toString() == pieRecordYear && selectMonthAlreadyExsist == false) {
                yearBox.setBackgroundResource(R.drawable.select_type)
            }
            catchYearText = pie_year_text.text.toString().toInt()
            catchYearText2 = catchYearText + 1
            pie_year_text.text = catchYearText2.toString()
            for (value in gridAdapterForDate!!.monthArray) {
                value.click = false
            }
            if (pieRecordYear == pie_year_text.text && selectMonthAlreadyExsist == true) {
                for (value in gridAdapterForDate!!.monthArray) {
                    if (ChineseDateToNumber(value.month) == pieRecordMonth) {
                        value.click = true
                    }
                }
            }
            gridAdapterForDate!!.notifyDataSetChanged()

        }

        yearBox.setOnClickListener {
            yearBox.setBackgroundResource(R.drawable.select_type)
            animationToChangeVisibility(pie_custom_year)
            pie_arrow_up.visibility = View.GONE
            pie_arrow_down.visibility = View.VISIBLE
            maskAlphaAnimation(pie_mask)
            pie_mask.visibility = View.GONE

            for (value in gridAdapterForDate!!.monthArray) {
                value.click = false
            }
            gridAdapterForDate!!.notifyDataSetChanged()
            selectMonthAlreadyExsist = false
            pieRecordYear = pie_year_text.text.toString()
            pieFinalData = DataHelper().getSingleYearData(this, pieRecordYear)
            updatePieRecyclerAdapter(
                pieFinalData,
                this.intent.getBooleanExtra("isPay", true)
            )
            if (now.split("-")[0] == pieRecordYear) {
                time_title_in_pie.text = "今年" + isPayOrSallary
            } else {
                time_title_in_pie.text = pieRecordYear + "年" + isPayOrSallary
            }

            var totalPayPrice = 0
            var totalSallaryPrice = 0
            for (value in finalData) {
                for (data in value) {
                    if (data.isPay == true) {
                        totalPayPrice += data.price.toInt()
                    } else {
                        totalSallaryPrice += data.price.toInt()
                    }
                }
            }
        }

        var monthGrid: GridView = findViewById<GridView>(R.id.pie_month_grid)
        monthGrid.setOnItemClickListener { adapterView, view, i, l ->
            selectMonthAlreadyExsist = true
            yearBox.background = null
            addBtn?.visibility = View.VISIBLE
            for (value in gridAdapterForDate!!.monthArray) {
                value.click = false
            }
            gridAdapterForDate!!.monthArray[i].click = true
            pieRecordYear = pie_year_text.text.toString()
            for (value in gridAdapterForDate!!.monthArray) {
                if (value.click == true) {
                    pieRecordMonth = ChineseDateToNumber(value.month)
                }
            }
            animationToChangeVisibility(pie_custom_year)
            pie_arrow_up.visibility = View.GONE
            pie_arrow_down.visibility = View.VISIBLE
            maskAlphaAnimation(pie_mask)
            pie_mask.visibility = View.GONE
            if (now.split("-")[0] == pieRecordYear) {
                time_title_in_pie.text = "今年" + NumberToChineseDate(pieRecordMonth) + isPayOrSallary
            } else {
                time_title_in_pie.text =
                    pieRecordYear + "年" + NumberToChineseDate(pieRecordMonth) + isPayOrSallary
            }

            gridAdapterForDate!!.notifyDataSetChanged()

            pieFinalData = DataHelper().getSingleMonthData(this, pieRecordYear, pieRecordMonth)

            updatePieRecyclerAdapter(
                pieFinalData,
                this.intent.getBooleanExtra("isPay", true)
            )

            var totalPayPrice = 0
            var totaiSallaryPrice = 0
            for (value in finalData) {
                for (data in value) {
                    if (data.isPay == true) {
                        totalPayPrice += data.price.toInt()
                    } else {
                        totaiSallaryPrice += data.price.toInt()
                    }
                }
            }

            recyclerAdapter?.notifyDataSetChanged()
        }



        val TIME_INTERVAL = 400L
        pie_open_date_button.setOnClickListener {
            var nowTime = System.currentTimeMillis()
            if (nowTime - mLastClickTime > TIME_INTERVAL) {
                animationToChangeVisibility(pie_custom_year)
                if (pie_arrow_down.visibility == View.VISIBLE) {
                    pie_arrow_up.visibility = View.VISIBLE
                    pie_arrow_down.visibility = View.GONE
                    maskAlphaAnimation(pie_mask)
                    pie_mask.visibility = View.VISIBLE

                } else {
                    pie_arrow_up.visibility = View.GONE
                    pie_arrow_down.visibility = View.VISIBLE
                    maskAlphaAnimation(pie_mask)
                    pie_mask.visibility = View.GONE

                }
                mLastClickTime = nowTime
            }
        }

        if (selectExsist == true) {
            recyclerAdapter = RecyclerAdapter(
                tidyData(
                    DataHelper().getSingleMonthData(this, getYear, getMonth),
                    this.intent.getBooleanExtra("isPay", true)
                )
            )
        } else if (selectExsist == false) {
            recyclerAdapter = RecyclerAdapter(
                tidyData(
                    DataHelper().getSingleYearData(this, getYear),
                    this.intent.getBooleanExtra("isPay", true)
                )
            )
        }
        pieList_rv.adapter = recyclerAdapter

        var monthText: ArrayList<MonthModel> = ArrayList()
        monthText.add(MonthModel("一月", false))
        monthText.add(MonthModel("二月", false))
        monthText.add(MonthModel("三月", false))
        monthText.add(MonthModel("四月", false))
        monthText.add(MonthModel("五月", false))
        monthText.add(MonthModel("六月", false))
        monthText.add(MonthModel("七月", false))
        monthText.add(MonthModel("八月", false))
        monthText.add(MonthModel("九月", false))
        monthText.add(MonthModel("十月", false))
        monthText.add(MonthModel("十一月", false))
        monthText.add(MonthModel("十二月", false))

        gridAdapterForDate = GridAdapterForMonth(this, monthText)
        pie_month_grid.adapter = gridAdapterForDate
        pie_month_grid.numColumns = 6
        tidyInfo(5, arrayTidy)

        var colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(this, R.color.red))
        colors.add(ContextCompat.getColor(this, R.color.orange))
        colors.add(ContextCompat.getColor(this, R.color.yellow))
        colors.add(ContextCompat.getColor(this, R.color.green))
        colors.add(ContextCompat.getColor(this, R.color.blue))

        pieChart.getDescription().setEnabled(false);


        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(colors)
        dataSet.valueTextColor = Color.TRANSPARENT

        val pieData = PieData(dataSet)
        pieData.setDrawValues(true)

        pieChart.setData(pieData)
        pieChart.invalidate()

        var colorTextUnderPie: GridView = findViewById(R.id.color_text_grid)
        gridAdapter = GridAdapter(this, tidyInfoOfColorText(arrayTidy))
        colorTextUnderPie.adapter = gridAdapter
        colorTextUnderPie.numColumns = 5

//        將對應的資料欄位指向對應的ViewHolder (ex: type資料欄指向color_text_type9)
        if (selectExsist) {
            if (pieRecordMonth == "01") {
                gridAdapterForDate!!.monthArray[0].click = true
            } else if (pieRecordMonth == "02") {
                gridAdapterForDate!!.monthArray[1].click = true
            } else if (pieRecordMonth == "03") {
                gridAdapterForDate!!.monthArray[2].click = true
            } else if (pieRecordMonth == "04") {
                gridAdapterForDate!!.monthArray[3].click = true
            } else if (pieRecordMonth == "05") {
                gridAdapterForDate!!.monthArray[4].click = true
            } else if (pieRecordMonth == "06") {
                gridAdapterForDate!!.monthArray[5].click = true
            } else if (pieRecordMonth == "07") {
                gridAdapterForDate!!.monthArray[6].click = true
            } else if (pieRecordMonth == "08") {
                gridAdapterForDate!!.monthArray[7].click = true
            } else if (pieRecordMonth == "09") {
                gridAdapterForDate!!.monthArray[8].click = true
            } else if (pieRecordMonth == "10") {
                gridAdapterForDate!!.monthArray[9].click = true
            } else if (pieRecordMonth == "11") {
                gridAdapterForDate!!.monthArray[10].click = true
            } else if (pieRecordMonth == "12") {
                gridAdapterForDate!!.monthArray[11].click = true
            }
        }else{
            yearBox.setBackgroundResource(R.drawable.select_type)
        }
    }

    fun tidyData(array: ArrayList<CatchModel>, isPay: Boolean): ArrayList<TidyModel> {
        arrayTidy.clear()
        var deleteArray: ArrayList<CatchModel> = ArrayList()
        for (value in array) {
            deleteArray.add(value)
        }
        while (deleteArray.size > 0) {
            for (value in array) {
                if (arrayTidy.any { t -> t.type == value.type } == false && isPay == value.isPay) {
                    arrayTidy.add(TidyModel(value.type, value.price.toInt()))
                    deleteArray.remove(value)
                } else if (arrayTidy.any { t -> t.type == value.type } == true && isPay == value.isPay) {
                    for (valueTidy in arrayTidy) {
                        if (valueTidy.type == value.type) {
                            valueTidy.price += value.price.toInt()
                            deleteArray.remove(value)
                        }
                    }
                } else if (isPay != value.isPay) {
                    deleteArray.remove(value)
                }
            }
        }
        array.clear()
        for (value in deleteArray) {
            array.add(value)
        }
        deleteArray.clear()
        return arrayTidy
    }

    class RecyclerAdapter(private var data: ArrayList<TidyModel>) :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        var dataHelper = DataHelper()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.list_pie, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            dataHelper.addAllInfo(3)

            var layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            if (data.size >= 2) {
                holder.itemCell.setBackgroundResource(R.drawable.middle_layout_type)
                if (position == 0) {
                    holder.itemCell.setBackgroundResource(R.drawable.top_layout_type)
                    layoutParams.setMargins(0, 11.dp, 0, 0)
                    holder.itemCell.layoutParams = layoutParams
                }
                holder.itemCell.setPieHolder(data[position])
                if (position == data.size - 1) {
                    holder.itemCell.setBackgroundResource(R.drawable.under_layout_type)
                }
            } else {
                holder.itemCell.setBackgroundResource(R.drawable.layout_type)
                layoutParams.setMargins(0, 11.dp, 0, 0)
                holder.itemCell.layoutParams = layoutParams
                holder.itemCell.setPieHolder(data[position])

            }
        }

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var itemCell = view.pie_content
        }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    class GridAdapter : BaseAdapter {
        var context: Context? = null
        var array: ArrayList<TidyModel> = ArrayList()

        constructor(context: Context, array: ArrayList<TidyModel>) : super() {
            this.context = context
            this.array = array
        }

        override fun getCount(): Int {
            return array.size
        }

        override fun getItem(position: Int): Any {
            return array[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var colorTextType = array[position]
            var colorArray: ArrayList<Int> = ArrayList()
            colorArray.add(ContextCompat.getColor(context!!, R.color.red))
            colorArray.add(ContextCompat.getColor(context!!, R.color.orange))
            colorArray.add(ContextCompat.getColor(context!!, R.color.yellow))
            colorArray.add(ContextCompat.getColor(context!!, R.color.green))
            colorArray.add(ContextCompat.getColor(context!!, R.color.blue))
            var inflator =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var addView = inflator.inflate(R.layout.color_text_under_pie_type, null)
            addView.color_text_type.text = colorTextType.type
            addView.color_box.setBackgroundColor(colorArray[position])
            return addView
        }
    }

    class GridAdapterForMonth : BaseAdapter {
        var monthArray: ArrayList<MonthModel> = ArrayList()
        var context: Context? = null

        constructor(context: Context, array: ArrayList<MonthModel>) : super() {
            this.context = context
            this.monthArray = array
        }

        override fun getCount(): Int {
            return monthArray.size
        }

        override fun getItem(position: Int): Any {
            return monthArray[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var inflator =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var addView = inflator.inflate(R.layout.month_text, null)
            addView.month_grid_text.text = monthArray[position].month
            if (monthArray[position].click == true) {
                addView.month_grid_text.setBackgroundResource(R.drawable.select_type)
            } else {
                addView.month_grid_text.setBackgroundResource(0)
            }
            return addView
        }
    }

    //併入其他，整理圖表
    fun tidyInfo(howMuch: Int, info: ArrayList<TidyModel>): ArrayList<PieEntry> {
        var tidyEntries = ArrayList<TidyModel>()
        var tidyOthers = ArrayList<TidyModel>()

        var b = 0
        info.sortByDescending { it.price }
        if (info.size <= howMuch) {
            for (value in info) {
                if (getPercent(value.price) < 0.1) {
                    entries.add(PieEntry(getPercent(value.price), ""))
                } else {
                    entries.add(PieEntry(getPercent(value.price), value.type))
                }
            }

        } else {
            for (i in 0..(howMuch - 2)) {
                if (getPercent(info[i].price) > 0.1) {
                    tidyEntries.add(info[i])
                } else {
                    tidyEntries.add(TidyModel("", info[i].price))
                }
            }

            for (i in (howMuch - 1)..(info.size - 1)) {
                tidyOthers.add(info[i])
            }
            for (value in tidyEntries) {
                entries.add(PieEntry(getPercent(value.price), value.type))
            }
            for (value in tidyOthers) {
                b += value.price
            }
            entries.add(PieEntry(getPercent(b), "其他"))

        }

        return entries

    }

    fun tidyInfoOfColorText(info: ArrayList<TidyModel>): ArrayList<TidyModel> {
        var tidyEntries = ArrayList<TidyModel>()
        var b = 0
        info.sortByDescending { it.price }
        if (info.size <= 5) {
            for (value in info) {
                tidyEntries.add(value)
            }
        } else {
            for (i in 0..3) {
                tidyEntries.add(info[i])
            }
            for (i in 4..info.size - 1) {
                b += info[i].price
            }
            tidyEntries.add(TidyModel("其他", b))
        }
        return tidyEntries
    }

    fun getPercent(howMuch: Int): Float {
        var a = 0
        for (value in arrayTidy) {
            a += value.price
        }
        var lastTotal = howMuch.toFloat() / a.toFloat()
        return lastTotal
    }

    fun maskAlphaAnimation(view: View) {
        if (view.visibility == View.GONE) {
            val alphaAnimation = AlphaAnimation(0f, 1f)
            alphaAnimation.duration = 300
            view.startAnimation(alphaAnimation)
        } else {
            val alphaAnimation = AlphaAnimation(1f, 0f)
            alphaAnimation.duration = 300
            view.startAnimation(alphaAnimation)
        }
    }

    fun animationToChangeVisibility(view: View) {

        if (view.visibility == View.GONE) {
            view.visibility = View.INVISIBLE
            view.post(Runnable {
                val animator =
                    ObjectAnimator.ofFloat(view, "translationY", -view.height.toFloat(), 0.0f)
                animator.duration = 300
                animator.start()
                view.visibility = View.VISIBLE
            })


        } else {

            view.post(Runnable {
                val animator =
                    ObjectAnimator.ofFloat(view, "translationY", 0.0f, -view.height.toFloat())
                animator.duration = 300
                animator.start()
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        view.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
            })

        }
    }


    fun ChineseDateToNumber(month: String): String {
        var numberMonth = ""
        if (month == "一月") {
            numberMonth = "01"
        } else if (month == "二月") {
            numberMonth = "02"
        } else if (month == "三月") {
            numberMonth = "03"
        } else if (month == "四月") {
            numberMonth = "04"
        } else if (month == "五月") {
            numberMonth = "05"
        } else if (month == "六月") {
            numberMonth = "06"
        } else if (month == "七月") {
            numberMonth = "07"
        } else if (month == "八月") {
            numberMonth = "08"
        } else if (month == "九月") {
            numberMonth = "09"
        } else if (month == "十月") {
            numberMonth = "10"
        } else if (month == "十一月") {
            numberMonth = "11"
        } else if (month == "十二月") {
            numberMonth = "12"
        }
        return numberMonth
    }

    fun NumberToChineseDate(month: String): String {
        var ChineseMonth = ""
        if (month == "01") {
            ChineseMonth = "1月"
        } else if (month == "02") {
            ChineseMonth = "2月"
        } else if (month == "03") {
            ChineseMonth = "3月"
        } else if (month == "04") {
            ChineseMonth = "4月"
        } else if (month == "05") {
            ChineseMonth = "5月"
        } else if (month == "06") {
            ChineseMonth = "6月"
        } else if (month == "07") {
            ChineseMonth = "7月"
        } else if (month == "08") {
            ChineseMonth = "8月"
        } else if (month == "09") {
            ChineseMonth = "9月"
        } else if (month == "10") {
            ChineseMonth = "10月"
        } else if (month == "11") {
            ChineseMonth = "11月"
        } else if (month == "12") {
            ChineseMonth = "12月"
        }
        return ChineseMonth
    }

    fun updatePieRecyclerAdapter(data: ArrayList<CatchModel>, isPay: Boolean) {

        var payText: String
        for (value in gridAdapterForDate!!.monthArray) {
            if (value.click == true) {
                pieRecordMonth = ChineseDateToNumber(value.month)
            }
            if (isPay) {
                payText = "(支出)"
            } else {
                payText = "(收入)"
            }
            if (pieRecordYear == now.split("-")[0]) {
                if (pieRecordMonth == "10" || pieRecordMonth == "11" || pieRecordMonth == "12") {
                    time_title_in_pie?.text = "今年" + pieRecordMonth + "月" + payText
                } else {
                    var splitRecordTime = pieRecordMonth.split("0")
                    time_title_in_pie?.text = "今年" + splitRecordTime[1] + "月" + payText
                }
            } else {
                if (pieRecordMonth == "10" || pieRecordMonth == "11" || pieRecordMonth == "12") {
                    time_title_in_pie?.text = pieRecordYear + "年" + pieRecordMonth + "月" + payText
                } else {
                    var splitRecordTime = pieRecordMonth.split("0")
                    time_title_in_pie?.text = pieRecordYear + "年" + splitRecordTime[1] + "月" + payText
                }
            }
        }
        recyclerAdapter = RecyclerAdapter(
            tidyData(
                data, isPay
            )
        )

        gridAdapter = GridAdapter(this, tidyInfoOfColorText(arrayTidy))
        color_text_grid.adapter = gridAdapter

        gridAdapter?.notifyDataSetChanged()

        var colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(this, R.color.red))
        colors.add(ContextCompat.getColor(this, R.color.orange))
        colors.add(ContextCompat.getColor(this, R.color.yellow))
        colors.add(ContextCompat.getColor(this, R.color.green))
        colors.add(ContextCompat.getColor(this, R.color.blue))
        pieList_rv.adapter = recyclerAdapter

        entries.clear()
        tidyInfo(5, arrayTidy)
        val dataSet = PieDataSet(entries, "")
        dataSet.valueTextColor = Color.TRANSPARENT

        val pieData = PieData(dataSet)
        pieData.setDrawValues(true)
        dataSet.setColors(colors)
        pieChart.setData(pieData)
        pieChart.invalidate()
    }


}
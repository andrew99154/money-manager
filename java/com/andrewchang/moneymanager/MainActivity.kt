package com.andrewchang.moneymanager

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrewchang.moneymanager.Model.CatchModel
import com.andrewchang.moneymanager.Model.DataHelper
import com.andrewchang.moneymanager.Model.MonthModel
import com.andrewchang.moneymanager.Model.TypeModel
import com.pdog.dimension.dp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_delete_activity.view.*
import kotlinx.android.synthetic.main.list_activity.view.*
import kotlinx.android.synthetic.main.month_text.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


var selectMonthAlreadyExsist = true
var finalData: ArrayList<ArrayList<CatchModel>> = ArrayList()
var gridAdaptor: MainActivity.GridAdapter? = null
var recyclerAdapter: MainActivity.RecyclerAdapter? = null
var yearText: TextView? = null
var topMonthText: TextView? = null
var isGone: Boolean = true
var addBtn: TextView? = null
val metrics = DisplayMetrics()


class MainActivity : Activity() {
    var dataHelper = DataHelper()
    var now = dataHelper.getNowTime()
    var splitNow = now.split("-")
    var recordYear = splitNow[0]
    var recordMonth = splitNow[1]


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var recycleView = findViewById<RecyclerView>(R.id.list_rv)
        custom_year.visibility = View.GONE
        arrow_up.visibility = View.GONE
        mask.visibility = View.GONE


        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    btn_add.visibility = View.VISIBLE
                    val scaleAnimation = ScaleAnimation(
                        0f, 1f, 0f, 1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5F,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5F
                    )
                    scaleAnimation.duration = 100
                    btn_add.startAnimation(scaleAnimation)
                } else {
                    val scaleAnimation = ScaleAnimation(
                        1f, 0f, 1f, 0f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5F,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5F
                    )
                    scaleAnimation.duration = 100
                    btn_add.startAnimation(scaleAnimation)
                    btn_add.visibility = View.GONE
                }
            }
        })


        windowManager.defaultDisplay.getMetrics(metrics)
        var allTotalLayout: RelativeLayout = findViewById(R.id.alltotal_layout)
        var layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            95.dp
        )
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParams.setMargins(0, metrics.heightPixels / 1000 * 30, 0, 0)
        layoutParams.addRule(RelativeLayout.BELOW, main_header.id)
        allTotalLayout.layoutParams = layoutParams


        var list: RecyclerView = findViewById(R.id.list_rv)
        layoutParams = RelativeLayout.LayoutParams(
            (metrics.widthPixels / 100 * 99.0).toInt(),
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParams.addRule(RelativeLayout.BELOW, alltotal_layout.id)
        list.layoutParams = layoutParams


        var payLayout: RelativeLayout = findViewById(R.id.pay_layout)
        layoutParams = RelativeLayout.LayoutParams(
            (metrics.widthPixels / 100 * 33.0).toInt(),
            63.dp
        )
        layoutParams.addRule(RelativeLayout.RIGHT_OF, line_1.id)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        payLayout.layoutParams = layoutParams

        num_pay.setEllipsize(TextUtils.TruncateAt.END)
        num_pay.setSingleLine(true);

        var line1: TextView = findViewById(R.id.line_1)
        layoutParams = RelativeLayout.LayoutParams(
            1.dp,
            33.dp
        )
        layoutParams.addRule(RelativeLayout.RIGHT_OF, sallary_layout.id)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        line1.layoutParams = layoutParams

        var sallaryLayout: RelativeLayout = findViewById(R.id.sallary_layout)
        layoutParams = RelativeLayout.LayoutParams(
            (metrics.widthPixels / 100 * 33.0).toInt(),
            63.dp
        )
        layoutParams.setMargins(0, 16.dp, 0, 0)
        sallaryLayout.layoutParams = layoutParams

        numSallary.setEllipsize(TextUtils.TruncateAt.END)
        numSallary.setSingleLine(true);

        var line2: TextView = findViewById(R.id.line_2)
        layoutParams = RelativeLayout.LayoutParams(
            1.dp,
            33.dp
        )
        layoutParams.addRule(RelativeLayout.RIGHT_OF, pay_layout.id)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        line2.layoutParams = layoutParams

        var totalLayout: RelativeLayout = findViewById(R.id.total_layout)
        layoutParams = RelativeLayout.LayoutParams(
            (metrics.widthPixels / 100 * 33.0).toInt(),
            63.dp
        )
        layoutParams.addRule(RelativeLayout.RIGHT_OF, line_2.id)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        totalLayout.layoutParams = layoutParams
        numTotal.setEllipsize(TextUtils.TruncateAt.END)
        numTotal.setSingleLine(true);

        top_month_text.textSize = 6.dp.toFloat()


        var mLastClickTime: Long = 0
        val TIME_INTERVAL = 400L
        addBtn = findViewById<TextView>(R.id.btn_add)
        addBtn?.setOnClickListener {
            var nowTime = System.currentTimeMillis()
            if (nowTime - mLastClickTime > 1000L) {
                var intent = Intent(this, AddActivity::class.java)
                intent.putExtra("topTitle", top_month_text.text)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                mLastClickTime = nowTime
            }
        }

        list_rv.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter(ArrayList(), this)
        recycleView.adapter = recyclerAdapter


        pay_layout.setOnClickListener {
            if (num_pay.text.toString() != "0") {
                var intent = Intent(this, PiePayActivity::class.java)
                intent.putExtra("isPay", true)
                intent.putExtra("month", recordMonth)
                intent.putExtra("year", recordYear)
                intent.putExtra("topTitle", top_month_text.text)
                if (selectMonthAlreadyExsist == true) {
                    intent.putExtra("selectExsist", true)
                } else {
                    intent.putExtra("selectExsist", false)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                Toast.makeText(this, "你還沒新增過支出項目喔~", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
        sallaryLayout.setOnClickListener {
            if (numSallary.text.toString() != "0") {
                var intent = Intent(this, PiePayActivity::class.java)
                intent.putExtra("isPay", false)
                intent.putExtra("month", recordMonth)
                intent.putExtra("year", recordYear)
                intent.putExtra("topTitle", top_month_text.text)
                if (selectMonthAlreadyExsist == true) {
                    intent.putExtra("selectExsist", true)
                } else {
                    intent.putExtra("selectExsist", false)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                Toast.makeText(this, "你還沒新增過收入項目喔~", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }




        text_and_arrow.setOnClickListener {
            var nowTime = System.currentTimeMillis()
            if (nowTime - mLastClickTime > TIME_INTERVAL) {
                animationToChangeVisibility(custom_year)
                if (isGone) {
                    arrow_up.visibility = View.VISIBLE
                    arrow_down.visibility = View.GONE
                    maskAlphaAnimation(mask)
                    mask.visibility = View.VISIBLE
                    isGone = false
                } else {
                    arrow_up.visibility = View.GONE
                    arrow_down.visibility = View.VISIBLE
                    maskAlphaAnimation(mask)
                    mask.visibility = View.GONE
                    isGone = true
                }
                mLastClickTime = nowTime
            }

        }
        var nowYearText = now.split("-")
        var catchYearText = 0
        var catchYearText2 = 0
        var lastYear: RelativeLayout = left_year_botton
        var nextYear: RelativeLayout = right_year_botton
        yearText = findViewById(R.id.year_text)
        year_text.text = nowYearText[0]

        topMonthText = findViewById(R.id.top_month_text)
        if (splitNow[0] == recordYear) {
            if (recordMonth == "10" || recordMonth == "11" || recordMonth == "12") {
                topMonthText?.text = "今年" + recordMonth + "月"
            } else {
                var splitRecordTime = recordMonth.split("0")
                topMonthText?.text = "今年" + splitRecordTime[1] + "月"
            }

        } else {
            if (recordMonth == "10" || recordMonth == "11" || recordMonth == "12") {
                topMonthText?.text = recordYear + "年" + recordMonth + "月"
            } else {
                var splitRecordTime = recordMonth.split("0")
                topMonthText?.text = recordYear + "年" + splitRecordTime[1] + "月"
            }
        }
        mask.setOnClickListener {
            var nowTime = System.currentTimeMillis()
            if (nowTime - mLastClickTime > 500L) {
                animationToChangeVisibility(custom_year)
                arrow_up.visibility = View.GONE
                arrow_down.visibility = View.VISIBLE
                maskAlphaAnimation(mask)
                mask.visibility = View.GONE
                isGone = true
                mLastClickTime = nowTime
            }
        }

        var yearBox: RelativeLayout = findViewById(R.id.year_box)
        lastYear.setOnClickListener {
            yearBox.background = null
            if ((year_text.text.toString().toInt() - 1).toString() == recordYear && selectMonthAlreadyExsist == false) {
                yearBox.setBackgroundResource(R.drawable.select_type)
            }
            catchYearText = year_text.text.toString().toInt()
            catchYearText2 = catchYearText - 1
            year_text.text = catchYearText2.toString()
            for (value in gridAdaptor!!.monthArray) {
                value.click = false
            }
            if (year_text.text.toString() == recordYear && selectMonthAlreadyExsist == true) {
                for (value in gridAdaptor!!.monthArray) {
                    if (ChineseDateToNumber(value.month) == recordMonth) {
                        value.click = true
                    }
                }
            }
            gridAdaptor!!.notifyDataSetChanged()
        }
        nextYear.setOnClickListener {
            yearBox.background = null
            if ((year_text.text.toString().toInt() + 1).toString() == recordYear && selectMonthAlreadyExsist == false) {
                yearBox.setBackgroundResource(R.drawable.select_type)
            }
            catchYearText = year_text.text.toString().toInt()
            catchYearText2 = catchYearText + 1
            year_text.text = catchYearText2.toString()
            for (value in gridAdaptor!!.monthArray) {
                value.click = false
            }
            if (recordYear == year_text.text && selectMonthAlreadyExsist == true) {
                for (value in gridAdaptor!!.monthArray) {
                    if (ChineseDateToNumber(value.month) == recordMonth) {
                        value.click = true
                    }
                }
            }
            gridAdaptor!!.notifyDataSetChanged()

        }


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


        yearBox.setOnClickListener {
            yearBox.setBackgroundResource(R.drawable.select_type)
            finalData.clear()
            animationToChangeVisibility(custom_year)
            arrow_up.visibility = View.GONE
            arrow_down.visibility = View.VISIBLE
            maskAlphaAnimation(mask)
            mask.visibility = View.GONE

            isGone = true

            for (value in gridAdaptor!!.monthArray) {
                value.click = false
            }
            gridAdaptor!!.notifyDataSetChanged()
            selectMonthAlreadyExsist = false
            recordYear = year_text.text.toString()

            if (splitNow[0] == recordYear) {
                top_month_text.text = "今年"
            } else {
                top_month_text.text = recordYear + "年"
            }
            updateFinalData(dataHelper.getLocalStorageData(this), false, this)
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
            num_pay.text = (-totalPayPrice).toString()
            numSallary.text = totalSallaryPrice.toString()
            var howMuchPay = num_pay.text.toString().toInt()
            var howMuchSallary = numSallary.text.toString().toInt()
            numTotal.text = (howMuchPay + howMuchSallary).toString()
            recyclerAdapter?.notifyDataSetChanged()
        }
        var monthGrid: GridView = findViewById<GridView>(R.id.month_grid)
        monthGrid.setOnItemClickListener { adapterView, view, i, l ->
            selectMonthAlreadyExsist = true
            yearBox.background = null
            addBtn?.visibility = View.VISIBLE
            for (value in gridAdaptor!!.monthArray) {
                value.click = false
            }
            gridAdaptor!!.monthArray[i].click = true
            animationToChangeVisibility(custom_year)
            arrow_up.visibility = View.GONE
            arrow_down.visibility = View.VISIBLE
            maskAlphaAnimation(mask)
            mask.visibility = View.GONE
            isGone = true

            gridAdaptor!!.notifyDataSetChanged()

            updateFinalData(dataHelper.getLocalStorageData(this), true, this)

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
            num_pay.text = (-totalPayPrice).toString()
            numSallary.text = totaiSallaryPrice.toString()
            var howMuchPay = num_pay.text.toString().toInt()
            var howMuchSallary = numSallary.text.toString().toInt()
            numTotal.text = (howMuchPay + howMuchSallary).toString()
            recyclerAdapter?.notifyDataSetChanged()
        }
        gridAdaptor = GridAdapter(this@MainActivity, monthText)
        month_grid.adapter = gridAdaptor
        month_grid.numColumns = 6

        if (recordMonth == "01") {
            gridAdaptor!!.monthArray[0].click = true
        } else if (recordMonth == "02") {
            gridAdaptor!!.monthArray[1].click = true
        } else if (recordMonth == "03") {
            gridAdaptor!!.monthArray[2].click = true
        } else if (recordMonth == "04") {
            gridAdaptor!!.monthArray[3].click = true
        } else if (recordMonth == "05") {
            gridAdaptor!!.monthArray[4].click = true
        } else if (recordMonth == "06") {
            gridAdaptor!!.monthArray[5].click = true
        } else if (recordMonth == "07") {
            gridAdaptor!!.monthArray[6].click = true
        } else if (recordMonth == "08") {
            gridAdaptor!!.monthArray[7].click = true
        } else if (recordMonth == "09") {
            gridAdaptor!!.monthArray[8].click = true
        } else if (recordMonth == "10") {
            gridAdaptor!!.monthArray[9].click = true
        } else if (recordMonth == "11") {
            gridAdaptor!!.monthArray[10].click = true
        } else if (recordMonth == "12") {
            gridAdaptor!!.monthArray[11].click = true
        }

//        var screenWidth = metrics.widthPixels
//        var screenHeight = metrics.heightPixels
//        var dm = getResources().getDisplayMetrics().densityDpi
//        // 屏幕密度（每寸像素：120(ldpi)/160(mdpi)/213(tvdpi)/240(hdpi)/320(xhdpi)） 

    }


    class GridAdapter : BaseAdapter {
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

    //刷新頁面
    override fun onResume() {
        super.onResume()
        if (recyclerAdapter != null) {
            updateFinalData(dataHelper.getLocalStorageData(this), selectMonthAlreadyExsist, this)
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
            num_pay.text = (-totalPayPrice).toString()
            numSallary.text = totaiSallaryPrice.toString()
            var howMuchPay = num_pay.text.toString().toInt()
            var howMuchSallary = numSallary.text.toString().toInt()
            numTotal.text = (howMuchPay + howMuchSallary).toString()
            recyclerAdapter!!.notifyDataSetChanged()
        }
    }

    var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isGone == true) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    val toast = Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    toast.show()
                    mExitTime = System.currentTimeMillis();
                } else {
                    System.exit(0)
                }
                return true
            } else {
                animationToChangeVisibility(custom_year)
                arrow_up.visibility = View.GONE
                arrow_down.visibility = View.VISIBLE
                maskAlphaAnimation(mask)
                mask.visibility = View.GONE
                isGone = true
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    //RecycleView 設置
    class RecyclerAdapter(private var data: ArrayList<TypeModel>, private var context: Context) :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_activity, parent, false)
            return ViewHolder(view)
        }


        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (data[position].type == 0) {
                var now = getNow()
                var nowTheSame = now.split("-")
                var splitTime = data[position].catchModel?.chooseTimeWhenAdd?.split("-")
                if (data[position].catchModel?.chooseTimeWhenAdd == null) {
                    if (splitTime!![0] == nowTheSame[0]) {
                        if (splitTime[1] == "10" || splitTime[1] == "11" || splitTime[1] == "12") {
                            data[position].catchModel?.chooseTimeWhenAdd =
                                "今年" + splitTime[1] + "月" + splitTime[2] + "日"
                        } else {
                            var firstWordIsZeroMonth = splitTime[1].split("0")
                            data[position].catchModel?.chooseTimeWhenAdd =
                                "今年" + firstWordIsZeroMonth[1] + "月" + splitTime[2] + "日"
                        }
                    }
                }
            }
            holder.textCell.reset()
            holder.itemCell.reset()
            holder.textCell.visibility = View.GONE
            holder.itemCell.visibility = View.GONE
            if (data[position].catchModel != null) {
                if (data[position].type == 0) {
                    holder.textCell.visibility = View.VISIBLE
                    holder.textCell.setHolder(data[position].catchModel!!)
                    var layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    holder.listGroup.setBackgroundResource(R.drawable.top_layout_type)
                    layoutParams.setMargins(0, 11.dp, 0, 0)
                    holder.listGroup.layoutParams = layoutParams

                } else {
                    holder.itemCell.visibility = View.VISIBLE
                    holder.itemCell.setHolder(data[position].catchModel!!)
                    holder.listGroup.setBackgroundResource(R.drawable.middle_layout_type)
                    var layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    holder.listGroup.layoutParams = layoutParams
                    if (position + 1 < data.size && data[position + 1].type == 0 || position == data.size - 1) {
                        holder.listGroup.setBackgroundResource(R.drawable.under_layout_type)
                    }
                    if (position == data.size - 1) {
                        layoutParams.setMargins(0, 0, 0, 11.dp)
                    }
                }
            }
            if (data[position].type == 1) {
                holder.listGroup.setOnLongClickListener(OnClickListener(position, context, this))
            }
        }

        class OnClickListener : View.OnLongClickListener {
            var position: Int = -1
            var context: Context? = null
            var recyclerAdapter: RecyclerAdapter? = null

            constructor(position: Int, context: Context, recyclerAdapter: RecyclerAdapter) {
                this.position = position
                this.context = context
                this.recyclerAdapter = recyclerAdapter
            }

            override fun onLongClick(p0: View?): Boolean {
                var oldData = DataHelper().getLocalStorageData(context!!)
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_delete_activity, null)
                val mBuilder = AlertDialog.Builder(context!!)
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                var totalPayPrice = 0
                var totalSallaryPrice = 0
                mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mAlertDialog.window?.setLayout(300.dp, 150.dp)

                mDialogView.deleteDialogContinueBtn.setOnClickListener {
                    var deleteArray: ArrayList<CatchModel> = ArrayList()
                    for (value in oldData) {
                        if (value.recordDownTime == recyclerAdapter!!.data[position].catchModel!!.recordDownTime) {
                            deleteArray.add(value)
                        }
                    }
                    for (value in deleteArray) {
                        oldData.remove(value)
                    }
                    DataHelper().saveData(oldData, context!!)
//                    需要使recyclerAdapter重新setData, 用finalData的資料,先更新finalData後才能更新recyclerAdapter,不然會沒有反應。 recyclerAdapter?.setData(MainActivity().returnFinalData(oldData,
                    MainActivity().updateFinalData(oldData, selectMonthAlreadyExsist, context!!)
                    if (selectMonthAlreadyExsist == true) {
                        recyclerAdapter!!.setDataDayVision(finalData)
                    } else {
                        recyclerAdapter!!.setDataMonthVision(finalData)
                    }
                    recyclerAdapter!!.notifyDataSetChanged()

                    for (value in recyclerAdapter!!.data) {
                        if (value.catchModel?.isPay == true && value.type == 1) {
                            totalPayPrice += value.catchModel!!.price.toInt()
                        } else if (value.catchModel?.isPay == false && value.type == 1) {
                            totalSallaryPrice += value.catchModel!!.price.toInt()
                        }
                    }

                    (context!! as Activity).num_pay.text = (-totalPayPrice).toString()
                    (context!! as Activity).numSallary.text = totalSallaryPrice.toString()
                    (context!! as Activity).numTotal.text =
                        ((context!! as Activity).num_pay.text.toString().toInt() + (context!! as Activity).numSallary.text.toString().toInt()).toString()
                    mAlertDialog.dismiss()
                }
                mDialogView.deleteDialogCancelBtn.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                return true
            }
        }

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var listGroup = view.list_group
            var textCell = view.month
            var itemCell = view.content
        }

        fun setDataDayVision(data: ArrayList<ArrayList<CatchModel>>) {
            this.data.clear()
            for (value in data) {
                if (value != null) {
                    var typeModel = TypeModel(0, getSonTimeDayVision(value))
                    this.data.add(typeModel)
                    for (type in value) {
                        typeModel = TypeModel(1, type)
                        this.data.add(typeModel)
                    }
                }
            }
        }

        fun setDataMonthVision(data: ArrayList<ArrayList<CatchModel>>) {
            this.data.clear()
            for (value in data) {
                if (value != null) {
                    var typeModel = TypeModel(0, getSonTimeMonthVision(value))
                    this.data.add(typeModel)
                    for (type in value) {
                        typeModel = TypeModel(1, type)
                        this.data.add(typeModel)
                    }
                }
            }
        }


        fun getSonTimeDayVision(son: ArrayList<CatchModel>): CatchModel {
            var catchSonTime = son[0].chooseTimeWhenAdd.split("-")
            var sonTime = catchSonTime[0] + "-" + catchSonTime[1] + "-" + catchSonTime[2]
            var catchModel = CatchModel("", "", "", true, "", sonTime)
            return catchModel
        }

        fun getSonTimeMonthVision(son: ArrayList<CatchModel>): CatchModel {
            var catchSonTime = son[0].chooseTimeWhenAdd.split("-")
            var sonTime = catchSonTime[0] + "-" + catchSonTime[1]
            var catchModel = CatchModel("", "", "", true, "", sonTime)
            return catchModel
        }

        fun getNow(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
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

    fun getMonth(data: ArrayList<CatchModel>): ArrayList<CatchModel> {
        var find: Boolean
        var monthModel: ArrayList<CatchModel> = ArrayList()
        var catchMonthModel: CatchModel? = null
        var dataTime: List<String>
        var valueTime: List<String>
        var delimiter = "-"
        var delimiter2 = " "

        for (data in data) {
            find = false
            if (monthModel.isNullOrEmpty()) {
                monthModel.add(data)
            } else {
                for (value in monthModel) {
                    dataTime = (data.chooseTimeWhenAdd.split(delimiter, delimiter2))
                    valueTime = (value.chooseTimeWhenAdd.split(delimiter, delimiter2))
                    if (dataTime[0] == valueTime[0] && dataTime[1] == valueTime[1] && find == false) {
                        catchMonthModel = data
                        find = true
                    }
                }
                if (find == true && catchMonthModel != null) {
                    monthModel.add(catchMonthModel)
                }
            }
        }
        return monthModel
    }

    fun updateFinalData(
        defaultData: ArrayList<CatchModel>,
        isExsist: Boolean,
        context: Context
    ) {
        finalData.clear()
        var tidyData: ArrayList<CatchModel> = ArrayList()
        var alreadyAdd = false
        var dataTime: List<String>
        var dataTimeStr = ""
        var valueTime: List<String>
        var valueTimeStr = ""
        defaultData.sortByDescending { it.chooseTimeWhenAdd }
        if (isExsist == false) {
            while (defaultData.size > 0) {
                var month = getMonth(defaultData)
                for (value in defaultData) {
                    alreadyAdd = false
                    for (data in month) {
                        dataTime = data.chooseTimeWhenAdd.split("-")
                        dataTimeStr = dataTime[0] + "-" + dataTime[1]
                        valueTime = value.chooseTimeWhenAdd.split("-")
                        valueTimeStr = valueTime[0] + "-" + valueTime[1]
                        if (dataTimeStr != valueTimeStr && alreadyAdd == false) {
                            tidyData.add(value)
                            alreadyAdd = true
                        }
                    }
                }
                defaultData.clear()
                for (data in tidyData) {
                    defaultData.add(data)
                }
                tidyData.clear()
                var lastInfo: ArrayList<CatchModel> = ArrayList()
                for (value in month) {
                    if (value.chooseTimeWhenAdd.split("-")[0] == recordYear) {
                        lastInfo.add(value)
                    }
                }
                month.clear()
                for (value in lastInfo) {
                    month.add(value)
                }
                if (month.size != 0) {
                    finalData.add(month)
                }
            }
            recyclerAdapter?.setDataMonthVision(finalData)
        } else {
            fun getFirstDayInfo(data: ArrayList<CatchModel>): ArrayList<CatchModel> {
                var find: Boolean
                var dayModel: ArrayList<CatchModel> = ArrayList()
                var catchDayModel: CatchModel? = null
                var dataTime: List<String>
                var valueTime: List<String>

                for (data in data) {
                    find = false
                    if (dayModel.isNullOrEmpty()) {
                        dayModel.add(data)
                    } else {
                        for (value in dayModel) {
                            dataTime = (data.chooseTimeWhenAdd.split("-", " "))
                            valueTime = (value.chooseTimeWhenAdd.split("-", " "))
                            if (dataTime[0] == valueTime[0] && dataTime[1] == valueTime[1] && dataTime[2] == valueTime[2] && find == false) {
                                catchDayModel = data
                                find = true
                            }
                        }
                        if (find == true && catchDayModel != null) {
                            dayModel.add(catchDayModel)
                        }
                    }
                }
                return dayModel
            }
            for (value in gridAdaptor!!.monthArray) {
                if (value.click == true) {
                    recordMonth = ChineseDateToNumber(value.month)
                }
            }

            var text = yearText?.text.toString()
            recordYear = text
//            刪除時更新資料不會用到此行，只有選擇年月時會用到，找時間寫判斷式迴避
            finalData.clear()
            while (defaultData.size > 0) {
                var firstGroupDataOfDay = getFirstDayInfo(defaultData)
                for (value in defaultData) {
                    alreadyAdd = false
                    for (data in firstGroupDataOfDay) {
                        dataTime = data.chooseTimeWhenAdd.split("-")
                        dataTimeStr = dataTime[0] + "-" + dataTime[1] + "-" + dataTime[2]
                        valueTime = value.chooseTimeWhenAdd.split("-")
                        valueTimeStr = valueTime[0] + "-" + valueTime[1] + "-" + valueTime[2]
                        if (dataTimeStr != valueTimeStr && alreadyAdd == false) {
                            tidyData.add(value)
                            alreadyAdd = true
                        }
                    }
                }
                defaultData.clear()
                for (data in tidyData) {
                    defaultData.add(data)
                }
                tidyData.clear()
                var lastInfo: ArrayList<CatchModel> = ArrayList()
                for (value in firstGroupDataOfDay) {
                    if (value.chooseTimeWhenAdd.split("-")[0] + "-" + value.chooseTimeWhenAdd.split(
                            "-"
                        )[1] == recordYear + "-" + recordMonth
                    ) {
                        lastInfo.add(value)
                    }
                }
                firstGroupDataOfDay.clear()
                for (value in lastInfo) {
                    firstGroupDataOfDay.add(value)
                }
                if (firstGroupDataOfDay.size != 0) {
                    finalData.add(firstGroupDataOfDay)
                }
            }
            recyclerAdapter!!.setDataDayVision(finalData)


            if (splitNow[0] == recordYear) {
                if (recordMonth == "10" || recordMonth == "11" || recordMonth == "12") {
                    topMonthText?.text = "今年" + recordMonth + "月"
                } else {
                    var splitRecordTime = recordMonth.split("0")
                    topMonthText?.text = "今年" + splitRecordTime[1] + "月"
                }

            } else {
                if (recordMonth == "10" || recordMonth == "11" || recordMonth == "12") {
                    topMonthText?.text = recordYear + "年" + recordMonth + "月"
                } else {
                    var splitRecordTime = recordMonth.split("0")
                    topMonthText?.text = recordYear + "年" + splitRecordTime[1] + "月"
                }
            }
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

}

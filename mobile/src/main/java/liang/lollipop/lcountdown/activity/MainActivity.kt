package liang.lollipop.lcountdown.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.support.design.widget.BottomSheetBehavior
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.widget_countdown.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.drawable.StyleSelectedForeDrawable
import liang.lollipop.lcountdown.utils.CountdownUtil
import liang.lollipop.lcountdown.utils.WidgetDBUtil
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.widget.CountdownWidget
import java.util.*

/**
 * 小部件的参数设置Activity
 * @author Lollipop
 */
class MainActivity : BaseActivity(), View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    companion object {

        private const val WHAT_UPDATE = 99

        private const val DELAYED = WidgetUtil.UPDATE_TIME

    }

    private var widgetBean = WidgetBean()

    private var widgetStyle = WidgetStyle.LIGHT

    private val calendar = Calendar.getInstance()

    private var isCreateModel = false

    private lateinit var style1BtnBG: StyleSelectedForeDrawable
    private lateinit var style2BtnBG: StyleSelectedForeDrawable
    private lateinit var style3BtnBG: StyleSelectedForeDrawable
    private lateinit var style4BtnBG: StyleSelectedForeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()

        WidgetUtil.alarmUpdate(this)
    }

    private fun initView(){
        widgetBean.widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)

        isCreateModel = intent.getIntExtra(CountdownWidget.WIDGET_SHOW,0) < 1
        if(isCreateModel){
            val bottomSheetBehavior = BottomSheetBehavior.from(sheetGroup)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }else{
            updateBtn.hide()
        }

        nameInputView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameView.text = s
            }
        })

        signInputView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signView.text = s
            }
        })

        dateSelectView.setOnClickListener(this)
        timeSelectView.setOnClickListener(this)
        updateBtn.setOnClickListener(this)

        style1BtnBG = StyleSelectedForeDrawable(this)
        style2BtnBG = StyleSelectedForeDrawable(this)
        style3BtnBG = StyleSelectedForeDrawable(this, R.drawable.bg_black)
        style4BtnBG = StyleSelectedForeDrawable(this, R.drawable.bg_white)

        style1Btn.setOnClickListener(this)
        style1Btn.background = style1BtnBG
        style2Btn.setOnClickListener(this)
        style2Btn.background = style2BtnBG
        style3Btn.setOnClickListener(this)
        style3Btn.background = style3BtnBG
        style4Btn.setOnClickListener(this)
        style4Btn.background = style4BtnBG

        val bottomSheetBehavior = BottomSheetBehavior.from(sheetGroup)
        bottomSheetBehavior.setBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    updateBtn.show()
                }else{
                    updateBtn.hide()
                }

            }

        })

        noTimeCheckBox.setOnCheckedChangeListener(this)

    }

    private fun initData(){
        if(isCreateModel){
            widgetBean.endTime = System.currentTimeMillis()
            widgetBean.countdownName = getString(R.string.app_name)
            widgetBean.widgetStyle = WidgetStyle.LIGHT
            widgetBean.signValue = ""
        }else{
            WidgetDBUtil.read(this).get(widgetBean).close()
        }

        widgetStyle = widgetBean.widgetStyle

        nameInputView.setText(widgetBean.countdownName)
        signInputView.setText(widgetBean.signValue)

        calendar.timeInMillis = widgetBean.endTime

        noTimeCheckBox.isChecked = widgetBean.noTime

        onEndTimeChange()

        onStyleChange()
    }



    override fun onClick(v: View?) {

        when(v){

            dateSelectView -> {

                val yearIn = calendar.get(Calendar.YEAR)
                val monthIn = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                    _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR,year)

                    calendar.set(Calendar.MONTH,month)

                    calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

                    onEndTimeChange()
                },yearIn,monthIn,day).show()

            }

            timeSelectView -> {

                val hourIn = calendar.get(Calendar.HOUR_OF_DAY)
                val minuteIn = calendar.get(Calendar.MINUTE)
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)

                    calendar.set(Calendar.MINUTE,minute)

                    onEndTimeChange()

                },hourIn,minuteIn,false).show()

            }

            updateBtn -> {
                updateWidget()
            }

            style1Btn -> {
                onStyleChange(WidgetStyle.LIGHT)
            }

            style2Btn -> {
                onStyleChange(WidgetStyle.DARK)
            }

            style3Btn -> {
                onStyleChange(WidgetStyle.BLACK)
            }

            style4Btn -> {
                onStyleChange(WidgetStyle.WHITE)
            }

        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when(buttonView){

            noTimeCheckBox -> {

                timeView.visibility = if(isChecked){View.GONE}else{View.VISIBLE}

            }

        }

    }

    override fun onHandler(message: Message) {

        when(message.what){

            WHAT_UPDATE -> {

                countdown(calendar.timeInMillis)

                handler.sendEmptyMessageDelayed(WHAT_UPDATE, DELAYED)

            }

        }

    }

    private fun updateWidget(){

        widgetBean.countdownName = nameInputView.text.toString()
        widgetBean.endTime = calendar.timeInMillis
        widgetBean.signValue = signInputView.text.toString()
        widgetBean.widgetStyle = widgetStyle
        widgetBean.noTime = noTimeCheckBox.isChecked

        if(isCreateModel){
            WidgetDBUtil.write(this).add(widgetBean).close()
        }else{
            WidgetDBUtil.write(this).update(widgetBean).close()
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)

        val countdownBean = CountdownUtil.countdown(widgetBean.endTime)
        WidgetUtil.update(this,countdownBean,widgetBean,appWidgetManager)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetBean.widgetId)
        setResult(Activity.RESULT_OK, resultValue)

        finish()

    }

    private fun onEndTimeChange(){

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        dateSelectView.text = "${year.formatNumber()}年${month.formatNumber()}月${day.formatNumber()}日"
        timeSelectView.text = "${hour.formatNumber()} : ${minute.formatNumber()}"

        countdown(calendar.timeInMillis)

    }

    private fun countdown(endTime: Long){
        val bean = CountdownUtil.countdown(endTime)
        dayView.text = bean.days
        hourView.text = bean.hours
        timeView.text = bean.time
    }

    private fun Int.formatNumber(): String{
        return when {
            this > 9 -> ""+this
            this < -9 -> "-"+Math.abs(this)
            this < 0 -> "-0"+Math.abs(this)
            else -> "0"+this
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeMessages(WHAT_UPDATE)
    }

    override fun onStart() {
        super.onStart()
        val delayed = ( System.currentTimeMillis() / DELAYED + 1 ) * DELAYED
        handler.sendEmptyMessageDelayed(WHAT_UPDATE,delayed)
    }

    private fun onStyleChange(style: WidgetStyle){
        widgetStyle = style
        onStyleChange()
    }

    private fun onStyleChange(){
        style1BtnBG.isShow(false)
        style2BtnBG.isShow(false)
        style3BtnBG.isShow(false)
        style4BtnBG.isShow(false)

        var isDark = false

        when(widgetStyle){

            WidgetStyle.LIGHT -> {
                style1BtnBG.isShow(true)
            }

            WidgetStyle.DARK -> {
                style2BtnBG.isShow(true)
                isDark = true
            }

            WidgetStyle.BLACK -> {
                style3BtnBG.isShow(true)
            }

            WidgetStyle.WHITE -> {
                style4BtnBG.isShow(true)
                isDark = true
            }

        }

        val textColor = if(isDark){ 0xFF333333.toInt() }else{ Color.WHITE }
        val bgColor = if(isDark){ Color.WHITE }else{ Color.BLACK }

        contentGroup.setBackgroundColor(bgColor)

        changeTextViewColor(widgetFrame,textColor)
    }

    private fun changeTextViewColor(viewGroup:ViewGroup, color: Int){
        for(index in 0 until viewGroup.childCount){

            val child = viewGroup.getChildAt(index)

            if(child is ViewGroup){
                changeTextViewColor(child,color)
            }else if(child is TextView){
                child.setTextColor(color)
            }else{
                continue
            }

        }
    }

}

package liang.lollipop.lcountdown.activity

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.widget_countdown.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lbaselib.base.BaseFragment
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.fragment.CountdownFontSizeFragment
import liang.lollipop.lcountdown.fragment.CountdownInfoFragment
import liang.lollipop.lcountdown.fragment.CountdownUnitFragment
import liang.lollipop.lcountdown.utils.CountdownUtil
import liang.lollipop.lcountdown.utils.WidgetDBUtil
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.widget.CountdownWidget

/**
 * 小部件的参数设置Activity
 * @author Lollipop
 */
class MainActivity : BaseActivity(),CountdownInfoFragment.Callback,
    CountdownUnitFragment.Callback,CountdownFontSizeFragment.Callback{

    companion object {

        private const val WHAT_UPDATE = 99

        private const val DELAYED = WidgetUtil.UPDATE_TIME

    }

    private var widgetBean = WidgetBean()

    private var isCreateModel = false

    private val countdownInfoFragment = CountdownInfoFragment()

    private val countdownUnitFragment = CountdownUnitFragment()

    private val countdownFontSizeFragment = CountdownFontSizeFragment()

    private val fragments = arrayOf(countdownInfoFragment,countdownUnitFragment,countdownFontSizeFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()

        WidgetUtil.alarmUpdate(this)
    }

    private var sheetState = BottomSheetBehavior.STATE_COLLAPSED

    private fun initView(){
        widgetBean.widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)

        val bottomSheetBehavior = BottomSheetBehavior.from(sheetGroup)
        bottomSheetBehavior.setBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                sheetState = newState
                updateButton(newState)
            }

        })

        isCreateModel = intent.getIntExtra(CountdownWidget.WIDGET_SHOW,0) < 1
        if(isCreateModel){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }else{
            updateButton(BottomSheetBehavior.STATE_COLLAPSED)
        }

        updateBtn.setOnClickListener{
            updateWidget()
        }

        sheetBtn.setOnClickListener {
            if(sheetState == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        viewPager.adapter = Adapter(supportFragmentManager,fragments)
        viewPager.adapter?.notifyDataSetChanged()

        if(CountdownUtil.isShowNewVersionHint(this,"MainActivity")){
            AlertDialog.Builder(this).apply {
                setTitle(R.string.new_way_of_operation)
                setMessage(R.string.new_way_of_operation_info)
                setPositiveButton(R.string.got_it){ dialog, _ ->
                    dialog.dismiss()
                }
                setNeutralButton(R.string.do_not_show_again){ dialog, _ ->
                    CountdownUtil.newVersionHintShown(this@MainActivity,"MainActivity")
                    dialog.dismiss()
                }
            }.show()
        }
    }

    private fun updateButton(newState: Int) {
        if(newState == BottomSheetBehavior.STATE_EXPANDED){
            updateBtn.show()
            sheetBtn.animate().let {
                it.cancel()
                it.rotation(180F).start()
            }
        }else{
            updateBtn.hide()
            sheetBtn.animate().let {
                it.cancel()
                it.rotation(0F).start()
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        viewPager.requestLayout()
    }

    private fun initData(){
        if(isCreateModel){
            widgetBean.endTime = System.currentTimeMillis()
            widgetBean.countdownName = getString(R.string.app_name)
            widgetBean.widgetStyle = WidgetStyle.LIGHT
            widgetBean.signValue = ""
            widgetBean.prefixName = getString(R.string.left_until)
            widgetBean.suffixName = getString(R.string.the_end)
            widgetBean.dayUnit = getString(R.string.day)
            widgetBean.hourUnit = getString(R.string.hour)
        }else{
            WidgetDBUtil.read(this).get(widgetBean).close()
        }

        onNameInfoChange(widgetBean.countdownName)
        onSignInfoChange(widgetBean.signValue)
        onTimeTypeChange(widgetBean.noTime)
        onTimeInfoChange(widgetBean.endTime)
        onStyleInfoChange(widgetBean.widgetStyle)

        onPrefixNameChange(widgetBean.prefixName)
        onSuffixNameChange(widgetBean.suffixName)
        onDayUnitChange(widgetBean.dayUnit)
        onHourUnitChange(widgetBean.hourUnit)

        onPrefixFontSizeChange(widgetBean.prefixFontSize)
        onNameFontSizeChange(widgetBean.nameFontSize)
        onSuffixFontSizeChange(widgetBean.suffixFontSize)
        onDayFontSizeChange(widgetBean.dayFontSize)
        onDayUnitFontSizeChange(widgetBean.dayUnitFontSize)
        onHourFontSizeChange(widgetBean.hourFontSize)
        onHourUnitFontSizeChange(widgetBean.hourUnitFontSize)
        onTimeFontSizeChange(widgetBean.timeFontSize)
        onSignFontSizeChange(widgetBean.signFontSize)

        countdownInfoFragment.reset(widgetBean)
        countdownUnitFragment.reset(widgetBean)
        countdownFontSizeFragment.reset(widgetBean)

    }

    override fun onHandler(message: Message) {

        when(message.what){

            WHAT_UPDATE -> {

                countdown()

                handler.sendEmptyMessageDelayed(WHAT_UPDATE, DELAYED)

            }

        }

    }

    private fun updateWidget(){

        if(isCreateModel){
            WidgetDBUtil.write(this).add(widgetBean).close()
        }else{
            WidgetDBUtil.write(this).update(widgetBean).close()
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)

        WidgetUtil.update(this,widgetBean,appWidgetManager)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetBean.widgetId)
        setResult(Activity.RESULT_OK, resultValue)

        finish()

    }

    private fun countdown(){
        val bean = widgetBean.getTimerInfo()
        dayView.text = bean.days
        hourView.text = bean.hours
        timeView.text = bean.time
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

    override fun onNameInfoChange(name: CharSequence) {
        widgetBean.countdownName = name.toString()
        nameView.text = name
    }

    override fun onSignInfoChange(sign: CharSequence) {
        widgetBean.signValue = sign.toString()
        signView.text = sign
    }

    override fun onTimeTypeChange(noTime: Boolean) {
        widgetBean.noTime = noTime
        timeView.visibility = if(noTime){View.GONE}else{View.VISIBLE}
    }

    override fun onTimeInfoChange(time: Long) {
        widgetBean.endTime = time
        countdown()
    }

    override fun onTimingTypeChange(noCountdown: Boolean) {
        widgetBean.noCountdown = noCountdown
        countdown()
    }

    override fun onStyleInfoChange(style: WidgetStyle) {
        val isDark = ( style == WidgetStyle.WHITE || style == WidgetStyle.DARK )

        val textColor = if(isDark){ 0xFF333333.toInt() }else{ Color.WHITE }
        val bgColor = if(isDark){ Color.WHITE }else{ Color.BLACK }

        contentGroup.setBackgroundColor(bgColor)

        changeTextViewColor(widgetFrame,textColor)

        widgetBean.widgetStyle = style
    }

    override fun onPrefixNameChange(name: CharSequence) {
        widgetBean.prefixName = name.toString()
        nameFrontView.text = name
    }

    override fun onSuffixNameChange(name: CharSequence) {
        widgetBean.suffixName = name.toString()
        nameBehindView.text = name
    }

    override fun onDayUnitChange(name: CharSequence) {
        widgetBean.dayUnit = name.toString()
        dayUnitView.text = name
    }

    override fun onHourUnitChange(name: CharSequence) {
        widgetBean.hourUnit = name.toString()
        hourUnitView.text = name
    }

    override fun onPrefixFontSizeChange(sizeDip: Int) {
        widgetBean.prefixFontSize = sizeDip
        nameFrontView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onNameFontSizeChange(sizeDip: Int) {
        widgetBean.nameFontSize = sizeDip
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onSuffixFontSizeChange(sizeDip: Int) {
        widgetBean.suffixFontSize = sizeDip
        nameBehindView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onDayFontSizeChange(sizeDip: Int) {
        widgetBean.dayFontSize = sizeDip
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onDayUnitFontSizeChange(sizeDip: Int) {
        widgetBean.dayUnitFontSize = sizeDip
        dayUnitView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onHourFontSizeChange(sizeDip: Int) {
        widgetBean.hourFontSize = sizeDip
        hourView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onHourUnitFontSizeChange(sizeDip: Int) {
        widgetBean.hourUnitFontSize = sizeDip
        hourUnitView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onTimeFontSizeChange(sizeDip: Int) {
        widgetBean.timeFontSize = sizeDip
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
    }

    override fun onSignFontSizeChange(sizeDip: Int) {
        widgetBean.signFontSize = sizeDip
        signView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeDip.toFloat())
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

    private class Adapter(fragmentManager: FragmentManager,
                          private val fragments: Array<BaseFragment>)
        : FragmentStatePagerAdapter(fragmentManager){

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }

}

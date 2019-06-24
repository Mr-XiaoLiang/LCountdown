package liang.lollipop.lcountdown.holder

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.lbaselib.base.BaseHolder
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.utils.CountdownUtil
import liang.lollipop.lcountdown.utils.WidgetUtil

class WidgetHolder private constructor(itemView: View): BaseHolder<WidgetBean>(itemView) {

    companion object {

        fun newInstance(layoutInflater: LayoutInflater,viewGroup: ViewGroup): WidgetHolder{
            val layoutId = R.layout.widget_countdown
            return create(layoutInflater,viewGroup,layoutId)
        }

        private fun create(layoutInflater: LayoutInflater,viewGroup: ViewGroup, layoutId: Int): WidgetHolder{
            val group = layoutInflater.inflate(R.layout.item_widget_group,viewGroup,false)
            layoutInflater.inflate(layoutId,group.findViewById(R.id.widgetItemGroup),true)
            return WidgetHolder(group)

        }

    }

    init {

        canMove = true
        canSwipe = false

    }

    override fun onBind(bean: WidgetBean) {

        val countdownBean = CountdownUtil.countdown(bean.endTime)
        setTextViewText(R.id.nameView,bean.countdownName)
        setTextViewText(R.id.dayView,countdownBean.days)
        setTextViewText(R.id.timeView,countdownBean.time)
        setTextViewText(R.id.signView,bean.signValue)
        find<View>(R.id.timeView).visibility = if(bean.noTime){ View.GONE }else{ View.VISIBLE }
        setTextViewText(R.id.nameFrontView,bean.prefixName)
        setTextViewText(R.id.nameBehindView,bean.suffixName)
        setTextViewText(R.id.dayUnitView,bean.dayUnit)

        setTextViewTextSize(R.id.nameFrontView,TypedValue.COMPLEX_UNIT_SP,bean.prefixFontSize.toFloat())
        setTextViewTextSize(R.id.nameView,TypedValue.COMPLEX_UNIT_SP,bean.nameFontSize.toFloat())
        setTextViewTextSize(R.id.nameBehindView,TypedValue.COMPLEX_UNIT_SP,bean.suffixFontSize.toFloat())
        setTextViewTextSize(R.id.dayView,TypedValue.COMPLEX_UNIT_SP,bean.dayFontSize.toFloat())
        setTextViewTextSize(R.id.dayUnitView,TypedValue.COMPLEX_UNIT_SP,bean.dayUnitFontSize.toFloat())
        setTextViewTextSize(R.id.timeView,TypedValue.COMPLEX_UNIT_SP,bean.timeFontSize.toFloat())
        setTextViewTextSize(R.id.signView,TypedValue.COMPLEX_UNIT_SP,bean.signFontSize.toFloat())
        find<View>(R.id.dayGroup).visibility = if (bean.inOneDay) { View.GONE } else { View.VISIBLE }

        WidgetUtil.updateTextColorByStyle(bean.widgetStyle) { id, color ->
            setTextColor(id, color)
        }
        val background = when (bean.widgetStyle) {
            WidgetStyle.DARK -> {
                find<View>(R.id.widgetItemGroup).setBackgroundColor(Color.WHITE)
                0
            }
            WidgetStyle.WHITE -> {
                find<View>(R.id.widgetItemGroup).setBackgroundColor(Color.BLACK)
                R.drawable.bg_white
            }
            WidgetStyle.LIGHT -> {
                find<View>(R.id.widgetItemGroup).setBackgroundColor(Color.BLACK)
                0
            }
            WidgetStyle.BLACK -> {
                find<View>(R.id.widgetItemGroup).setBackgroundColor(Color.WHITE)
                R.drawable.bg_black
            }
        }
        find<View>(R.id.widgetGroup).setBackgroundResource(background)
    }

    private fun setTextViewText(id: Int, value: String) {
        find<TextView>(id).text = value
    }

    private fun setTextColor(id: Int, color: Int) {
        find<TextView>(id).setTextColor(color)
    }

    private fun setTextViewTextSize(id: Int, unit: Int, size: Float) {
        find<TextView>(id).setTextSize(unit, size)
    }

}
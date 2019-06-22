package liang.lollipop.lcountdown.holder

import android.graphics.Color
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
import org.jetbrains.anko.textColor

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

    private val nameView: TextView = find(R.id.nameView)
    private val dayView: TextView = find(R.id.dayView)
    private val timeView: TextView = find(R.id.timeView)
    private val signView: TextView = find(R.id.signView)

    init {

        canMove = true
        canSwipe = false

    }

    override fun onBind(bean: WidgetBean) {

        nameView.text = bean.countdownName
        val countdownBean = CountdownUtil.countdown(bean.endTime)
        dayView.text = countdownBean.days
        timeView.text = countdownBean.time
        signView.text = bean.signValue

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

    private fun setTextColor(id: Int, color: Int) {
        find<TextView>(id).textColor = color
    }

}
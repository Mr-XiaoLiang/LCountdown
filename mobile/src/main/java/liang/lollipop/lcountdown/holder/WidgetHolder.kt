package liang.lollipop.lcountdown.holder

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import liang.lollipop.lbaselib.base.BaseHolder
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.utils.CountdownUtil

class WidgetHolder private constructor(itemView: View): BaseHolder<WidgetBean>(itemView) {

    companion object {

        fun newInstance(layoutInflater: LayoutInflater,viewGroup: ViewGroup, style: Int): WidgetHolder{
            val layoutId = when(style){

                WidgetStyle.WHITE.value -> R.layout.widget_countdown_white

                WidgetStyle.BLACK.value -> R.layout.widget_countdown_black

                WidgetStyle.DARK.value -> R.layout.widget_countdown_dark

                else -> R.layout.widget_countdown

            }
            return create(layoutInflater,viewGroup,layoutId)
        }

        private fun create(layoutInflater: LayoutInflater,viewGroup: ViewGroup, layoutId: Int): WidgetHolder{

            val group = layoutInflater.inflate(R.layout.item_widget_group,viewGroup,false)
            layoutInflater.inflate(layoutId,group.findViewById(R.id.widgetItemGroup),true)
            return WidgetHolder(group)

        }

    }

    private val nameView: TextView = itemView.findViewById(R.id.nameView)
    private val dayView: TextView = itemView.findViewById(R.id.dayView)
    private val timeView: TextView = itemView.findViewById(R.id.timeView)
    private val signView: TextView = itemView.findViewById(R.id.signView)

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

    }

}
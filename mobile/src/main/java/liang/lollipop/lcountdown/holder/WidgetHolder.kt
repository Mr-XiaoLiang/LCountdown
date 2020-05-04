package liang.lollipop.lcountdown.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseHolder
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.utils.WidgetUtil

class WidgetHolder private constructor(itemView: View) : BaseHolder<WidgetBean>(itemView) {
    companion object {
        fun newInstance(layoutInflater: LayoutInflater, viewGroup: ViewGroup): WidgetHolder {
            val group = layoutInflater.inflate(R.layout.item_widget_group, viewGroup, false)
            layoutInflater.inflate(R.layout.widget_countdown, group.findViewById(R.id.widgetItemGroup), true)
            return WidgetHolder(group)
        }
    }

    private val viewImageView = WidgetUtil.NativeViewInterface(itemView)

    init {
        canMove = true
        canSwipe = false
    }

    override fun onBind(bean: WidgetBean) {
        WidgetUtil.updateUI(context, bean, viewImageView)
    }

}
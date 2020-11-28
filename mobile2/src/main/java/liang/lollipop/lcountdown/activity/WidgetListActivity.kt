package liang.lollipop.lcountdown.activity

import android.appwidget.AppWidgetManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.engine.WidgetEngine
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.util.list.DirectionInfo
import liang.lollipop.lcountdown.util.list.SwipeableHolder

/**
 * 小部件列表
 * 包括已展示的小部件、未展示的小部件
 */
class WidgetListActivity : AppBarActivity() {

    override val layoutId: Int
        get() = R.layout.activity_widget_list

    private val widgetData = ArrayList<WidgetInfo>()



    private class WidgetAdapter(
            private val data: ArrayList<WidgetInfo>,
            private val onClick: (Int) -> Unit): RecyclerView.Adapter<WidgetHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetHolder {
            return WidgetHolder.create(parent, ::onHolderClick)
        }

        override fun onBindViewHolder(holder: WidgetHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        private fun onHolderClick(holder: WidgetHolder) {
            onClick(holder.adapterPosition)
        }

    }

    private class WidgetHolder
        private constructor(view: View,
            private val onClick: (WidgetHolder) -> Unit): RecyclerView.ViewHolder(view), SwipeableHolder {

            companion object {
                fun create(
                        group: ViewGroup,
                        onClick: (WidgetHolder) -> Unit): WidgetHolder {
                    return WidgetHolder(
                            LayoutInflater.from(group.context)
                                    .inflate(R.layout.item_widget_list,
                                            group, false),
                            onClick)
                }
            }

        private val widgetRoot = itemView.findViewById<FrameLayout>(R.id.widgetRoot)

        private val widgetEngine = WidgetEngine(widgetRoot)

        private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

        init {
            widgetRoot.setOnClickListener {
                onClick(this)
            }
        }

        fun bind(info: WidgetInfo) {
            widgetEngine.updateAll(info)
        }

        override fun canSwipe(): DirectionInfo {
            return if (widgetId == -1 ||
                    widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                DirectionInfo.NONE
            } else {
                DirectionInfo.HORIZONTAL
            }
        }

    }

}
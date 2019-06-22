package liang.lollipop.lcountdown.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import liang.lollipop.lbaselib.base.LSimpleAdapter
import liang.lollipop.lbaselib.util.LItemTouchHelper
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.holder.WidgetHolder

class WidgetListAdapter(data:ArrayList<WidgetBean>,
                        private val layoutInflater: LayoutInflater,
                        private val helper: LItemTouchHelper) : LSimpleAdapter<WidgetHolder,WidgetBean>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetHolder {

        return WidgetHolder.newInstance(layoutInflater,parent).apply {
            setTouchHelper(helper)
        }

    }

}
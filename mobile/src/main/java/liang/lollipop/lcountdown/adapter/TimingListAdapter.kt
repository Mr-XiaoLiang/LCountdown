package liang.lollipop.lcountdown.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import liang.lollipop.lbaselib.base.LSimpleAdapter
import liang.lollipop.lbaselib.util.LItemTouchHelper
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.holder.TimingHolder

/**
 * 计时器列表的适配器
 * @author Lollipop
 */
class TimingListAdapter(data: ArrayList<TimingBean>,
                        private val layoutInflater: LayoutInflater,
                        private val touchHelper: LItemTouchHelper): LSimpleAdapter<TimingHolder,TimingBean>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimingHolder {

        return TimingHolder.new(layoutInflater,parent).apply {
            setTouchHelper(touchHelper)
        }

    }

    override fun onViewRecycled(holder: TimingHolder) {
        super.onViewRecycled(holder)
        holder.stopTiming()
    }

}
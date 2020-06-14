package liang.lollipop.lcountdown.fragment.adjustment

import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 2020/6/14 22:09
 * 时间调整的碎片
 */
class TimeAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_time

    override val icon = R.drawable.ic_baseline_access_time_24

    override val title = R.string.title_time

    override val colorId = R.color.focusTimeAdjust

}
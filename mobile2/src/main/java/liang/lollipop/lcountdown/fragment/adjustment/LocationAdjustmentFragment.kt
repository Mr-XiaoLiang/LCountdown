package liang.lollipop.lcountdown.fragment.adjustment

import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 7/30/20 23:42
 */
class LocationAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_location
    override val icon: Int
        get() = R.drawable.ic_baseline_format_shapes_24
    override val title: Int
        get() = R.string.title_location
    override val colorId: Int
        get() = R.color.focusLocationAdjust


}

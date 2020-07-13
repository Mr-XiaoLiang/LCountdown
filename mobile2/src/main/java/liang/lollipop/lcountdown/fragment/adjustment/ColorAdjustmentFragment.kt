package liang.lollipop.lcountdown.fragment.adjustment

import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 7/14/20 00:13
 * 颜色调整的fragment
 */
class ColorAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_color
    override val icon: Int
        get() = R.drawable.ic_baseline_color_lens_24
    override val title: Int
        get() = R.string.title_color
    override val colorId: Int
        get() = R.color.focusColorAdjust
}
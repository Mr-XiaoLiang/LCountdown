package liang.lollipop.lcountdown.fragment.adjustment

import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 8/1/20 22:14
 * 卡片样式的调整
 */
class CardAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_card
    override val icon: Int
        get() = R.drawable.ic_baseline_crop_24
    override val title: Int
        get() = R.string.title_card
    override val colorId: Int
        get() = R.color.focusCardAdjust
}
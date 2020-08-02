package liang.lollipop.lcountdown.fragment.adjustment

import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 7/29/20 23:39
 * 背景梯度渐变渲染的Fragment
 */
class BackgroundGradientAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_background_gradient
    override val icon: Int
        get() = R.drawable.ic_baseline_gradient_24
    override val title: Int
        get() = R.string.title_background_gradient
    override val colorId: Int
        get() = R.color.focusGradientAdjust
}
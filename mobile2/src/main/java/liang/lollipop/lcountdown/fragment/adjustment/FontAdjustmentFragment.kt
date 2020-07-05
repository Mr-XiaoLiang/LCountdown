package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.View
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.view.InnerDialogProvider

/**
 * @author lollipop
 * @date 2020/6/20 20:55
 * 文字调整的碎片页
 */
class FontAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_font

    override val icon = R.drawable.ic_baseline_format_size_24

    override val title = R.string.title_font

    override val colorId = R.color.focusFontAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private class AdjustmentProvider: InnerDialogProvider() {
        override val layoutId = R.layout.dialog_adjustment_font
    }



}
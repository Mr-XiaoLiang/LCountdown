package liang.lollipop.lcountdown.fragment.adjustment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_adjustment_background_gradient.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.util.toDip

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testShapeBtn.changeStart(0.5F, 0.5F)
        testShapeBtn.changeEnd(0.5F, 0F)
        testShapeBtn.changeColor(Color.RED, Color.GREEN, Color.BLUE)
        testShapeBtn.setType(GradientDrawable.Type.Sweep)
        testShapeBtn.setCorner(5.toDip(testShapeBtn))
        var index = 0
        testShapeBtn.isChecked = true
        testShapeBtn.setOnClickListener {
            index++
            testShapeBtn.setType(when(index % 3) {
                0 -> GradientDrawable.Type.Linear
                1 -> GradientDrawable.Type.Sweep
                2 -> GradientDrawable.Type.Radial
                else -> GradientDrawable.Type.Linear
            })
            testShapeBtn.invalidate()
        }
    }

}
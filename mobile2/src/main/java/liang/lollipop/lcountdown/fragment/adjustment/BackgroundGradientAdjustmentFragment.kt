package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_adjustment_background_gradient.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.util.doAsync
import liang.lollipop.lcountdown.util.findColor
import liang.lollipop.lcountdown.util.onUI
import liang.lollipop.lcountdown.util.toDip
import liang.lollipop.lcountdown.view.ShapeView

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

    companion object {
        private const val Linear = 0
        private const val Sweep = 1
        private const val Radial = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ShapeView.CheckedGroup()
                .add(linearShapeBtn, sweepShapeBtn, radialShapeBtn)
                .foreach {
                    initShapeBtn(it)
                }
                .select(linearShapeBtn)
                .onCheckedChange {
                    when (it) {
                        linearShapeBtn -> {
                            onSelectedTypeChange(Linear)
                        }
                        sweepShapeBtn -> {
                            onSelectedTypeChange(Sweep)
                        }
                        radialShapeBtn -> {
                            onSelectedTypeChange(Radial)
                        }
                    }
        }
        lineView.color = R.color.linePlatBackground.findColor(lineView)
        lineView.pointColor = R.color.colorPrimary.findColor(lineView)
        lineView.pointRadius = 10.toDip(lineView)
        lineView.touchRadius = 20.toDip(lineView)
        lineView.onMoved { startX, startY, endX, endY ->
            onMovedChange(startX, startY, endX, endY)
        }
    }

    private fun onSelectedTypeChange(type: Int) {
        // TODO
    }

    private fun onMovedChange(startX: Float, startY: Float, endX: Float, endY: Float) {
        // TODO
    }

    private fun initShapeBtn(btn: ShapeView) {
        btn.setCorner(10.toDip(btn))
        btn.changeColor(R.color.colorAccent.findColor(btn), R.color.colorPrimary.findColor(btn))
        when (btn) {
            linearShapeBtn -> {
                btn.setType(GradientDrawable.Type.Linear)
                btn.changeStart(0F, 0F)
                btn.changeEnd(1F, 1F)
            }
            sweepShapeBtn -> {
                btn.setType(GradientDrawable.Type.Sweep)
                btn.changeStart(0.5F, 0.5F)
                btn.changeEnd(1F, 0.5F)
            }
            radialShapeBtn -> {
                btn.setType(GradientDrawable.Type.Radial)
                btn.changeStart(0.5F, 0.5F)
                btn.changeEnd(1F, 0.5F)
            }
        }
    }

}
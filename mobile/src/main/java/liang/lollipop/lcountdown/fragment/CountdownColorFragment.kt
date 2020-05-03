package liang.lollipop.lcountdown.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_countdown_color.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.view.ExpandButton

/**
 * @author lollipop
 * @date 2019-11-17 21:44
 * 设置颜色的Fragment
 */
class CountdownColorFragment: LTabFragment() {

    override fun getTitleId(): Int {
        return R.string.title_color_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_color_lens_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.colorTabSelected
    }

    private var selectedTarget = WidgetUtil.Target.Nothing
    private var callback: Callback? = null
    private var provider: Provider? = null

    private var targetColor = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_color, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            callback = context
        }
        if (context is Provider) {
            provider = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        expandBtnGroup.onSelectedBtnChange {
            onTargetChange(it)
            parser(provider?.getColorByTarget(selectedTarget)?:Color.WHITE)
        }
        expandBtnGroup.onExpandStateChange { _, isOpen ->
            if (isOpen) {
                hideView(transparencyPalette)
                hideView(huePalette)
                hideView(satValPalette)
            } else {
                showView(transparencyPalette)
                showView(huePalette)
                showView(satValPalette)
            }
        }
        huePalette.onHueChange { hue, _ ->
            satValPalette.onHueChange(hue.toFloat())
        }
        satValPalette.onHSVChange { _, color, isUser ->
            targetColor = color and 0x00FFFFFF or (targetColor and 0xFF000000.toInt())
            if (isUser) {
                onColorChange()
            }
        }
        transparencyPalette.onTransparencyChange { _, alphaI, isUser ->
            targetColor = targetColor and 0x00FFFFFF or (alphaI shl 24)
            if (isUser) {
                onColorChange()
            }
        }
        parser(Color.WHITE)
    }

    private fun onColorChange() {
        callback?.onColorChange(selectedTarget, targetColor)
    }

    private fun parser(color: Int) {
        targetColor = color
        transparencyPalette.parser(Color.alpha(color))
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        satValPalette.parser(hsv[1], hsv[2])
        huePalette.parser(hsv[0])
    }

    private fun hideView(view: View) {
        val animator = view.animate()
        animator.cancel()
        animator.alpha(0F)
        animator.start()
    }

    private fun showView(view: View, isAnimator: Boolean = true) {
        if (isAnimator) {
            val animator = view.animate()
            animator.cancel()
            animator.alpha(1F)
            animator.start()
        } else {
            view.alpha = 1F
        }
    }

    private fun onTargetChange(view: ExpandButton) {
        selectedTarget = when (view) {
            titleModeBtn ->  WidgetUtil.Target.Name
            prefixModeBtn -> WidgetUtil.Target.Prefix
            suffixModeBtn -> WidgetUtil.Target.Suffix
            daysModeBtn -> WidgetUtil.Target.Days
            unitModeBtn -> WidgetUtil.Target.Unit
            timeModeBtn -> WidgetUtil.Target.Time
            signModeBtn -> WidgetUtil.Target.Inscription
            else -> WidgetUtil.Target.Nothing
        }
    }

    interface Callback {
        fun onColorChange(target: WidgetUtil.Target, color: Int)
    }

    interface Provider {
        fun getColorByTarget(target: WidgetUtil.Target): Int
    }

}
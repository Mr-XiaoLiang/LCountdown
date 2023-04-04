package liang.lollipop.lcountdown.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.databinding.FragmentCountdownColorBinding
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.utils.lazyBind
import liang.lollipop.lcountdown.view.ExpandButton
import java.util.Locale

/**
 * @author lollipop
 * @date 2019-11-17 21:44
 * 设置颜色的Fragment
 */
class CountdownColorFragment : LTabFragment() {

    private val binding: FragmentCountdownColorBinding by lazyBind()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
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
        binding.expandBtnGroup.onSelectedBtnChange {
            onTargetChange(it)
            val target = if (selectedTarget == WidgetUtil.Target.All) {
                WidgetUtil.Target.Name
            } else {
                selectedTarget
            }
            parser(provider?.getColorByTarget(target) ?: Color.WHITE)
        }
        binding.expandBtnGroup.onExpandStateChange { _, isOpen ->
            if (isOpen) {
                hideView(binding.transparencyPalette)
                hideView(binding.huePalette)
                hideView(binding.satValPalette)
                hideView(binding.colorValueGroup)
            } else {
                showView(binding.transparencyPalette)
                showView(binding.huePalette)
                showView(binding.satValPalette)
                showView(binding.colorValueGroup)
            }
        }
        binding.huePalette.onHueChange { hue, _ ->
            binding.satValPalette.onHueChange(hue.toFloat())
        }
        binding.satValPalette.onHSVChange { _, color, isUser ->
            targetColor = color and 0x00FFFFFF or (targetColor and 0xFF000000.toInt())
            if (isUser) {
                onColorChange()
            }
        }
        binding.transparencyPalette.onTransparencyChange { _, alphaI, isUser ->
            targetColor = targetColor and 0x00FFFFFF or (alphaI shl 24)
            if (isUser) {
                onColorChange()
            }
        }

        binding.colorValueView.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                parserValue(binding.colorValueView.text?.toString() ?: "")
                true
            } else {
                false
            }
        }

        parser(Color.WHITE)
    }

    private fun parserValue(value: String) {
        if (value.isEmpty()) {
            colorToValue()
            return
        }
        val color: Int = when (value.length) {
            1 -> {
                val v = (value + value).toInt(16)
                Color.rgb(v, v, v)
            }

            2 -> {
                val v = value.toInt(16)
                Color.rgb(v, v, v)
            }

            3 -> {
                val r = value.substring(0, 1)
                val g = value.substring(1, 2)
                val b = value.substring(2, 3)
                Color.rgb(
                    (r + r).toInt(16),
                    (g + g).toInt(16),
                    (b + b).toInt(16)
                )
            }

            4, 5 -> {
                val a = value.substring(0, 1)
                val r = value.substring(1, 2)
                val g = value.substring(2, 3)
                val b = value.substring(3, 4)
                Color.argb(
                    (a + a).toInt(16),
                    (r + r).toInt(16),
                    (g + g).toInt(16),
                    (b + b).toInt(16)
                )
            }

            6, 7 -> {
                val r = value.substring(0, 2).toInt(16)
                val g = value.substring(2, 4).toInt(16)
                val b = value.substring(4, 6).toInt(16)
                Color.rgb(r, g, b)
            }

            8 -> {
                val a = value.substring(0, 2).toInt(16)
                val r = value.substring(2, 4).toInt(16)
                val g = value.substring(4, 6).toInt(16)
                val b = value.substring(6, 8).toInt(16)
                Color.argb(a, r, g, b)
            }

            else -> {
                Color.WHITE
            }
        }
        parser(color)
    }

    @SuppressLint("SetTextI18n")
    private fun colorToValue() {
        val alpha = Color.alpha(targetColor).format()
        val red = Color.red(targetColor).format()
        val green = Color.green(targetColor).format()
        val blue = Color.blue(targetColor).format()
        binding.colorValueView.setText("${alpha}${red}${green}${blue}")
    }

    private fun Int.format(): String {
        return this.toString(16).uppercase(Locale.US).let {
            if (it.length < 2) {
                "0$it"
            } else {
                it
            }
        }
    }

    private fun onColorChange() {
        if (selectedTarget == WidgetUtil.Target.All) {
            WidgetUtil.Target.values().forEach {
                if (it.value >= 0) {
                    callback?.onColorChange(it, targetColor)
                }
            }
        } else {
            callback?.onColorChange(selectedTarget, targetColor)
        }
        colorToValue()
    }

    private fun parser(color: Int) {
        targetColor = color
        binding.transparencyPalette.parser(Color.alpha(color))
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        binding.satValPalette.parser(hsv[1], hsv[2])
        binding.huePalette.parser(hsv[0])
        colorToValue()
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
            binding.allModeBtn -> WidgetUtil.Target.All
            binding.titleModeBtn -> WidgetUtil.Target.Name
            binding.prefixModeBtn -> WidgetUtil.Target.Prefix
            binding.suffixModeBtn -> WidgetUtil.Target.Suffix
            binding.daysModeBtn -> WidgetUtil.Target.Days
            binding.unitModeBtn -> WidgetUtil.Target.Unit
            binding.timeModeBtn -> WidgetUtil.Target.Time
            binding.signModeBtn -> WidgetUtil.Target.Inscription
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
package liang.lollipop.lcountdown.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.databinding.FragmentCountdownLocationBinding
import liang.lollipop.lcountdown.utils.CheckedButtonHelper
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.utils.lazyBind
import liang.lollipop.lcountdown.view.AutoSeekBar
import liang.lollipop.lcountdown.view.CheckImageView
import liang.lollipop.lcountdown.view.ExpandButton
import kotlin.math.abs

/**
 * @date: 2019-06-26 23:15
 * @author: lollipop
 * 位置调整的 Fragment
 */
class CountdownLocationFragment : LTabFragment() {

    private val binding: FragmentCountdownLocationBinding by lazyBind()

    override fun getTitleId(): Int {
        return R.string.title_loca_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_format_shapes_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.localTabSelected
    }

    private var onLocationChangeListener: OnLocationChangeListener? = null

    private var locationInfoProvider: LocationInfoProvider? = null

    private var isReady = false

    private var selectedTarget = WidgetUtil.Target.Nothing

    private var targetGravity = Gravity.NO_GRAVITY

    private val emptyInfo = WidgetBean.Location()

    private var checkedButtonHelper: CheckedButtonHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        isReady = true
        binding.expandBtnGroup.onSelectedBtnChange {
            onTargetChange(it)
            onLocationChange()
        }
        binding.expandBtnGroup.onExpandStateChange { _, isOpen ->
            if (isOpen) {
                hideView(binding.gridGroup)
                hideView(binding.horizontalSeekBar)
                hideView(binding.verticalSeekBar)
            } else {
                showView(binding.gridGroup)
                showView(binding.horizontalSeekBar)
                showView(binding.verticalSeekBar)
            }
        }
        checkedButtonHelper = CheckedButtonHelper.bind(binding.gridGroup)
        checkedButtonHelper?.onChecked { checkImageView, _ ->
            onGravityChange(checkImageView)
            onLocationChange()
        }
        val seekBarTouchListener = object : AutoSeekBar.OnTouchStateChangeListener {
            override fun onTouchStateChange(view: AutoSeekBar, isTouch: Boolean) {
                if (binding.floatText.visibility != View.VISIBLE) {
                    binding.floatText.alpha = 0F
                    binding.floatText.scaleX = 0F
                    binding.floatText.scaleY = 0F
                    binding.floatText.visibility = View.VISIBLE
                }
                val anim = binding.floatText.animate()
                anim.cancel()
                if (isTouch) {
                    anim.alpha(1F).scaleX(1F).scaleY(1F).start()
                } else if (!binding.verticalSeekBar.isPressed && !binding.horizontalSeekBar.isPressed) {
                    anim.alpha(0F).scaleX(0F).scaleY(0F).start()
                }
            }
        }
        val seekBarChangeListener = object : AutoSeekBar.OnProgressChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChange(view: AutoSeekBar, progress: Float) {
                binding.floatText.text =
                    "${abs(binding.horizontalSeekBar.progress).format()}\n${abs(binding.verticalSeekBar.progress).format()}"
                onLocationChange()
            }
        }
        binding.verticalSeekBar.onTouchStateChangeListener = seekBarTouchListener
        binding.horizontalSeekBar.onTouchStateChangeListener = seekBarTouchListener
        binding.verticalSeekBar.onProgressChangeListener = seekBarChangeListener
        binding.horizontalSeekBar.onProgressChangeListener = seekBarChangeListener
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

    private fun Float.format(): String {
        return this.toInt().toString()
    }

    override fun onStop() {
        super.onStop()
        binding.expandBtnGroup.closeAll()
        showView(binding.gridGroup, false)
        showView(binding.horizontalSeekBar, false)
        showView(binding.verticalSeekBar, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLocationChangeListener) {
            onLocationChangeListener = context
        }
        if (context is LocationInfoProvider) {
            locationInfoProvider = context
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun setCheckedByGravity(value: Int) {
        this.targetGravity = value
        val btn = when (value) {
            (Gravity.LEFT or Gravity.TOP) -> binding.leftTopGrid
            (Gravity.CENTER or Gravity.TOP) -> binding.centerTopGrid
            (Gravity.RIGHT or Gravity.TOP) -> binding.rightTopGrid
            (Gravity.LEFT or Gravity.CENTER) -> binding.leftMiddleGrid
            (Gravity.CENTER) -> binding.centerMiddleGrid
            (Gravity.RIGHT or Gravity.CENTER) -> binding.rightMiddleGrid
            (Gravity.LEFT or Gravity.BOTTOM) -> binding.leftBottomGrid
            (Gravity.BOTTOM or Gravity.CENTER) -> binding.centerBottomGrid
            (Gravity.RIGHT or Gravity.BOTTOM) -> binding.rightBottomGrid
            else -> null
        }
        checkedButtonHelper?.setChecked(btn, isChecked = true, cellListener = false)
    }

    @SuppressLint("RtlHardcoded")
    private fun onGravityChange(checkImageView: CheckImageView?) {
        targetGravity = when (checkImageView) {
            binding.leftTopGrid -> {
                Gravity.LEFT or Gravity.TOP
            }

            binding.centerTopGrid -> {
                Gravity.CENTER or Gravity.TOP
            }

            binding.rightTopGrid -> {
                Gravity.RIGHT or Gravity.TOP
            }

            binding.leftMiddleGrid -> {
                Gravity.LEFT or Gravity.CENTER
            }

            binding.centerMiddleGrid -> {
                Gravity.CENTER
            }

            binding.rightMiddleGrid -> {
                Gravity.RIGHT or Gravity.CENTER
            }

            binding.leftBottomGrid -> {
                Gravity.LEFT or Gravity.BOTTOM
            }

            binding.centerBottomGrid -> {
                Gravity.BOTTOM or Gravity.CENTER
            }

            binding.rightBottomGrid -> {
                Gravity.RIGHT or Gravity.BOTTOM
            }

            else -> {
                Gravity.NO_GRAVITY
            }
        }
        binding.verticalSeekBar.setProgress(0F, false)
        binding.horizontalSeekBar.setProgress(0F, false)
    }

    private fun onTargetChange(view: ExpandButton) {
        selectedTarget = when (view) {
            binding.titleModeBtn -> WidgetUtil.Target.Name
            binding.prefixModeBtn -> WidgetUtil.Target.Prefix
            binding.suffixModeBtn -> WidgetUtil.Target.Suffix
            binding.daysModeBtn -> WidgetUtil.Target.Days
            binding.unitModeBtn -> WidgetUtil.Target.Unit
            binding.timeModeBtn -> WidgetUtil.Target.Time
            binding.signModeBtn -> WidgetUtil.Target.Inscription
            else -> WidgetUtil.Target.Nothing
        }
        val info = locationInfoProvider?.getLocationInfo(selectedTarget) ?: emptyInfo
        setCheckedByGravity(info.gravity)
        binding.verticalSeekBar.setProgress(info.verticalMargin, false)
        Log.d(
            "Lollipop",
            "info.verticalMargin: ${info.verticalMargin}, verticalSeekBar: ${binding.verticalSeekBar.progress}"
        )
        binding.horizontalSeekBar.setProgress(info.horizontalMargin, false)
    }

    private fun onLocationChange() {
        onLocationChangeListener?.onLocationChange(
            selectedTarget, targetGravity,
            binding.verticalSeekBar.progress,
            binding.horizontalSeekBar.progress
        )
    }

    fun requestFocus() {
        onLocationChange()
    }

    interface OnLocationChangeListener {
        fun onLocationChange(
            target: WidgetUtil.Target,
            gravity: Int,
            verticalMargin: Float,
            horizontalMargin: Float
        )
    }

    interface LocationInfoProvider {
        fun getLocationInfo(target: WidgetUtil.Target): WidgetBean.Location?
    }

}
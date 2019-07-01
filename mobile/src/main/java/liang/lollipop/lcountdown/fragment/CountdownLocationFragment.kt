package liang.lollipop.lcountdown.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_countdown_location.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.utils.CheckedButtonHelper
import liang.lollipop.lcountdown.view.AutoSeekBar
import liang.lollipop.lcountdown.view.CheckImageView
import liang.lollipop.lcountdown.view.ExpandButton
import kotlin.math.abs

/**
 * @date: 2019-06-26 23:15
 * @author: lollipop
 * 位置调整的 Fragment
 */
class CountdownLocationFragment: LTabFragment() {
    override fun getTitleId(): Int {
        return R.string.title_loca_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_format_shapes_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.locaTabSelected
    }

    private var onLocationChangeListener: OnLocationChangeListener? = null

    private var locationInfoProvider: LocationInfoProvider? = null

    private var isReady = false

    private var selectedTarget = Target.Nothing

    private var targetGravity = Gravity.NO_GRAVITY

    private val emptyInfo = WidgetBean.Location()

    private var checkedButtonHelper: CheckedButtonHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_location,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        isReady = true
        expandBtnGroup.onSelectedBtnChange {
            onTargetChange(it)
            onLocationChange()
        }
        expandBtnGroup.onExpandStateChange { _, isOpen ->
            if (isOpen) {
                hideView(gridGroup)
                hideView(horizontalSeekBar)
                hideView(verticalSeekBar)
            } else {
                showView(gridGroup)
                showView(horizontalSeekBar)
                showView(verticalSeekBar)
            }
        }
        checkedButtonHelper = CheckedButtonHelper.bind(gridGroup)
        checkedButtonHelper?.onChecked { checkImageView, _ ->
            onGravityChange(checkImageView)
            onLocationChange()
        }
        val seekBarTouchListener = object: AutoSeekBar.OnTouchStateChangeListener {
            override fun onTouchStateChange(view: AutoSeekBar, isTouch: Boolean) {
                if (floatText.visibility != View.VISIBLE) {
                    floatText.alpha = 0F
                    floatText.scaleX = 0F
                    floatText.scaleY = 0F
                    floatText.visibility = View.VISIBLE
                }
                val anim = floatText.animate()
                anim.cancel()
                if (isTouch) {
                    anim.alpha(1F).scaleX(1F).scaleY(1F).start()
                } else if (!verticalSeekBar.isPressed && !horizontalSeekBar.isPressed) {
                    anim.alpha(0F).scaleX(0F).scaleY(0F).start()
                }
            }
        }
        val seekBarChangeListener = object: AutoSeekBar.OnProgressChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChange(view: AutoSeekBar, progress: Float) {
                floatText.text = "${abs(horizontalSeekBar.progress).format()}\n${abs(verticalSeekBar.progress).format()}"
                onLocationChange()
            }
        }
        verticalSeekBar.onTouchStateChangeListener = seekBarTouchListener
        horizontalSeekBar.onTouchStateChangeListener = seekBarTouchListener
        verticalSeekBar.onProgressChangeListener = seekBarChangeListener
        horizontalSeekBar.onProgressChangeListener = seekBarChangeListener
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
        expandBtnGroup.closeAll()
        showView(gridGroup, false)
        showView(horizontalSeekBar, false)
        showView(verticalSeekBar, false)
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
            (Gravity.LEFT or Gravity.TOP) -> leftTopGrid
            (Gravity.CENTER or Gravity.TOP) -> centerTopGrid
            (Gravity.RIGHT or Gravity.TOP) -> rightTopGrid
            (Gravity.LEFT or Gravity.CENTER) -> leftMiddleGrid
            (Gravity.CENTER) -> centerMiddleGrid
            (Gravity.RIGHT or Gravity.CENTER) -> rightMiddleGrid
            (Gravity.LEFT or Gravity.BOTTOM) -> leftBottomGrid
            (Gravity.BOTTOM or Gravity.CENTER) -> centerBottomGrid
            (Gravity.RIGHT or Gravity.BOTTOM) -> rightBottomGrid
            else -> null
        }
        checkedButtonHelper?.setChecked(btn, isChecked = true, cellListener = false)
    }

    @SuppressLint("RtlHardcoded")
    private fun onGravityChange(checkImageView: CheckImageView?) {
        targetGravity = when(checkImageView) {
            leftTopGrid -> {
                Gravity.LEFT or Gravity.TOP
            }
            centerTopGrid -> {
                Gravity.CENTER or Gravity.TOP
            }
            rightTopGrid -> {
                Gravity.RIGHT or Gravity.TOP
            }
            leftMiddleGrid -> {
                Gravity.LEFT or Gravity.CENTER
            }
            centerMiddleGrid -> {
                Gravity.CENTER
            }
            rightMiddleGrid -> {
                Gravity.RIGHT or Gravity.CENTER
            }
            leftBottomGrid -> {
                Gravity.LEFT or Gravity.BOTTOM
            }
            centerBottomGrid -> {
                Gravity.BOTTOM or Gravity.CENTER
            }
            rightBottomGrid -> {
                Gravity.RIGHT or Gravity.BOTTOM
            }
            else -> {
                Gravity.NO_GRAVITY
            }
        }
        verticalSeekBar.setProgress(0F, false)
        horizontalSeekBar.setProgress(0F, false)
    }

    private fun onTargetChange(view: ExpandButton) {
        selectedTarget = when (view) {
            titleModeBtn -> Target.Name
            prefixModeBtn -> Target.Prefix
            suffixModeBtn -> Target.Suffix
            daysModeBtn -> Target.Days
            unitModeBtn -> Target.Unit
            timeModeBtn -> Target.Time
            signModeBtn -> Target.Inscription
            else -> Target.Nothing
        }
        val info = locationInfoProvider?.getLocationInfo(selectedTarget)?:emptyInfo
        setCheckedByGravity(info.gravity)
        verticalSeekBar.setProgress(info.verticalMargin, false)
        Log.d("Lollipop", "info.verticalMargin: ${info.verticalMargin}, verticalSeekBar: ${verticalSeekBar.progress}")
        horizontalSeekBar.setProgress(info.horizontalMargin, false)
    }

    private fun onLocationChange() {
        onLocationChangeListener?.onLocationChange(
                selectedTarget, targetGravity,
                verticalSeekBar.progress, horizontalSeekBar.progress)
    }

    interface OnLocationChangeListener {
        fun onLocationChange(target: Target, gravity: Int, verticalMargin: Float, horizontalMargin: Float)
    }

    interface LocationInfoProvider {
        fun getLocationInfo(target: Target): WidgetBean.Location?
    }

    enum class Target(val value: Int) {
        /** 什么也没有 **/
        Nothing(-1),
        /** 名称 **/
        Name(0),
        /** 名称前缀 **/
        Prefix(1),
        /** 名称后缀 **/
        Suffix(2),
        /** 天数 **/
        Days(3),
        /** 天数的单位 **/
        Unit(4),
        /** 时间 **/
        Time(5),
        /** 签名 **/
        Inscription(6)
    }

}
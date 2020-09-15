package liang.lollipop.lcountdown.fragment.adjustment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.include_gravity.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.TextFocusProvider
import liang.lollipop.lcountdown.provider.TextLocationProvider
import liang.lollipop.lcountdown.util.GravityViewHelper

/**
 * @author lollipop
 * @date 7/30/20 23:42
 */
class LocationAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_location
    override val icon: Int
        get() = R.drawable.ic_baseline_format_shapes_24
    override val title: Int
        get() = R.string.title_location
    override val colorId: Int
        get() = R.color.focusLocationAdjust

    private val locationProvider = TextLocationProviderWrapper(null)
    private var onLocationChangeCallback: ((Int) -> Unit)? = null
    private var textFocusProvider: TextFocusProvider? = null

    private val focusIndex: Int
        get() {
            return textFocusProvider?.getSelectedIndex()?:-1
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GravityViewHelper(gridGroup)
                .viewToGravity(this::viewToGravity)
                .onGravityChange(this::onGravityChange)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            locationProvider.provider = context.getTextLocationProvider()
            onLocationChangeCallback = { index ->
                context.onTextLocationChange(index)
            }
        } else {
            parentFragment?.let { parent ->
                if (parent is Callback) {
                    locationProvider.provider = parent.getTextLocationProvider()
                    onLocationChangeCallback = { index ->
                        parent.onTextLocationChange(index)
                    }
                }
            }
        }
        if (context is TextFocusProvider) {
            textFocusProvider = context
        } else {
            parentFragment?.let { parent ->
                if (parent is TextFocusProvider) {
                    textFocusProvider = parent
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        locationProvider.provider = null
        onLocationChangeCallback = null
        textFocusProvider = null
    }

    override fun onResume() {
        super.onResume()
        parse()
    }

    private fun focusChange(index: Int) {
        textFocusProvider?.onTextSelected(index)
        // TODO
    }

    private fun onGravityChange(gravity: Int) {
        // TODO
        val index = focusIndex
        if (index >= 0) {
            onLocationChangeCallback?.invoke(index)
        }
    }

    private fun parse() {
        // TODO
    }

    @SuppressLint("RtlHardcoded")
    private fun viewToGravity(view: View): Int {
        return when(view) {
            leftTopGrid -> Gravity.LEFT or Gravity.TOP
            leftMiddleGrid -> Gravity.LEFT or Gravity.CENTER_VERTICAL
            leftBottomGrid -> Gravity.LEFT or Gravity.BOTTOM
            centerTopGrid -> Gravity.CENTER_HORIZONTAL or Gravity.TOP
            centerMiddleGrid -> Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
            centerBottomGrid -> Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            rightTopGrid -> Gravity.RIGHT or Gravity.TOP
            rightMiddleGrid -> Gravity.RIGHT or Gravity.CENTER_VERTICAL
            rightBottomGrid -> Gravity.RIGHT or Gravity.BOTTOM
            else -> 0
        }
    }

    interface Callback {
        fun getTextLocationProvider(): TextLocationProvider
        fun onTextLocationChange(index: Int)
    }

    private class TextLocationProviderWrapper(var provider: TextLocationProvider?): TextLocationProvider {
        override val textCount: Int
            get() {
                return provider?.textCount?:0
            }

        override fun getText(index: Int): String {
            return provider?.getText(index)?:""
        }

        override fun getGravity(index: Int): Int {
            return provider?.getGravity(index)?:0
        }

        override fun setGravity(index: Int, gravity: Int) {
            provider?.setGravity(index, gravity)
        }

        override fun getOffsetX(index: Int): Float {
            return provider?.getOffsetX(index)?:0F
        }

        override fun setOffsetX(index: Int, offset: Float) {
            provider?.setOffsetX(index, offset)
        }

        override fun getOffsetY(index: Int): Float {
            return provider?.getOffsetY(index)?:0F
        }

        override fun setOffsetY(index: Int, offset: Float) {
            provider?.setOffsetY(index, offset)
        }

    }

}


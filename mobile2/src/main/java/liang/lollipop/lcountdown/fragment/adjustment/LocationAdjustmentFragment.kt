package liang.lollipop.lcountdown.fragment.adjustment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_adjustment_location.*
import kotlinx.android.synthetic.main.include_gravity.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.WidgetPart
import liang.lollipop.lcountdown.provider.TextLocationProvider
import liang.lollipop.lcountdown.util.*

/**
 * @author lollipop
 * @date 7/30/20 23:42
 */
class LocationAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_location
    override val icon: Int
        get() = R.drawable.ic_baseline_format_shapes_24
    override val adjustmentPart: WidgetPart
        get() = WidgetPart.Location
    override val title: Int
        get() = R.string.title_location
    override val colorId: Int
        get() = R.color.focusLocationAdjust

    private val locationProvider = TextLocationProviderWrapper(null)
    private var gravityViewHelper: GravityViewHelper? = null
    private var textSelectHelper: TextSelectHelper? = null

    private val focusIndex: Int
        get() {
            return textSelectHelper?.getFocusIndex()?:-1
        }
    private var uploadLock = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gravityViewHelper = GravityViewHelper(gridGroup)
                .viewToGravity(this::viewToGravity)
                .onGravityChange(this::onGravityChange)

        locationYBar.setTheme(locationYBar.getColor(R.color.colorPrimary))
        locationYBar.onProgressChange { _, progress ->
            horizontalValueView.setText(progress.toInt().toString())
            onProgressChange(progress, false)
        }
        horizontalValueView.onActionDone {
            val value = (text?.toString() ?: "").tryInt(0).range(-1000, 1000)
            locationYBar.progress = value.toFloat()
            horizontalValueView.setText(value.toString())
        }

        locationXBar.setTheme(locationXBar.getColor(R.color.colorPrimary))
        locationXBar.onProgressChange { _, progress ->
            verticalValueView.setText(progress.toInt().toString())
            onProgressChange(progress, true)
        }
        verticalValueView.onActionDone {
            val value = (text?.toString() ?: "").tryInt(0).range(-1000, 1000)
            locationXBar.progress = value.toFloat()
            verticalValueView.setText(value.toString())
        }

        textSelectHelper = TextSelectHelper.with(textListView)
                .bindLifecycle(this)
                .textCount { locationProvider.textCount }
                .textValue { locationProvider.getText(it) }
                .onClicked { focusChange(it) }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachCallback<Callback>(context) {
            locationProvider.provider = it.getTextLocationProvider()
        }
    }

    override fun onDetach() {
        super.onDetach()
        locationProvider.provider = null
        textSelectHelper = null
    }

    override fun onResume() {
        super.onResume()
        parse()
    }

    private fun focusChange(index: Int) {
        parse()
    }

    override fun onWidgetInfoChange() {
        super.onWidgetInfoChange()
        parse()
    }

    private fun onGravityChange(gravity: Int) {
        if (uploadLock) {
            return
        }
        val index = focusIndex
        if (index >= 0) {
            locationProvider.setGravity(index, gravity)
            callChangeWidgetInfo()
        }
    }

    private fun onProgressChange(progress: Float, isX: Boolean) {
        if (uploadLock) {
            return
        }
        val index = focusIndex
        if (index >= 0) {
            if (isX) {
                locationProvider.setOffsetX(index, progress)
            } else {
                locationProvider.setOffsetY(index, progress)
            }
            callChangeWidgetInfo()
        }
    }

    private fun parse() {
        uploadLock = true
        val selectedIndex = focusIndex
        if (selectedIndex >= 0) {
            gravityViewHelper?.checkedGravity(locationProvider.getGravity(selectedIndex))
            locationXBar.progress = locationProvider.getOffsetX(selectedIndex)
            locationYBar.progress = locationProvider.getOffsetY(selectedIndex)
        } else {
            gravityViewHelper?.checkNone()
            locationXBar.progress = 0F
            locationYBar.progress = 0F
        }
        uploadLock = false
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

    interface Callback: CallChangeInfoCallback {
        fun getTextLocationProvider(): TextLocationProvider
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


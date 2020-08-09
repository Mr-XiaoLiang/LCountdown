package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Checkable
import liang.lollipop.lcountdown.drawable.GradientDrawable

/**
 * @author lollipop
 * @date 8/7/20 00:39
 * 形状的View
 */
class ShapeView(context: Context, attr: AttributeSet?,
                defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes), Checkable {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val gradientDrawable = GradientDrawable()

    private val grayColorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
        setSaturation(0F)
    })

    private var isChecked = false

    init {
        background = gradientDrawable
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onCheckedChange()
    }

    fun changeColor(vararg colors: Int) {
        gradientDrawable.changeColor(*colors)
    }

    fun changeColor(colors: List<Int>) {
        gradientDrawable.changeColor(colors)
    }

    fun changeStart(x: Float, y: Float) {
        gradientDrawable.changeStart(x, y)
    }

    fun changeEnd(x: Float, y: Float) {
        gradientDrawable.changeEnd(x, y)
    }

    fun setType(type: GradientDrawable.Type) {
        gradientDrawable.gradientType = type
    }

    fun setCorner(value: Float) {
        gradientDrawable.corner = value
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        isChecked = !isChecked
        onCheckedChange()
    }

    override fun setChecked(checked: Boolean) {
        isChecked = checked
        onCheckedChange()
    }

    private fun onCheckedChange() {
        gradientDrawable.colorFilter = if (isChecked) { null } else { grayColorFilter }
        invalidate()
    }

    override fun invalidate() {
        gradientDrawable.updateGradient()
        super.invalidate()
    }

}
package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import liang.lollipop.lcountdown.drawable.GradientDrawable

/**
 * @author lollipop
 * @date 8/7/20 00:39
 * 形状的View
 */
class ShapeView(context: Context, attr: AttributeSet?,
                defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val gradientDrawable = GradientDrawable()

    private var corner = 0F

    private val myOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            view?:return
            outline?:return
            outline.setRoundRect(view.left, view.top, view.right, view.bottom, corner)
        }
    }

    init {
        background = gradientDrawable
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

    override fun getOutlineProvider(): ViewOutlineProvider {
        return myOutlineProvider
    }

}
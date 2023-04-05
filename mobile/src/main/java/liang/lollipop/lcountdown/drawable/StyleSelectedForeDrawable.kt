package liang.lollipop.lcountdown.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import liang.lollipop.lcountdown.R

/**
 * @author Lollipop
 * 样式选择的前景Drawable
 */
class StyleSelectedForeDrawable(context: Context,private val bgDrawable: Drawable? = null): Drawable() {

    constructor(context: Context,bgId: Int): this(context,context.getDrawable(bgId))

    private val borderDrawable = context.getDrawable(R.drawable.fg_selected)

    private var isShow: Boolean = false

    override fun draw(canvas: Canvas) {
        bgDrawable?.draw(canvas)
        if(isShow){
            borderDrawable?.draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
        borderDrawable?.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        borderDrawable?.colorFilter = colorFilter
    }

    fun isShow(value: Boolean){
        isShow = value
        invalidateSelf()
    }

    override fun onBoundsChange(b: Rect) {
        super.onBoundsChange(b)
        bgDrawable?.bounds = bounds
        borderDrawable?.bounds = bounds
    }

}
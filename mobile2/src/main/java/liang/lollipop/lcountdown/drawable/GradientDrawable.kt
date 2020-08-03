package liang.lollipop.lcountdown.drawable

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * @author lollipop
 * @date 8/3/20 00:27
 * 渲染效果的Drawable
 */
class GradientDrawable: Drawable() {

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    var gradientType = Type.Linear
        set(value) {
            field = value
            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        TODO("Not yet implemented")
    }

    override fun setAlpha(alpha: Int) {
        TODO("Not yet implemented")
    }

    override fun getOpacity(): Int {
        TODO("Not yet implemented")
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    enum class Type {
        Linear,
        Sweep,
        Radial
    }

}
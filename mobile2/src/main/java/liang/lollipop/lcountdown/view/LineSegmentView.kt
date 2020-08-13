package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

/**
 * @author lollipop
 * @date 8/12/20 01:04
 * 线段的View
 */
class LineSegmentView(context: Context, attr: AttributeSet?,
                      defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private abstract class LineDrawable: Drawable() {
        var backgroundColor = Color.WHITE
        var lineColor = Color.BLACK
        var pointRadius = 10F

        abstract fun changePoint(startX: Float, startY: Float, endX: Float, endY: Float)
    }

    private class JellyDrawable: LineDrawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }
        private val linePath = Path()
        private val start = PointF()
        private val end = PointF()
        private val boundsF = RectF()

        override fun changePoint(startX: Float, startY: Float, endX: Float, endY: Float) {
            start.set(startX, startY)
            end.set(endX, endY)
            updatePath()
        }

        private fun updatePath() {
            val left = boundsF.left - pointRadius
            val top = boundsF.top - pointRadius
            val right = boundsF.right - pointRadius
            val bottom = boundsF.bottom - pointRadius
            val width = right - left
            val height = bottom - top
            val maxLength = sqrt(width.square() + height.square())
            val pointStartX = start.x * width
            val pointStartY = start.y * height
            val pointEndX = end.x * width
            val pointEndY = end.y * height
            val pointLength = sqrt(
                    (pointEndX - pointStartX).square() + (pointEndY - pointStartY).square())


        }

        private fun Float.square(): Double {
            return 1.0 * this * this
        }

        override fun draw(canvas: Canvas) {
            paint.color = backgroundColor
            canvas.drawRoundRect(boundsF, pointRadius, pointRadius, paint)
            paint.color = lineColor
            canvas.drawPath(linePath, paint)
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            boundsF.set(getBounds())
            updatePath()
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}
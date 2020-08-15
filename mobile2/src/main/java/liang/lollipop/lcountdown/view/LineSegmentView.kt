package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import liang.lollipop.lcountdown.util.toDip
import kotlin.math.asin
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
        var backgroundColor = Color.GREEN
        var lineColor = Color.BLACK
        var pointRadius = 10F

        abstract fun changePoint(startX: Float, startY: Float, endX: Float, endY: Float)
    }

    private val lineDrawable = JellyDrawable()

    init {
        background = lineDrawable
        lineDrawable.changePoint(0.5F, 0.5F, 0.5F, 0.5F)
        lineDrawable.pointRadius = 5F.toDip(this)
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
        private var rotate = 0F
        private val offset = PointF()

        override fun changePoint(startX: Float, startY: Float, endX: Float, endY: Float) {
            start.set(startX, startY)
            end.set(endX, endY)
            updatePath()
        }

        private fun updatePath() {
            if (bounds.isEmpty) {
                return
            }
            val left = boundsF.left + pointRadius
            val top = boundsF.top + pointRadius
            val right = boundsF.right - pointRadius
            val bottom = boundsF.bottom - pointRadius
            val width = right - left
            val height = bottom - top
            val maxLength = sqrt(width.square() + height.square())
            val pointStartX = start.x * width + left
            val pointStartY = start.y * height + top
            val pointEndX = end.x * width + left
            val pointEndY = end.y * height + top
            val pointLength = sqrt(
                    (pointEndX - pointStartX).square()
                            + (pointEndY - pointStartY).square()).let {
                if (it < 1) {
                    return@let 1F
                } else {
                    it.toFloat()
                }
            }

            val lineRadius = (pointRadius * (1 - (pointLength / maxLength))).let {
                if (it < 1) {
                    return@let 1F
                } else {
                    return@let it.toFloat()
                }
            }

            updatePath(pointLength, lineRadius)
            rotate = getRotate(pointStartX, pointStartY,
                    pointEndX, pointEndY, pointLength).toFloat() - 90F
            offset.set(pointStartX, pointStartY)
        }

        private fun updatePath(length: Float, lineRadius: Float) {
            val left = 0F
            val top = 0F
            val assist = pointRadius / 2
            val lineAssist = length * 0.05F
            val contentX = left + (length / 2)
            val endPointX = left + length
            linePath.reset()
            // 左侧顶点
            linePath.moveTo(left - pointRadius, top)
            // 上方
            linePath.cubicTo(left - pointRadius, top - assist,
                    left - assist, top - pointRadius,
                    left, top - pointRadius)
            // 连接到中点
            linePath.cubicTo(left + lineAssist, top - pointRadius,
                    contentX - lineAssist, top - lineRadius,
                    contentX, top - lineRadius)
            // 连接到右侧
            linePath.cubicTo(contentX + lineAssist, top - lineRadius,
                    endPointX - lineAssist, top - pointRadius,
                    endPointX, top - pointRadius)
            // 右侧顶点
            linePath.cubicTo(endPointX + assist, top - pointRadius,
                    endPointX + pointRadius, top - assist,
                    endPointX + pointRadius, top)
            // 右侧底部
            linePath.cubicTo(endPointX + pointRadius, top + assist,
                    endPointX + assist, top + pointRadius,
                    endPointX, top + pointRadius)
            // 连接到中点
            linePath.cubicTo(endPointX - lineAssist, top + pointRadius,
                    contentX + lineAssist, top + lineRadius,
                    contentX, top + lineRadius)
            // 连接到左侧
            linePath.cubicTo(contentX - lineAssist, top + lineRadius,
                    left + lineAssist, top + pointRadius,
                    left, top + pointRadius)
            // 连接回顶点
            linePath.cubicTo(left - assist, top + pointRadius,
                    left - pointRadius, top + assist,
                    left - pointRadius, top)
        }

        private fun getRotate(startX: Float, startY: Float,
                              endX: Float, endY:Float,
                              radius: Float): Double {
            val x = endX - startX
            val y = startY - endY
            var pa = asin(x / radius) / (2 * Math.PI) * 360
            // 度数矫正
            if (y < 0) {
                pa = if (x < 0) {
                    -pa - 180
                } else {
                    180 - pa
                }
            }
            if (pa < 0) {
                pa += 360.0
            }
            return pa
        }

        private fun Float.square(): Double {
            return 1.0 * this * this
        }

        override fun draw(canvas: Canvas) {
            paint.color = backgroundColor
            canvas.drawRoundRect(boundsF, pointRadius, pointRadius, paint)
            paint.color = lineColor
            canvas.save()
            canvas.translate(offset.x, offset.y)
            canvas.rotate(rotate)
            canvas.drawPath(linePath, paint)
            canvas.restore()
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
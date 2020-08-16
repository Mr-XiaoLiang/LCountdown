package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import liang.lollipop.lcountdown.util.range
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
        var backgroundColor = Color.WHITE
        var lineColor = Color.BLACK
        var pointRadius = 10F

        abstract fun changePoint(startX: Float, startY: Float, endX: Float, endY: Float)
    }

    companion object {
        private const val NONE = -1
        private const val START = 0
        private const val END = 1
    }

    private val lineDrawable = JellyDrawable()

    var pointRadius: Float
        get() = lineDrawable.pointRadius
        set(value) {
            lineDrawable.pointRadius = value
            if (touchRadius < value) {
                touchRadius = value
            }
        }
    var touchRadius = 10F

    var pointColor: Int
        set(value) {
            lineDrawable.lineColor = value
        }
        get() = lineDrawable.lineColor

    var color: Int
        set(value) {
            lineDrawable.backgroundColor = value
        }
        get() = lineDrawable.backgroundColor

    private var selectedPoint = NONE

    private var touchId = -1

    private val start = PointF()
    private val end = PointF()

    private val locationWeights = FloatArray(4)

    private var moveChangeListener: ((startX: Float, startY: Float,
                                      endX: Float, endY: Float) -> Unit)? = null

    init {
        background = lineDrawable
        pointRadius = 10F.toDip(this)
        setLocation(0.2F, 0.2F, 0.8F, 0.8F)
    }

    override fun setBackground(background: Drawable?) {
        if (background != lineDrawable) {
            throw RuntimeException("cant set background")
        }
        super.setBackground(background)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?:return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                selectedPoint = findPoint(event.getX(event.actionIndex), event.getY(event.actionIndex))
                if (selectedPoint == NONE) {
                    return false
                }
                touchId = event.getPointerId(event.actionIndex)
            }
            MotionEvent.ACTION_MOVE -> {
                if (selectedPoint == NONE) {
                    return false
                }
                onMove(event.focusX(), event.focusY())
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onMove(event.focusX(), event.focusY())
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onFocusChange(event)
                if (selectedPoint == NONE) {
                    return false
                }
                onMove(event.focusX(), event.focusY())
            }
        }
        return true
    }

    private fun onMove(x: Float, y: Float) {
        if (selectedPoint == NONE) {
            return
        }
        val pointF = if (selectedPoint == START) { start } else { end }
        pointF.set(x.range(pointRadius, width - pointRadius), y.range(pointRadius, height - pointRadius))
        onLocationChange(true)
    }

    fun setLocation(startX: Float, startY: Float, endX: Float, endY: Float) {
        locationWeights[0] = startX.range(0F, 1F)
        locationWeights[1] = startY.range(0F, 1F)
        locationWeights[2] = endX.range(0F, 1F)
        locationWeights[3] = endY.range(0F, 1F)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val maxWidth = width - (pointRadius * 2)
        val maxHeight = height - (pointRadius * 2)
        start.set(locationWeights[0] * maxWidth + pointRadius,
                locationWeights[1] * maxHeight + pointRadius)
        end.set(locationWeights[2] * maxWidth + pointRadius,
                locationWeights[3] * maxHeight + pointRadius)
        onLocationChange(false)
    }

    fun onMoved(listener: (startX: Float, startY: Float,
                          endX: Float, endY: Float) -> Unit) {
        this.moveChangeListener = listener
    }

    private fun onLocationChange(isUser: Boolean) {
        lineDrawable.changePoint(start.x, start.y, end.x, end.y)
        invalidate()
        val d = pointRadius * 2
        val contentWith = width - d
        val contentHeight = height - d
        val sX = (start.x - pointRadius) / contentWith
        val sY = (start.y - pointRadius) / contentHeight
        val eX = (end.x - pointRadius) / contentWith
        val eY = (end.y - pointRadius) / contentHeight
        locationWeights[0] = sX
        locationWeights[1] = sY
        locationWeights[2] = eX
        locationWeights[3] = eY
        if (isUser) {
            moveChangeListener?.invoke(sX, sY, eX, eY)
        }
    }

    private fun MotionEvent.focusX(): Float {
        var findPointerIndex = findPointerIndex(touchId)
        if (findPointerIndex < 0) {
            onFocusChange(this)
        }
        findPointerIndex = findPointerIndex(touchId)
        if (findPointerIndex < 0) {
            return this.x
        }
        return getX(findPointerIndex)
    }

    private fun MotionEvent.focusY(): Float {
        var findPointerIndex = findPointerIndex(touchId)
        if (findPointerIndex < 0) {
            onFocusChange(this)
        }
        findPointerIndex = findPointerIndex(touchId)
        if (findPointerIndex < 0) {
            return this.y
        }
        return getY(findPointerIndex)
    }

    private fun onFocusChange(event: MotionEvent) {
        touchId = event.getPointerId(0)
        selectedPoint = findPoint(event.getX(0), event.getY(0))
    }

    private fun findPoint(x: Float, y: Float): Int {
        if (pointInBounds(x, y, start)) {
            return START
        }
        if (pointInBounds(x, y, end)) {
            return END
        }
        return NONE
    }

    private fun pointInBounds(x: Float, y: Float, pointF: PointF): Boolean {
        return x > (pointF.x - touchRadius) && x < (pointF.x + touchRadius)
                && y > (pointF.y - touchRadius) && y < (pointF.y + touchRadius)
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
            val pointStartX = start.x
            val pointStartY = start.y
            val pointEndX = end.x
            val pointEndY = end.y
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
            val contentAssist = length * 0.2F
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
                    contentX - contentAssist, top - lineRadius,
                    contentX, top - lineRadius)
            // 连接到右侧
            linePath.cubicTo(contentX + contentAssist, top - lineRadius,
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
                    contentX + contentAssist, top + lineRadius,
                    contentX, top + lineRadius)
            // 连接到左侧
            linePath.cubicTo(contentX - contentAssist, top + lineRadius,
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
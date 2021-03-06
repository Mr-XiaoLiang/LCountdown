package liang.lollipop.lcountdown.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

/**
 * @author lollipop
 * @date 8/3/20 00:27
 * 渲染效果的Drawable
 */
class GradientDrawable: Drawable() {

    companion object {
        private const val TOLERANCE = 0.001F
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    var gradientType = Type.Linear
        set(value) {
            if (field == value) {
                return
            }
            field = value
            infoChanged = true
        }

    private var infoChanged = false

    private val startPoint = PointF()

    private val endPoint = PointF()

    private val colorArray = ArrayList<Int>()

    private val colorSize: Int
        get() {
            return colorArray.size
        }

    var path: Path? = null

    var corner = 0F

    private val tempRectF = RectF()

    fun changeColor(vararg colors: Int) {
        if (colors.size != colorArray.size) {
            colorArray.clear()
            colors.forEach {
                colorArray.add(it)
            }
            infoChanged = true
        } else {
            for (index in colors.indices) {
                if (colors[index] != colorArray[index]) {
                    colorArray[index] = colors[index]
                    infoChanged = true
                }
            }
        }
    }

    fun changeColor(colors: List<Int>) {
        if (colors.size != colorArray.size) {
            colorArray.clear()
            colors.forEach {
                colorArray.add(it)
            }
            infoChanged = true
        } else {
            for (index in colors.indices) {
                if (colors[index] != colorArray[index]) {
                    colorArray[index] = colors[index]
                    infoChanged = true
                }
            }
        }
    }

    fun changeStart(x: Float, y: Float) {
        changePoint(startPoint, x, y)
    }

    fun changeEnd(x: Float, y: Float) {
        changePoint(endPoint, x, y)
    }

    private fun changePoint(point: PointF, x: Float, y: Float) {
        if (abs(point.x - x) > TOLERANCE
                || abs(point.y - y) > TOLERANCE) {
            point.set(x, y)
            infoChanged = true
        }
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty) {
            return
        }
        val shape = path
        if (shape != null) {
            canvas.drawPath(shape, paint)
        } else {
            tempRectF.set(bounds)
            canvas.drawRoundRect(tempRectF, corner, corner, paint)
        }
    }

    fun setShape(path: Path?) {
        this.path = path
        invalidateSelf()
    }

    fun updateGradient() {
        if (infoChanged) {
            updateShader()
        }
        invalidateSelf()
    }

    private fun updateShader() {
        paint.shader = null
        if (bounds.isEmpty || colorSize < 2) {
            if (colorSize == 1) {
                paint.color = colorArray[0]
            } else {
                paint.color = Color.WHITE
            }
            return
        }
        val startX = startPoint.x * bounds.width() + bounds.left
        val startY = startPoint.y * bounds.height() + bounds.top
        val endX = endPoint.x * bounds.width() + bounds.left
        val endY = endPoint.y * bounds.height() + bounds.top
        val colors = IntArray(colorSize) { index -> colorArray[index] }
        val radius = sqrt(square(startX - endX) +
                square(startY - endY)).let {
            if (it.isNaN()) {
                1.0
            } else {
                it
            }
        }.toFloat()
        paint.shader = when (gradientType) {
            Type.Linear -> {
                LinearGradient(startX, startY, endX, endY, colors,
                        null, Shader.TileMode.CLAMP)
            }
            Type.Radial -> {
                RadialGradient(startX, startY, radius, colors, null, Shader.TileMode.CLAMP)
            }
            Type.Sweep -> {
                SweepGradient(startX, startY, colors, null).apply {
                    setLocalMatrix(Matrix().apply {
                        setRotate(getRotate(startX, startY, endX, endY, radius).toFloat() - 90,
                                bounds.exactCenterX(), bounds.exactCenterY())
                    })
                }
            }
        }
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

    private fun square(value: Float): Double {
        return 1.0 * value * value
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        updateShader()
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

    enum class Type {
        Linear,
        Sweep,
        Radial
    }

}
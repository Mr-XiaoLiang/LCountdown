package liang.lollipop.lcountdown.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import kotlin.math.asin
import kotlin.math.sqrt

/**
 * @author lollipop
 * @date 8/3/20 00:27
 * 渲染效果的Drawable
 */
class GradientDrawable: Drawable() {

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    var gradientType = Type.Linear
        set(value) {
            field = value
            updateGradient()
        }

    private val startPoint = PointF()

    private val endPoint = PointF()

    private val colorArray = ArrayList<Int>()

    val colorSize: Int
        get() {
            return colorArray.size
        }

    var path: Path? = null

    fun changeColor(vararg colors: Int) {
        colorArray.clear()
        colors.forEach {
            colorArray.add(it)
        }
        updateGradient()
    }

    fun changeColor(colors: List<Int>) {
        colorArray.clear()
        colorArray.addAll(colors)
        updateGradient()
    }

    fun changeStart(x: Float, y: Float) {
        startPoint.set(x, y)
    }

    fun changeEnd(x: Float, y: Float) {
        endPoint.set(x, y)
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty) {
            return
        }
        val shape = path
        if (shape != null) {
            canvas.drawPath(shape, paint)
        } else {
            canvas.drawRect(bounds, paint)
        }
    }

    fun setShape(path: Path) {
        this.path = path;
        invalidateSelf()
    }

    private fun updateGradient() {
        if (bounds.isEmpty) {
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
                        setRotate(getRotate(startX, startY, endX, endY, radius).toFloat(),
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
        updateGradient()
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
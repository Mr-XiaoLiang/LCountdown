package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 色相显示的View
 * @author Lollipop
 * @date 2019/09/05
 */
class HuePaletteView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private var hueCallback: ((hue: Int, isUser: Boolean) -> Unit)? = null
    private val hueDrawable = HuePaletteDrawable()

    init {
        setImageDrawable(hueDrawable)
    }

    fun onHueChange(run: (hue: Int, isUser: Boolean) -> Unit) {
        hueCallback = run
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event == null) {
            return super.onTouchEvent(event)
        }

        return when (event.action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                val hue = hueDrawable.selectTo(event.y)
                hueCallback?.invoke(hue, true)
                true
            }

            else -> {
                super.onTouchEvent(event)
            }

        }
    }

    fun parser(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        parser(hsv[0])
    }

    fun parser(hue: Float) {
        hueDrawable.parser(hue)
        hueCallback?.invoke(hue.toInt(), false)
    }

    class HuePaletteDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
        }
        private val hueColor = IntArray(361)
        private val linePaint = Paint().apply {
            isDither = true
            isAntiAlias = true
            color = Color.WHITE
        }
        private var selectHue = 0

        init {
            for ((count, i) in ((hueColor.size - 1) downTo 0).withIndex()) {
                hueColor[count] = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRect(bounds, paint)
            val selectY = getSelectY()
            canvas.drawLine(
                bounds.left.toFloat(), selectY,
                bounds.right.toFloat(), selectY, linePaint
            )
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            val hueShader = LinearGradient(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.left.toFloat(), bounds.bottom.toFloat(),
                hueColor, null, Shader.TileMode.CLAMP
            )

            paint.shader = hueShader
            invalidateSelf()
        }

        fun selectTo(y: Float): Int {
            //计算高度对应的色相值序号
            val values = y / bounds.height() * 361 + 0.5
            //得到色相值
            selectHue = hueColor.size - values.toInt()
            if (selectHue < 0) {
                selectHue = 0
            }
            if (selectHue > 360) {
                selectHue = 360
            }
            //发起重绘
            invalidateSelf()
            return selectHue
        }

        fun parser(hue: Float) {
            this.selectHue = (hue + 0.5F).toInt()
            invalidateSelf()
        }

        private fun getSelectY(): Float {
            return (1 - 1.0F * selectHue / hueColor.size) * bounds.height()
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun setColorFilter(filter: ColorFilter?) {
            paint.colorFilter = filter
        }
    }

}
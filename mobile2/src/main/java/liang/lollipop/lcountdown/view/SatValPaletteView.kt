package liang.lollipop.lcountdown.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by lollipop on 2018/1/23.
 * @author Lollipop
 * 饱和度和灰度的选择器
 */
class SatValPaletteView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val satValPaletteDrawable = SatValPaletteDrawable()
    private var hsvCallback: ((hsv: FloatArray, color: Int, isUser: Boolean) -> Unit)? = null

    init {
        setImageDrawable(satValPaletteDrawable)
        satValPaletteDrawable.setSelectRadius(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3F, context.resources.displayMetrics
            )
        )
    }

    fun onHSVChange(run: (hsv: FloatArray, color: Int, isUser: Boolean) -> Unit) {
        hsvCallback = run
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }

        return when (event.action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                val hsv = satValPaletteDrawable.selectTo(event.x, event.y)
                hsvCallback?.invoke(hsv, Color.HSVToColor(hsv), true)
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
        parser(hsv[1], hsv[2])
    }

    fun parser(satValue: Float, valValue: Float) {
        val hsv = satValPaletteDrawable.parser(satValue, valValue)
        hsvCallback?.invoke(hsv, Color.HSVToColor(hsv), false)
    }

    fun onHueChange(hue: Float) {
        satValPaletteDrawable.onHueChange(hue)
        val hsv = satValPaletteDrawable.lastSelected()
        hsvCallback?.invoke(hsv, Color.HSVToColor(hsv), true)
    }

    class SatValPaletteDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            color = Color.RED
        }
        private val pointPaint = Paint().apply {
            isDither = true
            isAntiAlias = true
            strokeWidth = 2F
            style = Paint.Style.STROKE
        }
        private var hue = 0F
        private var valShader: BitmapShader? = null
        private var selectRadius = 5F
        private var valBitmap: Bitmap? = null

        private var satValue = 0.5F
        private var valValue = 0.5F

        override fun draw(canvas: Canvas) {
            canvas.drawRect(bounds, paint)
            drawPoint(canvas)
        }

        private fun drawPoint(canvas: Canvas) {
            val loc = getPointLoc()

            pointPaint.color = Color.WHITE
            canvas.drawCircle(loc[0], loc[1], selectRadius, pointPaint)

            pointPaint.color = Color.BLACK
            val oval = RectF(
                loc[0] - selectRadius, loc[1] - selectRadius,
                loc[0] + selectRadius, loc[1] + selectRadius
            )
            canvas.drawArc(oval, 0F, 90F, false, pointPaint)
            canvas.drawArc(oval, 180F, 90F, false, pointPaint)
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            if (bounds == null) {
                return
            }
            createNewValShader()
            onHueChange(hue)
        }

        /**
         * 写法说明：
         * 因为设备开启了硬件加速以后，
         * ComposeShader无法合并两个相同类型的Shader（两个LinearGradient）
         * 因此，这里将变化不大的明度Shader转换为了BitmapShader
         * 并且在View尺寸发生变化的时候，回收Bitmap并且重新创建渲染
         */
        private fun createNewValShader() {
            if (bounds.width() < 1 || bounds.height() < 1) {
                return
            }
            if (valBitmap != null) {
                valBitmap?.recycle()
                valBitmap = null
            }
            val valLinearGradient = LinearGradient(
                bounds.left.toFloat(),
                bounds.top.toFloat(),
                bounds.left.toFloat(),
                bounds.bottom.toFloat(),
                0xFFFFFFFF.toInt(),
                0xFF000000.toInt(),
                Shader.TileMode.CLAMP
            )
            paint.shader = valLinearGradient
            valBitmap =
                Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(valBitmap!!)
            canvas.drawRect(bounds, paint)
            valShader = BitmapShader(valBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        fun onHueChange(hue: Float) {
            this.hue = hue
            val rgbColor = Color.HSVToColor(floatArrayOf(hue, 1F, 1F))
            val satShader = LinearGradient(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.right.toFloat(), bounds.top.toFloat(), 0xFFFFFFFF.toInt(), rgbColor,
                Shader.TileMode.CLAMP
            )

            if (valShader == null) {
                createNewValShader()
            }

            valShader?.let {
                val composeShader = ComposeShader(it, satShader, PorterDuff.Mode.MULTIPLY)
                paint.shader = composeShader
            }
            invalidateSelf()
        }

        fun lastSelected(): FloatArray {
            invalidateSelf()
            return floatArrayOf(hue, satValue, valValue)
        }

        fun selectTo(x: Float, y: Float): FloatArray {
            satValue = (x / bounds.width()).range(0F, 1F)
            valValue = (1 - y / bounds.height()).range(0F, 1F)

            invalidateSelf()
            return floatArrayOf(hue, satValue, valValue)
        }

        private fun getPointLoc(): FloatArray {
            val x = satValue * bounds.width()
            val y = (1 - valValue) * bounds.height()

            return floatArrayOf(x, y)
        }

        fun parser(satValue: Float, valValue: Float): FloatArray {
            this.satValue = satValue
            this.valValue = valValue
            return lastSelected()
        }

        fun setSelectRadius(radius: Float) {
            this.selectRadius = radius
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

        private fun Float.range(min: Float, max: Float): Float {
            if (this < min) {
                return min
            }
            if (this > max) {
                return max
            }
            return this
        }

    }

}
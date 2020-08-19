package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import liang.lollipop.lcountdown.util.log
import kotlin.math.min

/**
 * Created by lollipop on 2017/12/21.
 * 状态的显示View
 * @author Lollipop
 */
class CirclePointView(context: Context, attrs: AttributeSet?, defStyleAttr:Int)
    : AppCompatTextView(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private val backgroundDrawable = CircleBgDrawable().apply {
        setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
        setTextColor(textColors.defaultColor)
    }

    private var isInit = false

    init {
        if(background is ColorDrawable){
            backgroundDrawable.setColor((background as ColorDrawable).color)
        }
        background = backgroundDrawable
        isInit = true
    }

    var contextWeight: Float
        get() {
            return backgroundDrawable.contextWeight
        }
        set(value) {
            backgroundDrawable.contextWeight = value
        }

    fun setStatusColor(color:Int){
        backgroundDrawable.setColor(color)
    }

    override fun setBackgroundColor(color: Int) {
        setStatusColor(color)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        if (isInit) {
            backgroundDrawable.setTextColor(color)
        }
    }

    override fun setShadowLayer(radius: Float, dx: Float, dy: Float, color: Int) {
        super.setShadowLayer(radius, dx, dy, color)
        if (isInit) {
            backgroundDrawable.setShadowLayer(radius, dx, dy, color)
        }
    }

    class CircleBgDrawable: Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = 0xFFFF3C16.toInt()
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.BUTT
        }
        private val textPaint: Paint by lazy {
            Paint().apply {
                isAntiAlias = true
                isDither = true
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
            }
        }
        private val bound: RectF = RectF()
        private var biggestCorners = true
        private var corners = 0F

        private var textY = 0F

        var text = ""
            private set

        var contextWeight = 0.7F

        fun setTextColor(color: Int) {
            textPaint.color = color
        }

        fun setShadowLayer(radius: Float, dx: Float, dy: Float, color: Int) {
            textPaint.setShadowLayer(radius, dx, dy, color)
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(bound, corners, corners, paint)
            canvas.drawText(text, bound.centerX(), textY, textPaint)
        }

        override fun setAlpha(i: Int) {
            paint.alpha = i
            textPaint.alpha = i
            invalidateSelf()
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
            textPaint.colorFilter = colorFilter
            invalidateSelf()
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            bound.set(bounds)
            if (biggestCorners) {
                corners = min(bounds.width() * 0.5F, bounds.height() * 0.5F)
            }
            checkTextSize()
            invalidateSelf()
        }

        fun setColor(color: Int) {
            paint.color = color
            invalidateSelf()
        }

        fun setText(value: String) {
            text = value
            checkTextSize()
        }

        private val width: Int
            get() = bounds.width()

        private val height: Int
            get() = bounds.height()

        private fun checkTextSize() {
            if (text.isNotEmpty() && width > 0 && height > 0) {
                val contentWidth = width * contextWeight
                log("checkTextSize: width=$width, contextWeight=$contextWeight")
                val fontSize = refitText(contentWidth, text)
                log("checkTextSize: value=$text, fontSize=$fontSize")
                val contentHeight = height * contextWeight
                textPaint.textSize = min(fontSize, contentHeight)

                val fm = textPaint.fontMetrics
                textY = (fm.descent - fm.ascent) / 2 - fm.descent + bound.centerY()
            }
            invalidateSelf()
        }

        private fun refitText(targetWidth: Float, text: String): Float {
            log("refitText: targetWidth=$targetWidth, text=$text")
            if (text.isEmpty() || targetWidth < 1) {
                return 1F
            }
            val threshold = 2f
            val textPaint = textPaint
            var preferredTextSize = targetWidth
            var minTextSize = 1F
            while (preferredTextSize - minTextSize > threshold) {
                val size = (preferredTextSize + minTextSize) / 2
                textPaint.textSize = size
                if (textPaint.measureText(text) >= targetWidth) {
                    // too big
                    preferredTextSize = size
                } else {
                    // too small
                    minTextSize = size
                }
            }
            return minTextSize
        }

    }

     var autoText: String
        get() {
            return backgroundDrawable.text
        }
        set(value) {
            backgroundDrawable.setText(value)
        }

}
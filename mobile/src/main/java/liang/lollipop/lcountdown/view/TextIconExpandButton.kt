package liang.lollipop.lcountdown.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import liang.lollipop.lcountdown.R

/**
 * @date: 2019-06-25 21:48
 * @author: lollipop
 * 可以在手指放上去的时候展开的按钮
 */
class TextIconExpandButton @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null
) : ExpandButton(context, attr),
    ValueAnimator.AnimatorUpdateListener {

    companion object {
        private const val DEFAULT_ICON_TEXT_SIZE = 24F
    }

    private val textDrawable = TextDrawable(sp(DEFAULT_ICON_TEXT_SIZE).toInt())

    var iconTextSize: Int
        set(value) {
            textDrawable.textSize = value
        }
        get() {
            return textDrawable.textSize
        }

    var iconText: String
        set(value) {
            textDrawable.text = value
        }
        get() {
            return textDrawable.text
        }

    private fun sp(value: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics
    )

    init {
        if (attr != null) {
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.TextIconExpandButton)
            iconTextSize = typedArray.getDimensionPixelSize(
                R.styleable.TextIconExpandButton_iconTextSize,
                sp(DEFAULT_ICON_TEXT_SIZE).toInt()
            )
            val t = typedArray.getString(R.styleable.TextIconExpandButton_iconText) ?: ""
            iconText = if (TextUtils.isEmpty(t)) {
                if (TextUtils.isEmpty(text)) {
                    ""
                } else {
                    text.substring(0, 1)
                }
            } else {
                t
            }
            typedArray.recycle()
        }
        icon = textDrawable
    }

    private class TextDrawable(size: Int) : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            textAlign = Paint.Align.CENTER
        }

        var text: String = ""
            set(value) {
                field = value
                invalidateSelf()
            }

        var textSize = size
            set(value) {
                field = value
                invalidateSelf()
            }

        override fun draw(canvas: Canvas) {
            drawText(canvas, bounds.exactCenterX(), bounds.exactCenterY())
        }

        private fun drawText(canvas: Canvas, x: Float, y: Float) {
            if (TextUtils.isEmpty(text)) {
                return
            }
            paint.textSize = textSize.toFloat()
            val fm = paint.fontMetrics
            val textY = y - fm.descent + (fm.descent - fm.ascent) / 2
            canvas.drawText(text, x, textY, paint)
        }

        override fun setTintList(tint: ColorStateList?) {
            super.setTintList(tint)
            paint.color = tint?.defaultColor ?: Color.BLACK
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity() = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}
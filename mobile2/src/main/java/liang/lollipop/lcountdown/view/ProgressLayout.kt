package liang.lollipop.lcountdown.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020/6/14 23:19
 * 带有进度的Layout
 */
class ProgressLayout(context: Context, attributeSet: AttributeSet?, styleId: Int):
        FrameLayout(context, attributeSet, styleId),
        Animator.AnimatorListener,
        ValueAnimator.AnimatorUpdateListener {

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)

    constructor(context: Context): this(context, null)

    private val progressDrawable = ProgressDrawable()

    private val valueAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            addListener(this@ProgressLayout)
            addUpdateListener(this@ProgressLayout)
        }
    }

    init {
        progressDrawable.callback = this
        if (isInEditMode) {
            progressDrawable.progress = 0.5F
        }
    }

    var strokeWidth: Float
        set(value) {
            progressDrawable.strokeWidth = value
        }
        get() = progressDrawable.strokeWidth

    var unselected: Int
        get() = progressDrawable.unselected
        set(value) {
            progressDrawable.unselected = value
        }

    var selected = Color.BLACK

    var progress: Float
        set(value) {
            progressDrawable.progress = value
        }
        get() {
            return progressDrawable.progress
        }

    var radius: Int
        set(value) {
            progressDrawable.radius = value
        }
        get() {
            return progressDrawable.radius
        }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        progressDrawable.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == progressDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            progressDrawable.draw(it)
        }
    }

    private class ProgressDrawable: Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.STROKE
        }

        var strokeWidth: Float
            set(value) {
                paint.strokeWidth = value
                updatePath()
            }
            get() = paint.strokeWidth

        var unselected = Color.GRAY

        var selected = Color.BLACK

        var progress = 0F
            set(value) {
                field = value
                updateProgress()
            }

        var radius: Int = -1
            set(value) {
                field = value
                updatePath()
            }

        private val path = Path()

        private val dstPath = Path()

        private val pathMeasure = PathMeasure()

        override fun draw(canvas: Canvas) {
            paint.color = unselected
            canvas.drawPath(path, paint)
            paint.color = selected
            canvas.drawPath(dstPath, paint)
        }

        override fun onBoundsChange(bounds: Rect?) {
            super.onBoundsChange(bounds)
            updatePath()
        }

        private fun updatePath() {
            path.reset()
            if (bounds.isEmpty) {
                return
            }
            val padding = strokeWidth / 2
            val r = if (radius < 0) {
                min(bounds.width(), bounds.height()) / 2
            } else {
                radius
            }.toFloat()
            path.addRoundRect(
                    bounds.left + padding,
                    bounds.top + padding,
                    bounds.right - padding,
                    bounds.bottom - padding,
                    r,
                    r,
                    Path.Direction.CW
            )
            pathMeasure.setPath(path, false)
            updateProgress()
        }

        private fun updateProgress() {
            dstPath.reset()
            val length = pathMeasure.length
            pathMeasure.getSegment(0F, length * progress, dstPath, true)
            invalidateSelf()
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

    override fun onAnimationRepeat(animation: Animator?) {
        TODO("Not yet implemented")
    }

    override fun onAnimationEnd(animation: Animator?) {
        TODO("Not yet implemented")
    }

    override fun onAnimationCancel(animation: Animator?) {
        TODO("Not yet implemented")
    }

    override fun onAnimationStart(animation: Animator?) {
        TODO("Not yet implemented")
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        TODO("Not yet implemented")
    }

}
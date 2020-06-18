package liang.lollipop.lcountdown.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020/6/14 23:19
 * 带有进度的Layout
 */
class ProgressLayout(context: Context, attributeSet: AttributeSet?, styleId: Int):
        FrameLayout(context, attributeSet, styleId),
        ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener{

    companion object {
        const val ANIMATION_DURATION = 3000L
    }

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)

    constructor(context: Context): this(context, null)

    private val progressDrawable = ProgressDrawable()

    private val valueAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            addListener(this@ProgressLayout)
            addUpdateListener(this@ProgressLayout)
            interpolator = LinearInterpolator()
        }
    }

    var onLoading = false
        private set

    private var pendingToLoading = false

    init {
        progressDrawable.callback = this
        if (isInEditMode) {
            progressDrawable.update(0F, 0.5F)
        }
    }

    fun startLoad() {
        if (!onLoading && valueAnimator.isRunning) {
            pendingToLoading = true
            return
        }
        doLoadingAnimation()
    }

    fun loadSuccess() {
        doSuccessAnimation()
    }

    fun loadFailure() {
        doFailureAnimation()
    }

    private fun doLoadingAnimation() {
        onLoading = true
        valueAnimator.cancel()
        valueAnimator.duration = ANIMATION_DURATION * 2
        valueAnimator.setFloatValues(ProgressDrawable.MIN, ProgressDrawable.MAX +
                ProgressDrawable.LENGTH)
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.start()
    }

    private fun doSuccessAnimation() {
        onLoading = false
        valueAnimator.cancel()
        val pro = progress
        valueAnimator.duration = (ANIMATION_DURATION *
                (ProgressDrawable.MAX - pro) / ProgressDrawable.LENGTH).toLong()
        valueAnimator.setFloatValues(pro, ProgressDrawable.MAX)
        valueAnimator.repeatCount = 0
        valueAnimator.start()
    }

    private fun doFailureAnimation() {
        onLoading = false
        valueAnimator.cancel()
        val pro = progress
        valueAnimator.duration = (ANIMATION_DURATION *
                (pro - ProgressDrawable.MIN) / ProgressDrawable.LENGTH).toLong()
        valueAnimator.setFloatValues(pro, ProgressDrawable.MIN)
        valueAnimator.repeatCount = 0
        valueAnimator.start()
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

    var selected: Int
        get() = progressDrawable.selected
        set(value) {
            progressDrawable.selected = value
        }

    var progress: Float
        set(value) {
            progressDrawable.update(0F, value)
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

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let {
            progressDrawable.draw(it)
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation == valueAnimator) {
            val p = animation.animatedValue as Float
            val progress = if (p > ProgressDrawable.MAX) {
                abs(p - ProgressDrawable.MAX * 2)
            } else {
                p
            }
            val offset = if (onLoading) {
                p / (ProgressDrawable.LENGTH * 2)
            } else {
                progressDrawable.offset
            }
            progressDrawable.update(offset, progress)
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {  }

    override fun onAnimationEnd(animation: Animator?) {
        if (pendingToLoading) {
            pendingToLoading = false
            startLoad()
        }
    }

    override fun onAnimationCancel(animation: Animator?) { }

    override fun onAnimationStart(animation: Animator?) { }

    private class ProgressDrawable: Drawable() {

        companion object {
            const val MIN = 0F
            const val MAX = 1F
            const val LENGTH = MAX - MIN
        }

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
            private set

        var offset = 0F
            private set

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

        fun update(offset: Float = 0F, progress: Float) {
            this.offset = offset
            this.progress = progress
            updateProgress()
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
            val start = length * offset
            val end = length * progress + start
            if (end <= length) {
                pathMeasure.getSegment(start, end, dstPath, true)
            } else {
                pathMeasure.getSegment(start, length, dstPath, true)
                pathMeasure.getSegment(0F, end - length, dstPath, true)
            }
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

}
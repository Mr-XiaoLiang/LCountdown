package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import liang.lollipop.lcountdown.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @date: 2019-06-29 16:02
 * @author: lollipop
 * 自动确定方向的 SeekBar
 */
class AutoSeekBar(context: Context, attr: AttributeSet?,
                  defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val seekBarDrawable = SeekBarDrawable()

    private val isRtl = resources.configuration.layoutDirection == LAYOUT_DIRECTION_RTL

    /**
     * 最小有效滑动阈值
     */
    private val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * 是否在滚动容器中
     */
    var isInScrollingContainer = false

    /**
     * 手指按下的位置
     */
    private val touchDown = PointF()

    /**
     * 是否是垂直的
     */
    private var isVertical = false

    /**
     * 激活的
     */
    private var activeId = 0

    /**
     * 是否正在拖拽
     */
    private var isDragging = false

    var barColor: Int
        set(value) {
            seekBarDrawable.defaultColor = value
        }
        get() {
            return seekBarDrawable.defaultColor
        }

    var selectedBarColor: Int
        set(value) {
            seekBarDrawable.selectedColor = value
        }
        get() = seekBarDrawable.selectedColor

    var max: Float
        set(value) {
            seekBarDrawable.max = value.isRtl()
        }
        get() = seekBarDrawable.max.isRtl()

    var min: Float
        set(value) {
            seekBarDrawable.min = value.isRtl()
        }
        get() = seekBarDrawable.min.isRtl()

    var progress: Float
        set(value) {
            setProgress(value, true)
        }
        get() = seekBarDrawable.progress.isRtl()

    var onProgressChangeListener: OnProgressChangeListener? = null

    var onTouchStateChangeListener: OnTouchStateChangeListener? = null

    init {
        seekBarDrawable.callback = this
        if (attr != null) {
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.AutoSeekBar)
            barColor = typedArray.getColor(R.styleable.AutoSeekBar_barColor, Color.GRAY)
            selectedBarColor = typedArray.getColor(R.styleable.AutoSeekBar_selectedBarColor, Color.BLACK)
            max = typedArray.getFloat(R.styleable.AutoSeekBar_max, 100F)
            min = typedArray.getFloat(R.styleable.AutoSeekBar_min, 0F)
            progress = typedArray.getFloat(R.styleable.AutoSeekBar_progress, 0F)
            typedArray.recycle()
        }
    }

    fun setProgress(value: Float, callListener: Boolean = true) {
        seekBarDrawable.progress = value.isRtl()
        if (callListener) {
            onProgressChangeListener?.onProgressChange(this, value)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        seekBarDrawable.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                activeId = event.getPointerId(0)
                if (isInScrollingContainer) {
                    touchDown.set(event.getXById(), event.getYById())
                } else {
                    startDrag(event)
                }
            }

            MotionEvent.ACTION_MOVE -> if (isDragging) {
                trackTouchEvent(event)
            } else {
                if (isVertical) {
                    val y = event.getYById()
                    if (abs(y - touchDown.y) > scaledTouchSlop) {
                        startDrag(event)
                    }
                } else {
                    val x = event.getXById()
                    if (abs(x - touchDown.x) > scaledTouchSlop) {
                        startDrag(event)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activeId) {
                    // 当活跃的那个指头抬起，那么重新选定一个指头作为事件来源
                    val newPointerIndex = if (pointerIndex == 0) {1} else {0}
                    touchDown.set(event.getX(newPointerIndex), event.getY(newPointerIndex))
                    activeId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        onTouchStateChangeListener?.onTouchStateChange(this, pressed)
    }

    private fun startDrag(event: MotionEvent) {
        isPressed = true
        onStartTrackingTouch()
        trackTouchEvent(event)
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    private fun onStartTrackingTouch() {
        isDragging = true
    }

    private fun onStopTrackingTouch() {
        isDragging = false
    }

    private fun trackTouchEvent(event: MotionEvent) {
        val origin: Float
        val point: Float
        val start: Float
        val end: Float
        val allLength: Float
        if (isVertical) {
            // 如果是垂直的，那么就计算高度
            start = paddingTop.toFloat()
            end = (height - paddingBottom).toFloat()
            allLength = end - start
            origin = allLength * seekBarDrawable.startWeight + paddingTop
            point = event.getYById().range(start, end)
        } else {
            // 如果是水平的，那么计算宽度
            start = paddingLeft.toFloat()
            end = (width - paddingRight).toFloat()
            allLength = end - start
            origin = allLength * seekBarDrawable.startWeight + paddingLeft
            point =  event.getXById().range(start, end)
        }
        progress = if (point < origin) {
            // 如果在小于原点，那么说明是负数
            // 那么需要计算选中长度与原点左侧长度的比值，再乘以原点左侧的负数数值
            (origin - point) / (origin - start) * min
        } else {
            // 如果大于或者等于原点，那么肯定是正数
            // 并且是在正数区，需要用结束位置减去起点位置
            (point - origin) / (end - origin) * (max - max(0F, min))
        }
        invalidate()
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        isVertical = (right - left) < (bottom - top)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        seekBarDrawable.setBounds(left, top, right, bottom)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == seekBarDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    private class SeekBarDrawable : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        var defaultColor = Color.GRAY
            set(value) {
                field = value
                invalidateSelf()
            }

        var selectedColor = Color.GRAY
            set(value) {
                field = value
                invalidateSelf()
            }

        var max = 100F
            set(value) {
                field = value
                onProgressChange()
            }

        var min = 0F
            set(value) {
                field = value
                onProgressChange()
            }

        var progress = 0F
            set(value) {
                field = value
                onProgressChange()
            }

        private val round: Float
            get() {
                return min(bounds.width(), bounds.height()) * 0.5F
            }

        val startWeight: Float
            get() {
                return if (min < 0 && max > 0) {
                    // 如果途径了 0，那么以 0 作为起点
                    (0 - min) / (max - min)
                } else {
                    0F
                }
            }

        private val drawBounds = RectF()

        private val borderBounds = RectF()

        override fun draw(canvas: Canvas) {
            val r = round
            paint.color = defaultColor
            canvas.drawRoundRect(borderBounds, r, r, paint)
            paint.color = selectedColor
            canvas.drawRoundRect(drawBounds, r, r, paint)
        }

        private fun onProgressChange() {
            borderBounds.set(bounds)

            val allProgress = max - min
            val progressWeight = progress / allProgress
            val start = if (progressWeight < 0) {
                startWeight + progressWeight
            } else {
                startWeight
            }

            val length = abs(progressWeight)

            val r = round

            val width = bounds.width() - r * 2
            val height = bounds.height() - r * 2
            if (width > height) {
                // 宽度大于高度，视为横向
                val left = bounds.left + start * width
                drawBounds.set(left, bounds.top.toFloat(), length * width + left, bounds.bottom.toFloat())
                drawBounds.offset(r, 0F)
                drawBounds.left -= r
                drawBounds.right += r
            } else {
                // 否则视为纵向
                val top = bounds.top + start * height
                drawBounds.set(bounds.left.toFloat(), top, bounds.right.toFloat(), height * length + top)
                drawBounds.offset(0F, r)
                drawBounds.top -= r
                drawBounds.bottom += r
            }
            invalidateSelf()
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            onProgressChange()
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity() = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }
    }

    interface OnProgressChangeListener {
        fun onProgressChange(view: AutoSeekBar, progress: Float)
    }

    interface OnTouchStateChangeListener {
        fun onTouchStateChange(view: AutoSeekBar, isTouch: Boolean)
    }

    private fun Float.isRtl(): Float {
        return if (isRtl) { this * -1 } else { this }
    }

    private fun MotionEvent.getXById(): Float {
        return this.getX(this.findPointerIndex(activeId))
    }

    private fun MotionEvent.getYById(): Float {
        return this.getY(this.findPointerIndex(activeId))
    }

}
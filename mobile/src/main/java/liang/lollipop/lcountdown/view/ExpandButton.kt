package liang.lollipop.lcountdown.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import liang.lollipop.lcountdown.R
import kotlin.math.max
import kotlin.math.min

/**
 * @date: 2019-06-25 21:48
 * @author: lollipop
 * 可以在手指放上去的时候展开的按钮
 */
open class ExpandButton @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    ViewGroup(context, attr),
    ValueAnimator.AnimatorUpdateListener {

    companion object {
        private const val DEF_ICON_SIZE = 24F
        private const val DEF_ICON_INTERVAL = 10F

        private const val MAX_ANIMATOR_PROGRESS = 1F
        private const val MIN_ANIMATOR_PROGRESS = 0F
        private const val DEF_ANIMATOR_DURATION = 150L

        private const val DELAY_EXPAND = DEF_ANIMATOR_DURATION
    }

    /**
     * 按钮图标
     */
    private val iconView = PressedImageView(context)

    /**
     * 按钮名称
     */
    private val nameView = TextView(context)

    /**
     * 可以展开的背景
     */
    private val expandBackground = ExpandBackground()

    /**
     * 动画控制器
     */
    private val valueAnimator = ValueAnimator().apply {
        addUpdateListener(this@ExpandButton)
    }

    /**
     * 文字和图标之间的间隔
     */
    var interval = dp(DEF_ICON_INTERVAL).toInt()
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * icon 的摆放方式
     */
    var iconGravity = IconGravity.CENTER
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * icon 的尺寸
     */
    var iconWidth = dp(DEF_ICON_SIZE).toInt()
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * icon 的尺寸
     */
    var iconHeight = dp(DEF_ICON_SIZE).toInt()
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * 文字的尺寸
     */
    var textSize: Float
        set(value) {
            nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }
        get() {
            return nameView.textSize
        }

    /**
     * 按钮内容
     */
    var text: CharSequence
        set(value) {
            nameView.text = value
        }
        get() {
            return nameView.text
        }

    /**
     * 文字的颜色
     */
    var textColor: Int
        set(value) = nameView.setTextColor(value)
        get() = nameView.textColors.defaultColor

    /**
     * icon 的颜色
     */
    var iconColor: ColorStateList = ColorStateList.valueOf(Color.WHITE)
        set(value) {
            field = value
            onIconChange()
        }

    /**
     * icon 图标
     */
    var icon: Drawable? = null
        set(value) {
            field = value
            onIconChange()
        }

    /**
     * icon 的缩放模式
     */
    var scaleType: ImageView.ScaleType
        set(value) {
            iconView.scaleType = value
        }
        get() = iconView.scaleType

    /**
     * 背景颜色
     */
    var color: Int
        set(value) {
            expandBackground.color = value
        }
        get() = expandBackground.color

    /**
     * 圆角尺寸
     */
    var corner: Float
        set(value) {
            expandBackground.corner = value
        }
        get() = expandBackground.corner

    /**
     * 动画时间
     */
    var animatorDuration = DEF_ANIMATOR_DURATION

    /**
     * 精细调整文字描述的 View
     */
    fun adjustNameView(run: (TextView) -> Unit) {
        run(nameView)
    }

    /**
     * 精细调整图标的 View
     */
    fun adjustIconView(run: (ImageView) -> Unit) {
        run(iconView)
    }

    /**
     * 最小的尺寸
     */
    private val minSize: Int
        get() {
            return paddingLeft + paddingRight + iconWidth
        }

    /**
     * 是否已经展开的状态监测
     */
    private var isExpand = false

    /**
     * 动画的进度
     * 0：收起状态
     * 1：展开状态
     */
    private var animatorProgress = 0F

    /**
     * 展开面板的动画
     */
    private val expandRunnable = Runnable { expand() }

    /**
     * 按钮的点击事件
     */
    private var onClickListener: OnClickListener? = null

    /**
     * 当展开时，驳回点击事件
     */
    var beakOnExpand = true

    /**
     * 展开状态监听器
     */
    var onExpendListener: OnExpendListener? = null

    init {
        addView(iconView)
        addView(nameView)
        iconView.onPressed {
            if (it) {
                postDelayed(expandRunnable, DELAY_EXPAND)
            } else {
                collapse()
            }
        }
        iconView.setOnClickListener {
            if (!isExpand || !beakOnExpand) {
                onClickListener?.onClick(this)
            }
        }
        expandBackground.callback = this
        if (attr != null) {
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.ExpandButton)
            val iconId = typedArray.getResourceId(R.styleable.ExpandButton_icon, 0)
            if (iconId != 0) {
                icon = resources.getDrawable(iconId, context.theme)
            }
            val iconSize = typedArray.getDimensionPixelSize(
                R.styleable.ExpandButton_iconSize, dp(DEF_ICON_SIZE).toInt()
            )
            iconWidth = iconSize
            iconHeight = iconSize
            iconColor = ColorStateList.valueOf(
                typedArray.getColor(
                    R.styleable.ExpandButton_iconColor,
                    Color.WHITE
                )
            )
            text = typedArray.getText(R.styleable.ExpandButton_text) ?: ""
            textSize =
                typedArray.getDimensionPixelSize(R.styleable.ExpandButton_textSize, sp(16F).toInt())
                    .toFloat()
            textColor = typedArray.getColor(R.styleable.ExpandButton_textColor, Color.WHITE)
            color = typedArray.getColor(R.styleable.ExpandButton_btnColor, Color.BLUE)
            val gravity =
                typedArray.getInt(R.styleable.ExpandButton_exIconGravity, IconGravity.CENTER.value)
            iconGravity = when (gravity) {
                IconGravity.TOP.value -> IconGravity.TOP
                IconGravity.BOTTOM.value -> IconGravity.BOTTOM
                else -> IconGravity.CENTER
            }
            corner = typedArray.getDimension(R.styleable.ExpandButton_corner, 0F)
            interval = typedArray.getDimension(R.styleable.ExpandButton_interval, 0F).toInt()
            typedArray.recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        onClickListener = l
    }

    private fun onIconChange() {
        iconView.setImageDrawable(icon)
        iconView.imageTintList = iconColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        iconView.measure(
            MeasureSpec.makeMeasureSpec(iconWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(iconHeight, MeasureSpec.EXACTLY)
        )

        val nameWidth = widthSize - paddingLeft - paddingRight - iconWidth
        nameView.measure(
            MeasureSpec.makeMeasureSpec(nameWidth, widthMode),
            MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        )

        var measuredWidth =
            iconWidth + interval + nameView.measuredWidth + paddingLeft + paddingRight
        var measuredHeight = max(iconHeight, nameView.measuredHeight) + paddingTop + paddingBottom
        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = min(measuredWidth, widthSize)
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = min(measuredHeight, heightSize)
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingLeft
        val top = paddingTop
        val height = b - t

        val effectiveHeight = height - paddingBottom - paddingTop
        val effectiveWidth = r - l - paddingLeft - paddingRight
        val iconTop = when (iconGravity) {
            IconGravity.TOP -> {
                top
            }

            IconGravity.BOTTOM -> {
                height - paddingBottom - iconHeight
            }

            else -> {
                (effectiveHeight - iconHeight) / 2 + top
            }
        }
        iconView.layout(left, iconTop, left + iconWidth, iconTop + iconHeight)

        val nameTop = if (effectiveHeight > nameView.measuredHeight) {
            (effectiveHeight - nameView.measuredHeight) / 2 + top
        } else {
            top
        }
        val nameLeft = left + iconWidth + interval
        val nameMaxWidth = effectiveWidth - iconWidth - interval
        val nameWidth = min(nameView.measuredWidth, nameMaxWidth)
        val nameHeight = min(effectiveHeight, nameView.measuredHeight)
        nameView.layout(nameLeft, nameTop, nameLeft + nameWidth, nameTop + nameHeight)

        setTouchDelegate()
        onProgressChange(MIN_ANIMATOR_PROGRESS)
    }

    private fun setTouchDelegate() {
        val view = iconView
        val parent = view.parent as? View ?: return
        val bounds = Rect()
        view.isEnabled = true
        view.getHitRect(bounds)
        bounds.left -= parent.paddingLeft
        bounds.top -= view.top
        bounds.right += parent.paddingRight
        bounds.bottom += view.bottom
        parent.touchDelegate = TouchDelegate(bounds, view)
    }

    fun expand() {
        isExpand = true
        onExpendListener?.onExpend(this, isExpand)
        startAnimator(
            ((MAX_ANIMATOR_PROGRESS - animatorProgress) *
                    1F / (MAX_ANIMATOR_PROGRESS - MIN_ANIMATOR_PROGRESS) * animatorDuration).toLong(),
            MAX_ANIMATOR_PROGRESS
        )
    }

    fun collapse(isAnimator: Boolean = true) {
        if (!isExpand) {
            removeCallbacks(expandRunnable)
            return
        }
        isExpand = false
        onExpendListener?.onExpend(this, isExpand)
        if (isAnimator) {
            startAnimator(
                ((animatorProgress - MIN_ANIMATOR_PROGRESS) *
                        1F / (MAX_ANIMATOR_PROGRESS - MIN_ANIMATOR_PROGRESS) * animatorDuration).toLong(),
                MIN_ANIMATOR_PROGRESS
            )
        } else {
            valueAnimator.cancel()
            onProgressChange(MIN_ANIMATOR_PROGRESS)
        }
    }

    private fun startAnimator(duration: Long, endValue: Float) {
        valueAnimator.cancel()
        valueAnimator.setFloatValues(animatorProgress, endValue)
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        if (animation == valueAnimator) {
            onProgressChange(valueAnimator.animatedValue as Float)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onProgressChange(animatorProgress)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas != null) {
            expandBackground.draw(canvas)
        }
        super.dispatchDraw(canvas)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == expandBackground) {
            invalidate()
            invalidateOutline()
        }
    }

    private fun onProgressChange(value: Float) {
        animatorProgress = value
        val min = minSize
        val max = width
        val distance = max - min
        val offset = distance * value
        nameView.alpha = value
        nameView.translationX = offset - distance
        expandBackground.setBounds(0, 0, (min + offset).toInt(), height)
    }

    private fun dp(value: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics
    )

    private fun sp(value: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics
    )

    private fun log(value: String) {
        Log.d("Lollipop", "ExpandButton: $value")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator.cancel()
        removeCallbacks(expandRunnable)
    }

    interface OnExpendListener {
        fun onExpend(button: ExpandButton, isOpen: Boolean)
    }

    private class ExpandBackground : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        private val drawBoundF = RectF()

        var corner = 0F
            set(value) {
                field = value
                invalidateSelf()
            }

        var color: Int
            set(value) {
                paint.color = value
                invalidateSelf()
            }
            get() = paint.color

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(drawBoundF, corner, corner, paint)
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            drawBoundF.set(bounds)
            invalidateSelf()
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity() = PixelFormat.TRANSPARENT

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }
    }

    private class PressedImageView(
        context: Context, attr: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) :
        AppCompatImageView(context, attr, defStyleAttr) {

        private var pressedListener: ((Boolean) -> Unit)? = null

        fun onPressed(listener: (Boolean) -> Unit) {
            pressedListener = listener
        }

        override fun setPressed(pressed: Boolean) {
            super.setPressed(pressed)
            pressedListener?.invoke(pressed)
        }

    }

    enum class IconGravity(val value: Int) {
        CENTER(0),
        TOP(1),
        BOTTOM(2)
    }

}
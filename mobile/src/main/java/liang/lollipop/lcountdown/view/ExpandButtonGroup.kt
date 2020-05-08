package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import liang.lollipop.lcountdown.R

/**
 * @date: 2019-06-29 15:25
 * @author: lollipop
 *
 */
class ExpandButtonGroup(context: Context, attr: AttributeSet?,
                        defStyleAttr: Int, defStyleRes: Int):
        LinearLayout(context, attr, defStyleAttr, defStyleRes), ExpandButton.OnExpendListener {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    companion object {
        private const val NOTHING = -1
    }

    var selectedColor = Color.BLUE
        set(value) {
            field = value
            updateBtnColor()
        }

    var defaultColor = Color.GREEN
        set(value) {
            field = value
            updateBtnColor()
        }

    var textMaxLines = 1
        set(value) {
            field = value
            requestLayout()
        }

    var selectedIndex = NOTHING
        private set

    private var onSelectedBtnChangeListener: ((ExpandButton) -> Unit)? = null

    private var onExpandStateChangeListener: ((ExpandButton, Boolean) -> Unit)? = null

    init {
        if (attr != null) {
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.ExpandButtonGroup)
            selectedColor = typedArray.getColor(R.styleable.ExpandButtonGroup_selectedColor, Color.BLUE)
            defaultColor = typedArray.getColor(R.styleable.ExpandButtonGroup_defaultColor, Color.GREEN)
            textMaxLines = typedArray.getInt(R.styleable.ExpandButtonGroup_textMaxLines, 1)
            typedArray.recycle()
        }
    }

    fun onSelectedBtnChange(listener: ((ExpandButton) -> Unit)?) {
        this.onSelectedBtnChangeListener = listener
    }

    fun onExpandStateChange(listener: ((ExpandButton, Boolean) -> Unit)?) {
        this.onExpandStateChangeListener = listener
    }

    fun closeAll() {
        for (i in 0 until childCount) {
            getChildAt(i).let { btn ->
                if (btn is ExpandButton) {
                    btn.collapse(false)
                }
            }
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        if (child is ExpandButton) {
            child.setOnClickListener {
                onBtnClick(it as ExpandButton)
            }
            child.onExpendListener = this
        }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        child?.setOnClickListener(null)
        if (child is ExpandButton) {
            child.onExpendListener = null
        }
    }

    override fun onExpend(button: ExpandButton, isOpen: Boolean) {
        onExpandStateChangeListener?.invoke(button, isOpen)
    }

    private fun onBtnClick(btn: ExpandButton? = null) {
        selectedIndex = NOTHING
        if (btn != null) {
            val index = indexOfChild(btn)
            if (index != selectedIndex) {
                onSelectedBtnChangeListener?.invoke(btn)
                selectedIndex = index
            }
        }
        updateBtnColor()
    }

    private fun updateBtnColor() {
        for (i in 0 until childCount) {
            getChildAt(i).let {
                if (it is ExpandButton) {
                    it.color = if (i == selectedIndex) { selectedColor } else { defaultColor }
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        updateBtnColor()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).let { btn ->
                if (btn is ExpandButton) {
                    btn.adjustNameView { nameView ->
                        nameView.maxLines = textMaxLines
                    }
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}
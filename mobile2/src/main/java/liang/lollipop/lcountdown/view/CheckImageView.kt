package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.ImageView

/**
 * @date: 2019-06-29 16:36
 * @author: lollipop
 * 带有点击功能的 ImageView
 */
class CheckImageView(context: Context, attr: AttributeSet?,
                     defStyleAttr: Int, defStyleRes: Int):
        ImageView(context, attr, defStyleAttr, defStyleRes),
        Checkable {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var checkedState = false

    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    fun onChecked(listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }

    override fun isChecked(): Boolean {
        return checkedState
    }

    override fun toggle() {
        setChecked(!checkedState, true)
    }

    override fun setChecked(checked: Boolean) {
        setChecked(checked, true)
    }

    fun setChecked(checked: Boolean, callListener: Boolean = true) {
        if (checkedState != checked) {
            checkedState = checked
            refreshDrawableState()
            if (callListener) {
                onCheckedChangeListener?.onCheckedChange(this, checked)
            }
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(view: CheckImageView, isChecked: Boolean)
    }

}
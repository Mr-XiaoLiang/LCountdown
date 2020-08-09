package liang.lollipop.lcountdown.view

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Checkable
import liang.lollipop.lcountdown.drawable.GradientDrawable

/**
 * @author lollipop
 * @date 8/7/20 00:39
 * 形状的View
 */
class ShapeView(context: Context, attr: AttributeSet?,
                defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes), Checkable {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val gradientDrawable = GradientDrawable()

    private val grayColorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
        setSaturation(0F)
    })

    private var isChecked = false

    init {
        background = gradientDrawable
    }

    private var checkedChangeListener: ((ShapeView, Boolean) -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onCheckedChange()
    }

    fun changeColor(vararg colors: Int) {
        gradientDrawable.changeColor(*colors)
    }

    fun changeColor(colors: List<Int>) {
        gradientDrawable.changeColor(colors)
    }

    fun changeStart(x: Float, y: Float) {
        gradientDrawable.changeStart(x, y)
    }

    fun changeEnd(x: Float, y: Float) {
        gradientDrawable.changeEnd(x, y)
    }

    fun setType(type: GradientDrawable.Type) {
        gradientDrawable.gradientType = type
    }

    fun setCorner(value: Float) {
        gradientDrawable.corner = value
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        isChecked = !isChecked
        onCheckedChange()
    }

    override fun setChecked(checked: Boolean) {
        if (checked != isChecked) {
            this.checkedChangeListener?.invoke(this, checked)
            isChecked = checked
            onCheckedChange()
        }
    }

    private fun onCheckedChange() {
        gradientDrawable.colorFilter = if (isChecked) { null } else { grayColorFilter }
        invalidate()
    }

    override fun invalidate() {
        gradientDrawable.updateGradient()
        super.invalidate()
    }

    fun onCheckedChange(listener: ((ShapeView, Boolean) -> Unit)?) {
        this.checkedChangeListener = listener
    }

    class CheckedGroup {

        private val viewList = ArrayList<ShapeView>()

        private var checkedIndex = -1

        private var isEnable = true

        private var checkedChangeListener: ((view: ShapeView) -> Unit)? = null

        private var viewClickListener = { view: View ->
            if (view is ShapeView) {
                select(view)
            }
        }

        fun add(vararg views: ShapeView) {
            views.forEach {
                it.setOnClickListener(viewClickListener)
                viewList.add(it)
            }
            checked()
        }

        fun select(view: ShapeView) {
            if (!view.isChecked) {
                return
            }
            view.isChecked = true
            checkedIndex = viewList.indexOf(view)
            for (index in viewList.indices) {
                if (index != checkedIndex && viewList[index].isChecked) {
                    viewList[index].isChecked = false
                }
            }
            checkedChangeListener?.invoke(view)
        }

        private fun checked() {
            isEnable = false
            if (checkedIndex < 0) {
                // 如果没有选中的，那么默认记录第一个选中的
                for (index in viewList.indices) {
                    if (viewList[index].isChecked) {
                        if (checkedIndex < 0) {
                            checkedIndex = index
                        } else {
                            viewList[index].isChecked = false
                        }
                    }
                }
            } else {
                // 如果已经有一个选中的，那么关闭其他
                for (index in viewList.indices) {
                    if (index != checkedIndex && viewList[index].isChecked) {
                        viewList[index].isChecked = false
                    }
                }
            }
            isEnable = true
        }

        fun onCheckedChange(listener: (view: ShapeView) -> Unit) {
            checkedChangeListener = listener
        }

    }

}
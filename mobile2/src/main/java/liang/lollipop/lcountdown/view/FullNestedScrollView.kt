package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView

/**
 * @author lollipop
 * @date 2020/5/24 00:42
 * 自动填充满容器的ScrollView
 */
class FullNestedScrollView(context: Context, attr: AttributeSet?, style: Int)
    : NestedScrollView(context, attr, style) {

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            return
        }
        if (childCount > 0) {
            val child = getChildAt(0)
            val layoutParams = child.layoutParams as MarginLayoutParams
            val groupHeight = (measuredHeight
                    - paddingTop
                    - paddingBottom
                    - layoutParams.topMargin
                    - layoutParams.bottomMargin)
            if (child.measuredHeight < groupHeight) {
                val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        paddingLeft + paddingRight + layoutParams.leftMargin + layoutParams.rightMargin,
                        layoutParams.width)
                val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(groupHeight, MeasureSpec.EXACTLY)
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }

}
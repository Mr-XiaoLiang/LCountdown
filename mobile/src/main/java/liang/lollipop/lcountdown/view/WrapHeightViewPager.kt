package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * @date: 2018/6/21 17:50
 * @author: lollipop
 *
 * 自适应高度的ViewPager
 */
class WrapHeightViewPager(context: Context, attr: AttributeSet?) : ViewPager(context,attr) {

    constructor(context: Context): this(context,null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val newHeightMeasureSpec = if (mode != MeasureSpec.EXACTLY && childCount > 0) {
            //如果是未指定状态，就测量Child的最大高度
            //下面遍历所有child的高度
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                val h = child.measuredHeight
                if (h > height) {
                    //采用最大的view的高度。
                    height = h
                }
            }
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun setCurrentItem(item: Int) {
        setCurrentItem(item, true)
    }

}
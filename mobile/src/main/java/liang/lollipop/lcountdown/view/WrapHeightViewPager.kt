package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
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
        var newHeightMeasureSpec = heightMeasureSpec

        var height = 0
        if (childCount > 0) {//如果是未指定状态，就测量Child的最大高度
            //下面遍历所有child的高度
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                val h = child.measuredHeight
                if (h > height)
                //采用最大的view的高度。
                    height = h
            }
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY)
        }else{
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                                MeasureSpec.UNSPECIFIED)
        }

        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

}
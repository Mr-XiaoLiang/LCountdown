package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author lollipop
 * @date 2020/6/14 23:02
 * 放弃事件处理的ViewPager
 */
class NoTouchViewPager(context: Context, attr: AttributeSet?) : ViewPager(context,attr) {

    constructor(context: Context): this(context,null)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun setCurrentItem(item: Int) {
        setCurrentItem(item, true)
    }
}
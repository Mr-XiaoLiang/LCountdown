package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * @date: 2019-06-29 16:02
 * @author: lollipop
 * 自动确定方向的 SeekBar
 */
class AutoSeekBar(context: Context, attr: AttributeSet?,
                  defStyleAttr: Int, defStyleRes: Int): View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)



}
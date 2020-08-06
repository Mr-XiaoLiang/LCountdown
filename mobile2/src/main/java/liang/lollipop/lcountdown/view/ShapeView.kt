package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * @author lollipop
 * @date 8/7/20 00:39
 * 形状的View
 */
class ShapeView(context: Context, attr: AttributeSet?,
                defStyleAttr: Int, defStyleRes: Int) : View(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)



}
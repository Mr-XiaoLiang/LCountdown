package liang.lollipop.lcountdown.view

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton

/**
 * @author lollipop
 * @date 7/26/20 13:14
 * 支持检查状态的Text
 */
class CheckableTextView(context: Context, attr: AttributeSet?, style: Int)
    : CompoundButton(context, attr, style) {

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context): this(context, null)

    override fun getAccessibilityClassName(): CharSequence {
        return CheckableTextView::class.java.name
    }

}
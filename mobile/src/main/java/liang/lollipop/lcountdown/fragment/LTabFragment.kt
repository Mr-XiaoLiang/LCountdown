package liang.lollipop.lcountdown.fragment

import android.graphics.Color
import android.graphics.drawable.Drawable
import liang.lollipop.lbaselib.base.BaseFragment

/**
 * @date: 2019-06-22 17:35
 * @author: lollipop
 * 兼容 LTabView 的 Fragment
 */
open class LTabFragment: BaseFragment() {

    open fun getIcon(): Drawable? {
        return null
    }

    open fun getIconId(): Int {
        return 0
    }

    open fun getSelectedColor(): Int {
        return 0
    }

    open fun getSelectedColorId(): Int {
        return 0
    }

}
package liang.lollipop.ltabview

import android.graphics.drawable.Drawable

/**
 * @date: 2019-06-22 17:16
 * @author: lollipop
 * LTabView 的 ViewPager.Adapter 接口，用于扩展
 */
interface ViewPagerAdapterByLTab {

    fun getIcon(index: Int): Drawable

    fun getSelectedColor(index: Int): Int

    fun getUnselectedColor(index: Int): Int

}
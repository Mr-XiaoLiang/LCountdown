package liang.lollipop.lcountdown.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager

/**
 * @author lollipop
 * @date 2020/5/31 23:21
 * View的辅助类
 */
object ViewHelper {

    fun draw(bitmap: Bitmap, view: View) {
        val canvas = Canvas(bitmap)
        view.draw(canvas)
    }

    fun layout(view: View, width: Int, height: Int) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        view.layout(0, 0, width, height)
    }

    fun addView(group: ViewGroup, child: View,
                bind: (ViewGroup.LayoutParams) -> Unit,
                generate: (ViewGroup) -> ViewGroup.LayoutParams) {
        val layoutParams = child.layoutParams?:generate.invoke(group)
        bind.invoke(layoutParams)
        child.layoutParams = layoutParams
        child.parent?.let {
            if (it is ViewManager) {
                it.removeView(child)
            }
        }
        group.addView(child)
    }

}
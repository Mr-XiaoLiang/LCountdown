package liang.lollipop.lcountdown.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

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

}
package liang.lollipop.lcountdown.engine

import android.graphics.Canvas
import android.view.View

/**
 * @author lollipop
 * @date 9/30/20 00:36
 * 渲染引擎
 */
abstract class RenderEngine {

    /**
     * 绘制的方法
     */
    abstract fun draw(canvas: Canvas)

    protected fun layout(view: View, width: Int, height: Int) {
        view.layout(0, 0, width, height)
    }

    protected fun measure(view: View, width: Int, height: Int) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
    }

}
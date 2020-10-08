package liang.lollipop.lcountdown.engine

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager

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

    protected fun draw(view: View, canvas: Canvas) {
        view.draw(canvas)
    }

    protected fun remove(view: View) {
        val parent = view.parent?:return
        if (parent is ViewManager) {
            parent.removeView(view)
        }
    }

    protected inline fun <reified T: ViewGroup> add(group: T, view: View) {
        val parent = view.parent
        if (parent != group) {
            remove(view)
        }
        group.addView(view)
    }

    protected inline fun <reified T> findFromList(list: ArrayList<*>, create: () -> T): T {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next is T) {
                iterator.remove()
                return next
            }
        }
        return create()
    }

}
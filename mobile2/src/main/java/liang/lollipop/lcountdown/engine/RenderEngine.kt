package liang.lollipop.lcountdown.engine

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.FrameLayout
import java.lang.RuntimeException

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


    protected fun matchParent(view: View) {
        val layoutParams = view.layoutParams
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
        view.layoutParams = layoutParams
    }

    protected fun findFrameLayoutParams(view: View): FrameLayout.LayoutParams {
        var layoutParams = view.layoutParams
        if (layoutParams is FrameLayout.LayoutParams) {
            return layoutParams
        }
        layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        return layoutParams
    }

    class EngineException(msg: String, e: Throwable? = null): RuntimeException(msg, e)

}
package liang.lollipop.lcountdown.engine

import android.content.Context
import android.graphics.Canvas
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 10/2/20 20:24
 */
class WidgetEngine(private val widgetRoot: FrameLayout): RenderEngine() {

    companion object {
        fun create(context: Context): WidgetEngine {
            return WidgetEngine(FrameLayout(context))
        }
    }

    override fun draw(canvas: Canvas) {

    }

}
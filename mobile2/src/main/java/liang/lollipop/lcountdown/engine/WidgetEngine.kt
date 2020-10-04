package liang.lollipop.lcountdown.engine

import android.content.Context
import android.graphics.Canvas
import android.widget.FrameLayout
import liang.lollipop.lcountdown.info.WidgetInfo

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

    fun update(widgetInfo: WidgetInfo) {
        updateCard(widgetInfo)
        updateBackground(widgetInfo)
        updateText(widgetInfo)
    }

    private fun updateCard(widgetInfo: WidgetInfo) {
        // TODO
    }

    private fun updateBackground(widgetInfo: WidgetInfo) {
        updateBackgroundColor(widgetInfo)
        updateBackgroundImage(widgetInfo)
    }

    private fun updateBackgroundColor(widgetInfo: WidgetInfo) {
        // TODO
    }

    private fun updateBackgroundImage(widgetInfo: WidgetInfo) {
        // TODO
    }

    private fun updateText(widgetInfo: WidgetInfo) {
        // TODO
    }

}
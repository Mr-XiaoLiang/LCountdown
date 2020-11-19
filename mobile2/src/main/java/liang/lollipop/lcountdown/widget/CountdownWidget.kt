package liang.lollipop.lcountdown.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.widget.FrameLayout
import liang.lollipop.lcountdown.engine.WidgetEngine
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.util.WidgetDBUtil

/**
 * @author lollipop
 * @date 11/18/20 21:28
 */
open class CountdownWidget: AppWidgetProvider() {

    companion object {
        const val WIDGET_SHOW = "WIDGET_SHOW"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context?:return
        appWidgetManager?:return
        appWidgetIds?:return
        val dbUtil = WidgetDBUtil.read(context)
        val widgetInfo = WidgetInfo()
        val widgetEngine = WidgetEngine(FrameLayout(context))
        val bitmapProvider = BitmapProvider()
        for (id in appWidgetIds) {
            widgetInfo.widgetId = id
            dbUtil.findByWidgetId(widgetInfo)
            widgetEngine.updateAll(widgetInfo)
            updateWidget(widgetEngine, bitmapProvider)
        }
        dbUtil.close()
    }

    private fun updateWidget(widgetEngine: WidgetEngine, bitmapProvider: BitmapProvider) {

    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    private class BitmapProvider {
        private val bitmapList = ArrayList<Bitmap>()

        fun get(width: Int, height: Int): Bitmap {
            for (bitmap in bitmapList) {
                if (!bitmap.isRecycled
                        && bitmap.width == width
                        && bitmap.height == height) {
                    return bitmap
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmapList.add(bitmap)
            return bitmap
        }

        fun recycle() {
            for (bitmap in bitmapList) {
                try {
                    if (!bitmap.isRecycled) {
                        bitmap.recycle()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            bitmapList.clear()
        }
    }

}
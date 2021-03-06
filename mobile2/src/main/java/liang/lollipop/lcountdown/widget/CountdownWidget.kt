package liang.lollipop.lcountdown.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.FrameLayout
import android.widget.RemoteViews
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.activity.WidgetAdjustmentActivity
import liang.lollipop.lcountdown.engine.WidgetEngine
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.util.WidgetDBUtil
import liang.lollipop.lcountdown.util.doAsync

/**
 * @author lollipop
 * @date 11/18/20 21:28
 */
open class CountdownWidget: AppWidgetProvider() {

    companion object {

        const val LAYOUT_ID = R.layout.widget_desktop

        fun updateWidget(
                widgetEngine: WidgetEngine,
                bitmapProvider: BitmapProvider,
                widgetInfo: WidgetInfo,
                context: Context,
                appWidgetManager: AppWidgetManager) {

            val bitmap = bitmapProvider.new(
                    widgetInfo.width.toInt(), widgetInfo.height.toInt())
            val canvas = Canvas(bitmap)
            widgetEngine.draw(canvas)

            val remoteViews = RemoteViews(context.packageName, LAYOUT_ID)
            remoteViews.setImageViewBitmap(R.id.widgetPanel, bitmap)
            val widgetId = widgetInfo.widgetId
            //创建点击意图
            val intent = WidgetAdjustmentActivity.createIntent(context, widgetId)
            //请求ID重复会导致延时意图被覆盖，刷新模式表示覆盖的方式
            val pendingIntent = PendingIntent.getActivity(context, widgetId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
            //为小部件设置点击事件
            remoteViews.setOnClickPendingIntent(R.id.widgetPanel, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
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
            try {
                widgetInfo.widgetId = id
                dbUtil.findByWidgetId(widgetInfo)
                widgetEngine.updateAll(widgetInfo)

                updateWidget(widgetEngine, bitmapProvider,
                        widgetInfo, context, appWidgetManager)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        bitmapProvider.recycle()
        dbUtil.close()
    }

    private fun updateWidgetId(
            context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        val dbUtil = WidgetDBUtil.read(context)
        doAsync {
            for (idIndex in oldWidgetIds.indices) {
                try {
                    // 更新为新id
                    dbUtil.updateWidgetId(oldWidgetIds[idIndex], newWidgetIds[idIndex])
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }.finally {
            dbUtil.close()
        }
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        context?:return
        oldWidgetIds?:return
        newWidgetIds?:return
        updateWidgetId(context, oldWidgetIds, newWidgetIds)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        context?:return
        appWidgetIds?:return
        updateWidgetId(context, appWidgetIds,
                IntArray(appWidgetIds.size) { AppWidgetManager.INVALID_APPWIDGET_ID })
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    class BitmapProvider {
        private val bitmapList = ArrayList<Bitmap>()

        fun get(width: Int, height: Int): Bitmap {
            for (bitmap in bitmapList) {
                if (!bitmap.isRecycled
                        && bitmap.width == width
                        && bitmap.height == height) {
                    return bitmap
                }
            }
            return new(width, height)
        }

        fun new(width: Int, height: Int): Bitmap {
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
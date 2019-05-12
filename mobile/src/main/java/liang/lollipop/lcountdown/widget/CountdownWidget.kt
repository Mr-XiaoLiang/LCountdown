package liang.lollipop.lcountdown.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.utils.AppSettings
import liang.lollipop.lcountdown.utils.WidgetDBUtil
import liang.lollipop.lcountdown.utils.WidgetUtil

/**
 * 倒计时的小部件
 * @author Lollipop
 */
class CountdownWidget: AppWidgetProvider() {

    companion object {

        const val WIDGET_SHOW = "WIDGET_SHOW"

    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if(context == null || appWidgetIds == null || appWidgetManager == null){
            return
        }

        val dbUtil = WidgetDBUtil.read(context)
        val widget = WidgetBean()

        for(weightId in appWidgetIds){
            widget.widgetId = weightId
            dbUtil.get(widget)
            WidgetUtil.update(context,widget,appWidgetManager)
        }

        dbUtil.close()

    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        if(context==null || oldWidgetIds == null || newWidgetIds == null || oldWidgetIds.size != newWidgetIds.size){
            return
        }
        for(index in 0 until oldWidgetIds.size){
            AppSettings.copyData(context,newWidgetIds[index],oldWidgetIds[index])
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        if(context == null || appWidgetIds == null){
            return
        }
        val dbUtil = WidgetDBUtil.write(context)
        for(id in appWidgetIds){
            dbUtil.delete(id)
        }
        dbUtil.close()
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        if(context == null){
            return
        }
//        if(context != null){
//            WidgetUtil.lastUpdate(context)
//        }
        WidgetUtil.alarmUpdate(context)
//        context?.startService(Intent(context,CountdownService::class.java))
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        if(context == null){
            return
        }
//        if(context != null){
//            WidgetUtil.cancel(context)
//        }

        WidgetDBUtil.write(context).deleteAll().close()
        WidgetUtil.cancel(context)
//        context.stopService(Intent(context,CountdownService::class.java))
    }

}
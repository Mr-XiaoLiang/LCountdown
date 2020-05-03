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
        for(index in oldWidgetIds.indices){
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
        WidgetUtil.alarmUpdate(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        if(context == null){
            return
        }
        WidgetDBUtil.write(context).deleteAll().close()
        WidgetUtil.cancel(context)
    }

}
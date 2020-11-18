package liang.lollipop.lcountdown.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

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


}
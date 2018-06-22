package liang.lollipop.lcountdown.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.activity.TimingListActivity

/**
 * 倒计时的小部件
 * @author Lollipop
 */
class TimingListWidget: AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if(context == null || appWidgetIds == null || appWidgetManager == null){
            return
        }

        for(widgetId in appWidgetIds){

            val views = RemoteViews(context.packageName, R.layout.widget_timing_list)
            //创建点击意图
            val intent = Intent(context, TimingListActivity::class.java)
            //创建一个延时意图，意图目标为打开页面(getActivity)，
            //getActivity(上下文,请求ID，意图内容，刷新模式)
            //请求ID重复会导致延时意图被覆盖，刷新模式表示覆盖的方式
            val pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            //为小部件设置点击事件
            views.setOnClickPendingIntent(R.id.timingListBtn, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }

    }

}
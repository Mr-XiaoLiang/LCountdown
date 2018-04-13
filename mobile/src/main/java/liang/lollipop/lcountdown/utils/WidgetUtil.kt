package liang.lollipop.lcountdown.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import liang.lollipop.lcountdown.widget.CountdownWidget
import liang.lollipop.lcountdown.activity.MainActivity
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle

object WidgetUtil {

    const val UPDATE_TIME = 1000L * 60

    fun update(context: Context, bean: CountdownBean, widgetBean: WidgetBean, appWidgetManager: AppWidgetManager){

        val layoutId = when(widgetBean.widgetStyle){

            WidgetStyle.LIGHT -> R.layout.widget_countdown

            WidgetStyle.DARK -> R.layout.widget_countdown_dark

            WidgetStyle.BLACK -> R.layout.widget_countdown_black

            WidgetStyle.WHITE -> R.layout.widget_countdown_white

        }

        val views = RemoteViews(context.packageName, layoutId)

        views.setTextViewText(R.id.nameView,widgetBean.countdownName)
        views.setTextViewText(R.id.dayView,bean.days)
        views.setTextViewText(R.id.hourView,bean.hours)
        views.setTextViewText(R.id.timeView,bean.time)
        views.setTextViewText(R.id.signView,widgetBean.signValue)

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetBean.widgetId)
        intent.putExtra(CountdownWidget.WIDGET_SHOW,1)
        val pendingIntent = PendingIntent.getActivity(context, widgetBean.widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.widgetGroup, pendingIntent)

        appWidgetManager.updateAppWidget(widgetBean.widgetId, views)

    }

    fun alarmUpdate(context: Context){

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextTime:Long = ((System.currentTimeMillis() / UPDATE_TIME) + 1) * UPDATE_TIME

        alarm.setRepeating(AlarmManager.RTC,nextTime, UPDATE_TIME,getIntent(context))

    }

    fun cancel(context: Context){
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarm.cancel(getIntent(context))
    }

    private fun getIntent(context: Context): PendingIntent{
        val intent = Intent(context, CountdownWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,getIdArray(context))
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getIdArray(context: Context): IntArray{

        val idList = ArrayList<Int>()

        WidgetDBUtil.read(context).getAllId(idList).close()

        return IntArray(idList.size,{ it -> idList[it] })

    }

    fun callUpdate(context: Context){

        val intent = Intent(context, CountdownWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,getIdArray(context))
        context.sendBroadcast(intent)

    }

}
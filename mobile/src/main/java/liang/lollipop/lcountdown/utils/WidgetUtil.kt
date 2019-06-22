package liang.lollipop.lcountdown.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import liang.lollipop.lcountdown.widget.CountdownWidget
import liang.lollipop.lcountdown.activity.MainActivity
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle

object WidgetUtil {

    const val UPDATE_TIME = 1000L * 60

    val TEXT_VIEW_ID = arrayOf(R.id.nameFrontView, R.id.nameView, R.id.nameBehindView, R.id.dayView, R.id.dayUnitView, R.id.timeView, R.id.signView)

    fun update(context: Context, widgetBean: WidgetBean, appWidgetManager: AppWidgetManager){

        val layoutId = R.layout.widget_countdown

        val views = RemoteViews(context.packageName, layoutId)

        views.updateColors(widgetBean.widgetStyle)

        views.updateValues(widgetBean.getTimerInfo(), widgetBean)

        views.updateTextSize(widgetBean)

        //创建点击意图
        val intent = Intent(context, MainActivity::class.java)
        //携带参数：小部件ID
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetBean.widgetId)
        //携带参数：是否是小部件显示模式
        intent.putExtra(CountdownWidget.WIDGET_SHOW,1)
        //创建一个延时意图，意图目标为打开页面(getActivity)，
        //getActivity(上下文,请求ID，意图内容，刷新模式)
        //请求ID重复会导致延时意图被覆盖，刷新模式表示覆盖的方式
        val pendingIntent = PendingIntent.getActivity(context, widgetBean.widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        //为小部件设置点击事件
        views.setOnClickPendingIntent(R.id.widgetGroup, pendingIntent)

        appWidgetManager.updateAppWidget(widgetBean.widgetId, views)

    }

    fun getTextColor(style: WidgetStyle): Int {
        return when (style) {
            WidgetStyle.LIGHT -> {
                Color.WHITE
            }
            WidgetStyle.DARK -> {
                Color.BLACK
            }
            WidgetStyle.WHITE -> {
                Color.BLACK
            }
            WidgetStyle.BLACK -> {
                Color.WHITE
            }
        }
    }

    fun getBackgroundResource(style: WidgetStyle): Int {
        return when (style) {
            WidgetStyle.LIGHT, WidgetStyle.DARK -> {
                0
            }
            WidgetStyle.WHITE -> {
                R.drawable.bg_white
            }
            WidgetStyle.BLACK -> {
                R.drawable.bg_black
            }
        }
    }

    fun updateTextColorByStyle(style: WidgetStyle, run: (Int, Int) -> Unit) {
        val textColor = getTextColor(style)
        TEXT_VIEW_ID.forEach { id ->
            run(id, textColor)
        }
    }

    private fun RemoteViews.updateColors(style: WidgetStyle) {
        log("RemoteViews.updateColors: $style")
        updateTextColorByStyle(style) {id, color ->
            setTextColor(id, color)
        }
        val background = getBackgroundResource(style)
        setInt(R.id.widgetGroup, "setBackgroundResource", background)
    }

    private fun RemoteViews.updateValues(bean: CountdownBean, widgetBean: WidgetBean) {
        setTextViewText(R.id.nameView,widgetBean.countdownName)
        setTextViewText(R.id.dayView,bean.days)
        setTextViewText(R.id.timeView,bean.time)
        setTextViewText(R.id.signView,widgetBean.signValue)

        setViewVisibility(R.id.timeView,if(widgetBean.noTime){ View.GONE }else{ View.VISIBLE })

        setTextViewText(R.id.nameFrontView,widgetBean.prefixName)
        setTextViewText(R.id.nameBehindView,widgetBean.suffixName)
        setTextViewText(R.id.dayUnitView,widgetBean.dayUnit)
    }

    private fun RemoteViews.updateTextSize(widgetBean: WidgetBean) {
        setTextViewTextSize(R.id.nameFrontView,TypedValue.COMPLEX_UNIT_SP,widgetBean.prefixFontSize.toFloat())
        setTextViewTextSize(R.id.nameView,TypedValue.COMPLEX_UNIT_SP,widgetBean.nameFontSize.toFloat())
        setTextViewTextSize(R.id.nameBehindView,TypedValue.COMPLEX_UNIT_SP,widgetBean.suffixFontSize.toFloat())
        setTextViewTextSize(R.id.dayView,TypedValue.COMPLEX_UNIT_SP,widgetBean.dayFontSize.toFloat())
        setTextViewTextSize(R.id.dayUnitView,TypedValue.COMPLEX_UNIT_SP,widgetBean.dayUnitFontSize.toFloat())
        setTextViewTextSize(R.id.timeView,TypedValue.COMPLEX_UNIT_SP,widgetBean.timeFontSize.toFloat())
        setTextViewTextSize(R.id.signView,TypedValue.COMPLEX_UNIT_SP,widgetBean.signFontSize.toFloat())
        setViewVisibility(R.id.dayGroup, if (widgetBean.inOneDay) { View.GONE } else { View.VISIBLE })
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

        return IntArray(idList.size) { idList[it] }

    }

    fun callUpdate(context: Context){

        val intent = Intent(context, CountdownWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,getIdArray(context))
        context.sendBroadcast(intent)

    }

    private fun log(value: String) {
        Log.d("Lollipop", "WidgetUtil: $value")
    }

}
package liang.lollipop.lcountdown.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.activity.MainActivity
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.widget.CountdownWidget
import kotlin.math.abs

object WidgetUtil {

    const val UPDATE_TIME = 1000L * 60

    private val TEXT_VIEW_ID = arrayOf(R.id.nameFrontView, R.id.nameView, R.id.nameBehindView,
            R.id.dayView, R.id.dayUnitView, R.id.timeView, R.id.signView)

    fun update(context: Context, widgetBean: WidgetBean, appWidgetManager: AppWidgetManager){

        val layoutId = R.layout.widget_countdown

        val views = RemoteViewInterface(context, RemoteViews(context.packageName, layoutId))

//        views.updateColors(widgetBean)
//        views.updateImage(context, widgetBean)
//        views.updateValues(widgetBean.getTimerInfo(), widgetBean)
//        views.updateTextSize(widgetBean)
//        views.updateLocation(widgetBean, context.resources)

        updateUI(context, widgetBean, views)

        //创建点击意图
        val intent = Intent(context, MainActivity::class.java)
        //携带参数：小部件ID
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetBean.widgetId)
        //携带参数：是否是小部件显示模式
        intent.putExtra(CountdownWidget.WIDGET_SHOW,1)
        //创建一个延时意图，意图目标为打开页面(getActivity)，
        //getActivity(上下文,请求ID，意图内容，刷新模式)
        //请求ID重复会导致延时意图被覆盖，刷新模式表示覆盖的方式
        val pendingIntent = PendingIntent.getActivity(context, widgetBean.widgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        //为小部件设置点击事件
        views.target.setOnClickPendingIntent(R.id.widgetGroup, pendingIntent)

        appWidgetManager.updateAppWidget(widgetBean.widgetId, views.target)

    }

    fun updateUI(context: Context, widgetBean: WidgetBean, views: ViewInterface) {
        views.updateColors(widgetBean)
        views.updateImage(widgetBean)
        views.updateValues(widgetBean.getTimerInfo(), widgetBean)
        views.updateTextSize(widgetBean)
        views.updateLocation(widgetBean, context.resources)
    }

    interface ViewInterface {
        fun setImageViewBitmapById(id: Int, widgetId: Int)
        fun setTextColor(id: Int, color: Int)
        fun setBackgroundResource(id: Int, res: Int)
        fun setTextViewText(id: Int, text: CharSequence)
        fun setViewVisibility(id: Int, visible: Int)
        fun setTextViewTextSize(id: Int, units: Int, size: Float)
        fun setViewPadding(id: Int, left: Int, top: Int, right: Int, bottom: Int)
        fun setGravity(id: Int, gravity: Int)
    }

    class NativeViewInterface(private val views: View): ViewInterface {

        private fun <T: View> Int.find(): T? {
            return views.findViewById<T>(this)
        }

        override fun setImageViewBitmapById(id: Int, widgetId: Int) {
            id.find<ImageView>()?.let {
                FileUtil.loadWidgetImage(it, widgetId)
            }
        }

        override fun setTextColor(id: Int, color: Int) {
            id.find<TextView>()?.setTextColor(color)
        }

        override fun setBackgroundResource(id: Int, res: Int) {
            id.find<View>()?.setBackgroundResource(res)
        }

        override fun setTextViewText(id: Int, text: CharSequence) {
            id.find<TextView>()?.text = text
        }

        override fun setViewVisibility(id: Int, visible: Int) {
            id.find<View>()?.visibility = visible
        }

        override fun setTextViewTextSize(id: Int, units: Int, size: Float) {
            id.find<TextView>()?.setTextSize(units, size)
        }

        override fun setViewPadding(id: Int, left: Int, top: Int, right: Int, bottom: Int) {
            id.find<View>()?.setPadding(left, top, right, bottom)
        }

        override fun setGravity(id: Int, gravity: Int) {
            id.find<LinearLayout>()?.gravity = gravity
        }
    }

    private class RemoteViewInterface(private val context: Context,
                                      private val views: RemoteViews): ViewInterface {

        val target: RemoteViews
            get() {
                return views
            }

        override fun setImageViewBitmapById(id: Int, widgetId: Int) {
            val image = FileUtil.getWidgetImage(context, widgetId)
            if (!image.exists()) {
                views.setImageViewBitmap(id, null)
                return
            }
            val bitmap = BitmapFactory.decodeFile(image.path)
            views.setImageViewBitmap(id, bitmap)
        }

        override fun setTextColor(id: Int, color: Int) {
            views.setTextColor(id, color)
        }

        override fun setBackgroundResource(id: Int, res: Int) {
            views.setInt(R.id.widgetGroup, "setBackgroundResource", res)
        }

        override fun setTextViewText(id: Int, text: CharSequence) {
            views.setTextViewText(id, text)
        }

        override fun setViewVisibility(id: Int, visible: Int) {
            views.setViewVisibility(id, visible)
        }

        override fun setTextViewTextSize(id: Int, units: Int, size: Float) {
            views.setTextViewTextSize(id, units, size)
        }

        override fun setViewPadding(id: Int, left: Int, top: Int, right: Int, bottom: Int) {
            views.setViewPadding(id, left, top, right, bottom)
        }

        override fun setGravity(id: Int, gravity: Int) {
            views.setInt(id, "setGravity", gravity)
        }

    }

    private fun getTextColor(style: WidgetStyle): Int {
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

    private fun getColorById(id: Int, def: Int, bean: WidgetBean): Int {
        return when(id) {
            R.id.nameFrontView -> bean.prefixColor
            R.id.nameView -> bean.nameColor
            R.id.nameBehindView -> bean.suffixColor
            R.id.dayView -> bean.daysColor
            R.id.dayUnitView -> bean.unitColor
            R.id.timeView -> bean.timeColor
            R.id.signView -> bean.inscriptionColor
            else -> def
        }
    }

    private fun getBackgroundResource(style: WidgetStyle): Int {
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

    private fun updateTextColor(bean: WidgetBean, run: (Int, Int) -> Unit) {
        val defColor = getTextColor(bean.widgetStyle)
        TEXT_VIEW_ID.forEach { id ->
            run(id, getColorById(id, defColor, bean))
        }
    }

    private fun ViewInterface.updateImage(bean: WidgetBean) {
        setImageViewBitmapById(R.id.backgroundImage, bean.widgetId)
    }

    private fun ViewInterface.updateColors(bean: WidgetBean) {
        log("RemoteViews.updateColors: ${bean.widgetStyle}")
        updateTextColor(bean) { id, color ->
            setTextColor(id, color)
        }
        val background = getBackgroundResource(bean.widgetStyle)
//        setInt(R.id.widgetGroup, "setBackgroundResource", background)
        setBackgroundResource(R.id.widgetGroup, background)
    }

    private fun ViewInterface.updateValues(bean: CountdownBean, widgetBean: WidgetBean) {
        setTextViewText(R.id.nameView,widgetBean.countdownName)
        setTextViewText(R.id.dayView,bean.days)
        setTextViewText(R.id.timeView,bean.time)
        setTextViewText(R.id.signView,widgetBean.signValue)

        setViewVisibility(R.id.timeView,if(widgetBean.noTime){ View.GONE }else{ View.VISIBLE })

        setTextViewText(R.id.nameFrontView,widgetBean.prefixName)
        setTextViewText(R.id.nameBehindView,widgetBean.suffixName)
        setTextViewText(R.id.dayUnitView,widgetBean.dayUnit)
    }

    private fun ViewInterface.updateTextSize(widgetBean: WidgetBean) {
        setTextViewTextSize(R.id.nameFrontView,TypedValue.COMPLEX_UNIT_SP,widgetBean.prefixFontSize.toFloat())
        setTextViewTextSize(R.id.nameView,TypedValue.COMPLEX_UNIT_SP,widgetBean.nameFontSize.toFloat())
        setTextViewTextSize(R.id.nameBehindView,TypedValue.COMPLEX_UNIT_SP,widgetBean.suffixFontSize.toFloat())
        setTextViewTextSize(R.id.dayView,TypedValue.COMPLEX_UNIT_SP,widgetBean.dayFontSize.toFloat())
        setTextViewTextSize(R.id.dayUnitView,TypedValue.COMPLEX_UNIT_SP,widgetBean.dayUnitFontSize.toFloat())
        setTextViewTextSize(R.id.timeView,TypedValue.COMPLEX_UNIT_SP,widgetBean.timeFontSize.toFloat())
        setTextViewTextSize(R.id.signView,TypedValue.COMPLEX_UNIT_SP,widgetBean.signFontSize.toFloat())
        setViewVisibility(R.id.dayGroup, if (widgetBean.inOneDay) { View.GONE } else { View.VISIBLE })
    }

    private fun ViewInterface.updateLocation(widgetBean: WidgetBean, resources: Resources) {
        Target.forEach { target ->
            val locationGroup = when (target) {
                Target.Name -> R.id.nameLocationGroup
                Target.Prefix -> R.id.prefixLocationGroup
                Target.Suffix -> R.id.suffixLocationGroup
                Target.Days -> R.id.dayLocationGroup
                Target.Unit -> R.id.unitLocationGroup
                Target.Time -> R.id.timeLocationGroup
                Target.Inscription -> R.id.signLocationGroup
                else -> null
            }
            locationGroup?.let { groupId ->
                val location = getLocationInfo(widgetBean, target)?: WidgetBean.EMPTY_LOCATION
                if (location.gravity == Gravity.NO_GRAVITY) {
                    resetLocation(widgetBean, target, resources)
                }
                val verticalMargin = location.verticalMargin
                val horizontalMargin = location.horizontalMargin
                val gravity = location.gravity
                val vertical = resources.dp(abs(verticalMargin)).toInt()
                val horizontal = resources.dp(abs(horizontalMargin)).toInt()
                val l: Int
                val t: Int
                val r: Int
                val b: Int
                if (verticalMargin < 0) {
                    t = 0
                    b = vertical
                } else {
                    t = vertical
                    b = 0
                }
                if (horizontalMargin < 0) {
                    l = 0
                    r = horizontal
                } else {
                    l = horizontal
                    r = 0
                }
                setViewPadding(groupId, l, t, r, b)
//                setInt(groupId, "setGravity", gravity)
                setGravity(groupId, gravity)
            }
        }
    }

    fun getLocationInfo(widgetBean: WidgetBean, target: Target): WidgetBean.Location? {
        return when (target) {
            Target.Name -> widgetBean.nameLocation
            Target.Prefix -> widgetBean.prefixLocation
            Target.Suffix -> widgetBean.suffixLocation
            Target.Days -> widgetBean.daysLocation
            Target.Unit -> widgetBean.unitLocation
            Target.Time -> widgetBean.timeLocation
            Target.Inscription -> widgetBean.inscriptionLocation
            else -> null
        }
    }

    private fun Resources.dp(float: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, float, displayMetrics)
    }

    fun resetLocation(widgetBean: WidgetBean, target: Target, resources: Resources) {
        when (target) {
            Target.Name -> widgetBean.nameLocation.apply{
                gravity = resources.getInteger(R.integer.def_name_gravity)
                verticalMargin = resources.getInteger(R.integer.def_name_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_name_horizontal_margin).toFloat()
            }
            Target.Prefix -> widgetBean.prefixLocation.apply{
                gravity = resources.getInteger(R.integer.def_prefix_gravity)
                verticalMargin = resources.getInteger(R.integer.def_prefix_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_prefix_horizontal_margin).toFloat()
            }
            Target.Suffix -> widgetBean.suffixLocation.apply{
                gravity = resources.getInteger(R.integer.def_suffix_gravity)
                verticalMargin = resources.getInteger(R.integer.def_suffix_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_suffix_horizontal_margin).toFloat()
            }
            Target.Days -> widgetBean.daysLocation.apply{
                gravity = resources.getInteger(R.integer.def_days_gravity)
                verticalMargin = resources.getInteger(R.integer.def_days_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_days_horizontal_margin).toFloat()
            }
            Target.Unit -> widgetBean.unitLocation.apply{
                gravity = resources.getInteger(R.integer.def_unit_gravity)
                verticalMargin = resources.getInteger(R.integer.def_unit_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_unit_horizontal_margin).toFloat()
            }
            Target.Time -> widgetBean.timeLocation.apply{
                gravity = resources.getInteger(R.integer.def_time_gravity)
                verticalMargin = resources.getInteger(R.integer.def_time_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_time_horizontal_margin).toFloat()
            }
            Target.Inscription -> widgetBean.inscriptionLocation.apply{
                gravity = resources.getInteger(R.integer.def_inscription_gravity)
                verticalMargin = resources.getInteger(R.integer.def_inscription_vertical_margin).toFloat()
                horizontalMargin = resources.getInteger(R.integer.def_inscription_horizontal_margin).toFloat()
            }
            else -> {}
        }
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

    enum class Target(val value: Int) {
        /** 什么也没有 **/
        Nothing(-1),
        /** 名称 **/
        Name(0),
        /** 名称前缀 **/
        Prefix(1),
        /** 名称后缀 **/
        Suffix(2),
        /** 天数 **/
        Days(3),
        /** 天数的单位 **/
        Unit(4),
        /** 时间 **/
        Time(5),
        /** 签名 **/
        Inscription(6);

        companion object {
            private val allTypes = arrayOf(Name, Prefix, Suffix, Days, Unit, Time, Inscription)

            fun forEach(run: (Target) -> kotlin.Unit) {
                allTypes.forEach(run)
            }

        }
    }

}
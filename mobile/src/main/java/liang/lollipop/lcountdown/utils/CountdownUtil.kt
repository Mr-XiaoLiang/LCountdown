package liang.lollipop.lcountdown.utils

import liang.lollipop.lcountdown.bean.CountdownBean
import android.content.Context


/**
 * 倒计时工具类
 * @author Lollipop
 */
object CountdownUtil {

    private const val ONE_SECOND = 1000
    private const val ONE_MINUTE = ONE_SECOND * 60
    private const val ONE_HOUR = ONE_MINUTE * 60
    private const val ONE_DAY = ONE_HOUR * 24

    private const val NEW_VERSION_HINT = "NEW_VERSION_HINT"

    fun countdown(endTime: Long): CountdownBean {
        val now = System.currentTimeMillis()
        val leftTime = endTime - now
        val days = leftTime / ONE_DAY
        val hours = leftTime % ONE_DAY / ONE_HOUR
        val minutes = leftTime % ONE_HOUR / ONE_MINUTE
        val seconds = leftTime % ONE_MINUTE / ONE_SECOND

        val bean = CountdownBean()
        bean.dayInt = days.toInt()
        bean.hourInt = hours.toInt()
        bean.minuteInt = minutes.toInt()
        bean.secondInt = seconds.toInt()
        bean.days = days.formatNumber()
        bean.hours = hours.formatNumber()
        bean.minutes = minutes.formatNumber()
        bean.seconds = seconds.formatNumber()
        bean.time = "${hours.formatNumber()} : ${minutes.formatNumber()}"

        return bean
    }

    fun timer(startTime: Long): CountdownBean{
        return timer(startTime,System.currentTimeMillis())
    }

    fun timer(startTime: Long,endTime: Long): CountdownBean{
        return timer(CountdownBean(),startTime, endTime)
    }

    fun timer(bean: CountdownBean,startTime: Long,endTime: Long): CountdownBean{
        val leftTime =  endTime - startTime
        val days = leftTime / ONE_DAY
        val hours = leftTime % ONE_DAY / ONE_HOUR
        val minutes = leftTime % ONE_HOUR / ONE_MINUTE
        val seconds = leftTime % ONE_MINUTE / ONE_SECOND

        bean.dayInt = days.toInt()
        bean.hourInt = hours.toInt()
        bean.minuteInt = minutes.toInt()
        bean.secondInt = seconds.toInt()
        bean.days = days.formatNumber()
        bean.hours = hours.formatNumber()
        bean.minutes = minutes.formatNumber()
        bean.seconds = seconds.formatNumber()
        bean.time = "${hours.formatNumber()} : ${minutes.formatNumber()}"

        return bean
    }

    private fun Long.formatNumber(): String{
        return when {
            this > 9 -> ""+this
            this < -9 -> "-"+Math.abs(this)
            this < 0 -> "-0"+Math.abs(this)
            else -> "0"+this
        }
    }

    fun isShowNewVersionHint(context: Context,tag: String): Boolean{
        val thisVersion = getAppVersionName(context)

        val lastVersion = SharedPreferencesUtils[context, NEW_VERSION_HINT + tag, ""] ?:""

        return thisVersion != lastVersion
    }

    fun newVersionHintShown(context: Context,tag: String){
        val thisVersion = getAppVersionName(context)

        SharedPreferencesUtils.put(context, NEW_VERSION_HINT + tag, thisVersion)
    }

    private fun getAppVersionName(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName?:""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}
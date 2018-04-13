package liang.lollipop.lcountdown.utils

import liang.lollipop.lcountdown.bean.CountdownBean

/**
 * 倒计时工具类
 * @author Lollipop
 */
object CountdownUtil {

    private const val ONE_SECOND = 1000
    private const val ONE_MINUTE = ONE_SECOND * 60
    private const val ONE_HOUR = ONE_MINUTE * 60
    private const val ONE_DAY = ONE_HOUR * 24

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

    private fun Long.formatNumber(): String{
        return when {
            this > 9 -> ""+this
            this < -9 -> "-"+Math.abs(this)
            this < 0 -> "-0"+Math.abs(this)
            else -> "0"+this
        }
    }

}
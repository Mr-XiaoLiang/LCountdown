package liang.lollipop.lcountdown.utils

import liang.lollipop.lcountdown.bean.CountdownBean
import android.content.Context
import liang.lollipop.lcountdown.bean.RepeatType
import java.util.*
import kotlin.math.abs


/**
 * 倒计时工具类
 * @author Lollipop
 */
object CountdownUtil {

    private const val ONE_SECOND = 1000L
    private const val ONE_MINUTE = ONE_SECOND * 60
    private const val ONE_HOUR = ONE_MINUTE * 60
    private const val ONE_DAY = ONE_HOUR * 24

    private const val NEW_VERSION_HINT = "NEW_VERSION_HINT"
    private val calendar: Calendar by lazy {
        Calendar.getInstance()
    }

    private val Long.days: Long
        get() {
            return this / ONE_DAY
        }

    private val Long.hours: Long
        get() {
            return this % ONE_DAY / ONE_HOUR
        }
    private val Long.minutes: Long
        get() {
            return this % ONE_HOUR / ONE_MINUTE
        }
    private val Long.seconds: Long
        get() {
            return this % ONE_MINUTE / ONE_SECOND
        }

    fun countdown(endTime: Long, repeatType: RepeatType = RepeatType.None): CountdownBean {
        val now = System.currentTimeMillis()
        return timer(CountdownBean(), now, endTime, repeatType)
    }

    fun timer(startTime: Long, repeatType: RepeatType = RepeatType.None): CountdownBean {
        return timer(CountdownBean(), startTime, System.currentTimeMillis(), repeatType)
    }

    fun timeToWeekDay(time: Long): Int {
        calendar.timeInMillis = time
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun timeToMonthDay(time: Long): Int {
        calendar.timeInMillis = time
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun timer(bean: CountdownBean, startTime: Long, endTime: Long, repeatType: RepeatType = RepeatType.None): CountdownBean {
        val leftTime: Long = when (repeatType) {
            RepeatType.Day -> {
                ((endTime % ONE_DAY) - (startTime % ONE_DAY) + ONE_DAY) % ONE_DAY
            }
            RepeatType.Week -> {
                val dayOffset = ((endTime % ONE_DAY) - (startTime % ONE_DAY)) % ONE_DAY
                calendar.timeInMillis = endTime
                val endWeek = calendar.get(Calendar.DAY_OF_WEEK)
                calendar.timeInMillis = startTime
                val startWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val weekLength = if (endWeek < startWeek) {
                    7 - startWeek + endWeek
                } else {
                    endWeek - startWeek
                }
                val oneWeek = 7 * ONE_DAY
                (dayOffset + (weekLength * ONE_DAY) + oneWeek) % oneWeek
            }
            RepeatType.Month -> {
                val dayOffset = ((endTime % ONE_DAY) - (startTime % ONE_DAY)) % ONE_DAY
                calendar.timeInMillis = endTime
                val endDay = calendar.get(Calendar.DAY_OF_MONTH)
                calendar.timeInMillis = startTime
                val startDay = calendar.get(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val thisMonthMax = calendar.get(Calendar.DAY_OF_MONTH)
                val daySize = if (thisMonthMax < endDay || endDay < startDay) {
                    thisMonthMax - startDay + endDay
                } else {
                    endDay - startDay
                }
               dayOffset + (daySize * ONE_DAY)
            }
            else -> {
                endTime - startTime
            }
        }
        val days = leftTime.days
        val hours = leftTime.hours
        val minutes = leftTime.minutes
        val seconds = leftTime.seconds

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

    private fun Long.formatNumber(): String {
        return when {
            this > 9 -> "" + this
            this < -9 -> "-" + abs(this)
            this < 0 -> "-0" + abs(this)
            else -> "0$this"
        }
    }

    fun isShowNewVersionHint(context: Context, tag: String): Boolean {
        val thisVersion = getAppVersionName(context)
        val lastVersion = SharedPreferencesUtils[context, NEW_VERSION_HINT + tag, ""] ?: ""
        return thisVersion != lastVersion
    }

    fun newVersionHintShown(context: Context, tag: String) {
        val thisVersion = getAppVersionName(context)
        SharedPreferencesUtils.put(context, NEW_VERSION_HINT + tag, thisVersion)
    }

    private fun getAppVersionName(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}
package liang.lollipop.lcountdown.util

import android.content.Context
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.provider.TimeInfoProvider
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

/**
 * @author lollipop
 * @date 10/18/20 21:15
 * 内容填充工具
 */
class InfoStuffHelper (private var context: Context? = null) {

    companion object {
        private const val ONE_SECOND = 1000L
        private const val ONE_MINUTE = ONE_SECOND * 60
        private const val ONE_HOUR = ONE_MINUTE * 60
        private const val ONE_DAY = ONE_HOUR * 24

        private const val INVALID_TIME = -1L
    }

    /**
     * 缓存集合
     */
    private val cacheMap = HashMap<String, String>()

    /**
     * 日历类
     */
    private val calendar = Calendar.getInstance()

    private val timeInfo = TimeInfo()

    /**
     * 是否是倒计时的形式
     */
    private val isCountdown: Boolean
        get() {
            return timeInfo.isCountdown
        }

    /**
     * 循环模式
     */
    private val cycleType: TimeInfoProvider.CycleType
        get() {
            return timeInfo.cycleType
        }

    /**
     * 汉语格式化日期
     */
    private val chineseFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    }

    /**
     * 英语格式化日期
     */
    private val englishFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("MMM d, yyyy HH:mm", Locale.US)
    }

    private val startTime: Long
        get() {
            return if (timeInfo.targetTime == INVALID_TIME) {
                timeInfo.now
            } else {
                timeInfo.targetTime
            }
        }

    private val endTime: Long
        get() {
            return if (timeInfo.limitTime == INVALID_TIME) {
                timeInfo.now
            } else {
                timeInfo.limitTime
            }
        }

    fun updateTarget(info: WidgetInfo) {
        updateTarget(info.targetTime, info.limitTime, info.isCountdown, info.cycleType)
    }

    fun updateTarget(target: Long, limit: Long, countdown: Boolean, cycle: TimeInfoProvider.CycleType) {
        timeInfo.targetTime = target
        timeInfo.limitTime = limit
        timeInfo.isCountdown = countdown
        timeInfo.cycleType = cycle
        updateTime()
    }

    /**
     * 更新时间
     * 清除缓存
     */
    fun updateTime() {
        timeInfo.now = System.currentTimeMillis()
        cacheMap.clear()
    }

    fun stuff(value: String): String {
        val keys = TextFormat.KEYS
        val builder = StringBuilder(value)
        for (index in keys.indices) {
            val key = keys[index]
            replace(builder, key.value)
        }
        return builder.toString()
    }

    private fun replace(builder: StringBuilder, key: String) {
        var index = builder.indexOf(key)
        if (index < 0) {
            return
        }
        val keyLength = key.length
        val value = getValueByKey(key)
        while (index >= 0) {
            val end = index + keyLength
            builder.replace(index, end, value)
            index = builder.indexOf(key, end)
        }
    }

    private fun getValueByKey(key: String): String {
        if (cacheMap.containsKey(key)) {
            val s = cacheMap[key]
            if (s != null && s.isNotEmpty()) {
                return s
            }
        }
        val value: String = when(key) {
            TextFormat.KEY_DAY_WITH_MONTH -> { getDayOfMonth() }
            TextFormat.KEY_DAY_WITH_YEAR -> { getDayOfYear() }
            TextFormat.KEY_DAY_WITH_WEEK -> { getDayOfWeek() }
            TextFormat.KEY_MONTH -> { getMonth() }
            TextFormat.KEY_MONTH_FULL -> { getMonthFull() }
            TextFormat.KEY_MONTH_JAPAN -> { getMonthJapan() }
            TextFormat.KEY_MONTH_ENGLISH -> { getMonthEnglish() }
            TextFormat.KEY_MONTH_CHINESE -> { getMonthChinese() }
            TextFormat.KEY_MONTH_TRADITIONAL -> { getMonthTraditional() }
            TextFormat.KEY_YEAR -> { (getYear() % 100).toString() }
            TextFormat.KEY_YEAR_FULL -> { getYear().toString() }
            TextFormat.KEY_WEEK -> { getWeek() }
            TextFormat.KEY_WEEK_JAPAN -> { getWeekJapan() }
            TextFormat.KEY_WEEK_ENGLISH -> { getWeekEnglish() }
            TextFormat.KEY_WEEK_CHINESE -> { getWeekChinese() }
            TextFormat.KEY_WEEK_TRADITIONAL -> { getWeekTraditional() }
            TextFormat.KEY_HOUR -> { getHour().toString() }
            TextFormat.KEY_HOUR_FULL -> { getHourFull() }
            TextFormat.KEY_MINUTE -> { getMinute().toString() }
            TextFormat.KEY_MINUTE_FULL -> { getMinuteFull() }
            TextFormat.KEY_TIME_CHINA -> { chineseFormat.format(Date(System.currentTimeMillis())) }
            TextFormat.KEY_TIME_ENGLISH -> { englishFormat.format(Date(System.currentTimeMillis())) }
            TextFormat.KEY_COUNTDOWN_DAYS -> { getCountdownDays() }
            TextFormat.KEY_COUNTDOWN_DAY_OF_MONTH -> { getCountdownDayOfMonth() }
            TextFormat.KEY_COUNTDOWN_DAY_OF_YEAR -> { getCountdownDayOfYear() }
            TextFormat.KEY_COUNTDOWN_DAY_OF_WEEK -> { getCountdownDayOfWeek() }
            TextFormat.KEY_COUNTDOWN_HOUR -> { getCountdownHour().toString() }
            TextFormat.KEY_COUNTDOWN_HOUR_FULL -> { getCountdownHourFull() }
            TextFormat.KEY_COUNTDOWN_HOUR_DAY -> { getCountdownHourOfDay().toString() }
            TextFormat.KEY_COUNTDOWN_HOUR_DAY_FULL -> { getCountdownHourOfDayFull() }
            TextFormat.KEY_COUNTDOWN_MINUTE -> { getCountdownMinute().toString() }
            TextFormat.KEY_COUNTDOWN_MINUTE_FULL -> { getCountdownMinuteFull() }
            TextFormat.KEY_COUNTDOWN_MINUTE_HOUR -> { getCountdownMinuteOfHour().toString() }
            TextFormat.KEY_COUNTDOWN_MINUTE_HOUR_FULL -> { getCountdownMinuteOfHourFull() }
            else -> { "" }
        }
        cacheMap[key] = value
        return value
    }

    private fun getMonth(): String {
        return (getData(Calendar.MONTH) + 1).toString()
    }

    private fun getMonthFull(): String {
        return (getData(Calendar.MONTH) + 1).fullNumber()
    }

    private fun getMonthJapan(): String {
        return getMonthWithTranslate(R.array.month_jp)
    }

    private fun getMonthEnglish(): String {
        return getMonthWithTranslate(R.array.month_en)
    }

    private fun getMonthChinese(): String {
        return getMonthWithTranslate(R.array.month_cn)
    }

    private fun getMonthTraditional(): String {
        return getMonthWithTranslate(R.array.month_tr)
    }

    private fun getMonthWithTranslate(id: Int): String {
        val month = getData(Calendar.MONTH)
        return context?.resources?.getStringArray(id)?.get(month)?:""
    }

    private fun getWeek(): String {
        return (getData(Calendar.DAY_OF_WEEK)).toString()
    }

    private fun getWeekJapan(): String {
        return getWeekWithTranslate(R.array.day_of_week_jp)
    }

    private fun getWeekEnglish(): String {
        return getWeekWithTranslate(R.array.day_of_week_en)
    }

    private fun getWeekChinese(): String {
        return getWeekWithTranslate(R.array.day_of_week_cn)
    }

    private fun getWeekTraditional(): String {
        return getWeekWithTranslate(R.array.day_of_week_tr)
    }

    private fun getWeekWithTranslate(id: Int): String {
        val week = getData(Calendar.DAY_OF_WEEK) - 1
        return context?.resources?.getStringArray(id)?.get(week)?:""
    }

    private fun getYear(): Int {
        return getData(Calendar.YEAR)
    }

    private fun getDayOfMonth(): String {
        return (getData(Calendar.DAY_OF_MONTH)).toString()
    }

    private fun getDayOfYear(): String {
        return getData(Calendar.DAY_OF_YEAR).toString()
    }

    private fun getDayOfWeek(): String {
        return getData(Calendar.DAY_OF_WEEK).toString()
    }

    private fun getHour(): Int {
        return getData(Calendar.HOUR_OF_DAY)
    }

    private fun getHourFull(): String {
        return getHour().fullNumber()
    }

    private fun getMinute(): Int {
        return getData(Calendar.MINUTE)
    }

    private fun getMinuteFull(): String {
        return getMinute().fullNumber()
    }

    private fun getData(type: Int): Int {
        calendar.timeInMillis = endTime
        return calendar.get(type)
    }

    private fun getCountdownDays(): String {
        val zone = timeZone
        val targetDay = (startTime + zone) / ONE_DAY
        val nowDay = (endTime + zone) / ONE_DAY
        return if (isCountdown) {
            "${targetDay - nowDay}"
        } else {
            "${nowDay - targetDay}"
        }
    }

    private fun getCountdownDayOfMonth(): String {
        return getCountdownDayOfStandard(Calendar.DAY_OF_MONTH).toString()
    }

    private fun getCountdownDayOfYear(): String {
        return getCountdownDayOfStandard(Calendar.DAY_OF_YEAR).toString()
    }

    private fun getCountdownDayOfWeek(): String {
        return getCountdownDayOfStandard(Calendar.DAY_OF_WEEK).toString()
    }

    private fun getCountdownDayOfStandard(standard: Int): Int{
        calendar.timeInMillis = startTime
        val targetDay = calendar.get(standard)
        calendar.timeInMillis = endTime
        val nowDay = calendar.get(standard)
        return if (isCountdown) {
            targetDay - nowDay
        } else {
            nowDay - targetDay
        }
    }

    private fun getCountdownHourOfDay(): Int {
        calendar.timeInMillis = startTime
        val targetHour = calendar.get(Calendar.HOUR_OF_DAY)
        calendar.timeInMillis = endTime
        val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
        return if (isCountdown) {
            targetHour - nowHour
        } else {
            nowHour - targetHour
        }
    }

    private fun getCountdownHourOfDayFull(): String {
        return getCountdownHourOfDay().fullNumber()
    }

    private fun getCountdownHour(): Long {
        val diff = if (isCountdown) {
            startTime - endTime
        } else {
            endTime - startTime
        }
        return diff / ONE_HOUR
    }

    private fun getCountdownHourFull(): String {
        return getCountdownHour().fullNumber()
    }

    private fun getCountdownMinuteOfHour(): Int {
        calendar.timeInMillis = startTime
        val targetMinute = calendar.get(Calendar.MINUTE)
        calendar.timeInMillis = endTime
        val nowMinute = calendar.get(Calendar.MINUTE)
        return if (isCountdown) {
            targetMinute - nowMinute
        } else {
            nowMinute - targetMinute
        }
    }

    private fun getCountdownMinuteOfHourFull(): String {
        return getCountdownMinuteOfHour().fullNumber()
    }

    private fun getCountdownMinute(): Long {
        val targetMinute = startTime
        val nowMinute = endTime
        val diff = if (isCountdown) {
            targetMinute - nowMinute
        } else {
            nowMinute - targetMinute
        }
        return diff / ONE_MINUTE
    }

    private fun getCountdownMinuteFull(): String {
        return getCountdownMinute().fullNumber()
    }

    private val timeZone: Int
        get() {
            return calendar.timeZone.rawOffset
        }

    private fun Int.fullNumber(): String {
        return if (this < 10 || this > -10) {
            if (this >= 0) {
                "0$this"
            } else {
                "-0${abs(this)}"
            }
        } else {
            this.toString()
        }
    }

    private fun Long.fullNumber(): String {
        return if (this < 10 || this > -10) {
            if (this >= 0) {
                "0$this"
            } else {
                "-0${abs(this)}"
            }
        } else {
            this.toString()
        }
    }

    private class TimeInfo(var now: Long = System.currentTimeMillis(),
                           var targetTime: Long = now,
                           var limitTime: Long = INVALID_TIME,
                           var isCountdown: Boolean = true,
                           var cycleType: TimeInfoProvider.CycleType = TimeInfoProvider.CycleType.No)

    fun attach(context: Context) {
        this.context = context
    }

    fun detach() {
        this.context = null
    }

}
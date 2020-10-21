package liang.lollipop.lcountdown.util

import android.content.Context
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.provider.TimeInfoProvider
import java.util.*
import kotlin.collections.HashMap

/**
 * @author lollipop
 * @date 10/18/20 21:15
 * 内容填充工具
 */
class InfoStuffHelper(private val context: Context) {

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

    /**
     * 当前时间
     */
    private var now = System.currentTimeMillis()

    /**
     * 目标时间
     */
    private var targetTime = now

    /**
     * 约束时间
     */
    private var limitTime = INVALID_TIME

    /**
     * 是否是倒计时的形式
     */
    private var isCountdown = true

    /**
     * 循环模式
     */
    private var cycleType = TimeInfoProvider.CycleType.No

    fun updateTarget(info: WidgetInfo) {
        updateTarget(info.targetTime, info.limitTime, info.isCountdown, info.cycleType)
    }

    fun updateTarget(target: Long, limit: Long, countdown: Boolean, cycle: TimeInfoProvider.CycleType) {
        this.targetTime = target
        this.limitTime = limit
        this.isCountdown = countdown
        this.cycleType = cycle
        updateTime()
    }

    /**
     * 更新时间
     * 清除缓存
     */
    fun updateTime() {
        now = System.currentTimeMillis()
        cacheMap.clear()
    }

    fun stuff(value: String): String {
        // TODO
        return value
    }

    private fun replace(value: String, key: String): String {
        return value.replace(key, getValueByKey(key))
    }

    private fun getValueByKey(key: String): String {
        if (cacheMap.containsKey(key)) {
            return cacheMap[key] ?: ""
        }
        val value = when(key) {
            TextFormat.KEY_DAY_WITH_MONTH -> { getDayOfMonth() }
            TextFormat.KEY_DAY_WITH_YEAR -> { getDayOfYear() }
            TextFormat.KEY_DAY_WITH_WEEK -> { getDayOfWeek() }
            TextFormat.KEY_MONTH -> { getMonth() }
            TextFormat.KEY_MONTH_FULL -> { getMonthFull() }
            TextFormat.KEY_MONTH_JAPAN -> { getMonthJapan() }
            TextFormat.KEY_MONTH_ENGLISH -> { getMonthEnglish() }
            TextFormat.KEY_MONTH_CHINESE -> { getMonthChinese() }
            TextFormat.KEY_MONTH_TRADITIONAL -> { getMonthTraditional() }
            TextFormat.KEY_YEAR -> { "" }
            TextFormat.KEY_YEAR_FULL -> { "" }
            TextFormat.KEY_WEEK -> { getWeek() }
            TextFormat.KEY_WEEK_JAPAN -> { getWeekJapan() }
            TextFormat.KEY_WEEK_ENGLISH -> { getWeekEnglish() }
            TextFormat.KEY_WEEK_CHINESE -> { getWeekChinese() }
            TextFormat.KEY_WEEK_TRADITIONAL -> { getWeekTraditional() }
            TextFormat.KEY_HOUR -> { "" }
            TextFormat.KEY_HOUR_FULL -> { "" }
            TextFormat.KEY_MINUTE -> { "" }
            TextFormat.KEY_MINUTE_FULL -> { "" }
            TextFormat.KEY_TIME_CHINA -> { "" }
            TextFormat.KEY_TIME_ENGLISH -> { "" }
            TextFormat.KEY_COUNTDOWN_DAYS -> { getCountdownDays() }
            TextFormat.KEY_COUNTDOWN_DAY_OF_MONTH -> { getCountdownDayOfMonth() }
            TextFormat.KEY_COUNTDOWN_DAY_OF_YEAR -> { getCountdownDayOfYear() }
            TextFormat.KEY_COUNTDOWN_DAY_OF_WEEK -> { getCountdownDayOfWeek() }
            TextFormat.KEY_COUNTDOWN_HOUR -> { "" }
            TextFormat.KEY_COUNTDOWN_HOUR_FULL -> { "" }
            TextFormat.KEY_COUNTDOWN_MINUTE -> { "" }
            TextFormat.KEY_COUNTDOWN_MINUTE_FULL -> { "" }
            else -> {
                ""
            }
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
        return context.resources.getStringArray(id)[month]
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
        val month = getData(Calendar.DAY_OF_WEEK) - 1
        return context.resources.getStringArray(id)[month]
    }

    private fun getDayOfMonth(): String {
        return (getData(Calendar.DAY_OF_MONTH) + 1).toString()
    }

    private fun getDayOfYear(): String {
        return getData(Calendar.DAY_OF_YEAR).toString()
    }

    private fun getDayOfWeek(): String {
        return getData(Calendar.DAY_OF_WEEK).toString()
    }

    private fun getData(type: Int): Int {
        calendar.timeInMillis = now
        return calendar.get(type)
    }

    private fun getCountdownDays(): String {
        val zone = timeZone
        val targetDay = (targetTime + zone) / ONE_DAY
        val nowDay = if (limitTime == INVALID_TIME) {
            (now + zone) / ONE_DAY
        } else {
            (limitTime + zone) / ONE_DAY
        }
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
        calendar.timeInMillis = targetTime
        val targetDay = calendar.get(standard)
        calendar.timeInMillis = now
        val nowDay = calendar.get(standard)
        return if (isCountdown) {
            targetDay - nowDay
        } else {
            nowDay - targetDay
        }
    }

    private val timeZone: Int
        get() {
            return calendar.timeZone.rawOffset
        }

    private fun Int.fullNumber(): String {
        return if (this < 10) {
            "0$this"
        } else {
            this.toString()
        }
    }

}
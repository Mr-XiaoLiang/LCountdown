package liang.lollipop.lcountdown.util

import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.provider.TimeInfoProvider
import java.util.*
import kotlin.collections.HashMap

/**
 * @author lollipop
 * @date 10/18/20 21:15
 * 内容填充工具
 */
class InfoStuffHelper {

    /**
     * 缓存集合
     */
    private val cacheMap = HashMap<String, String>()

    /**
     * 日历类
     */
    private val calendar = Calendar.getInstance()

    /**
     * 目标时间
     */
    private var targetTime = System.currentTimeMillis()

    /**
     * 约束时间
     */
    private var limitTime = -1L

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
        calendar.timeInMillis = System.currentTimeMillis()
        cacheMap.clear()
    }

    fun stuff(value: String): String {

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
            TextFormat.KEY_DAYS -> { "" }
            TextFormat.KEY_DAY_OF_MONTH -> { "" }
            TextFormat.KEY_DAY_OF_YEAR -> { "" }
            TextFormat.KEY_DAY_OF_WEEK -> { "" }
            TextFormat.KEY_DAY_WITH_MONTH -> { "" }
            TextFormat.KEY_DAY_WITH_YEAR -> { "" }
            TextFormat.KEY_DAY_WITH_WEEK -> { "" }
            TextFormat.KEY_MONTH -> { "" }
            TextFormat.KEY_MONTH_FULL -> { "" }
            TextFormat.KEY_MONTH_JAPAN -> { "" }
            TextFormat.KEY_MONTH_ENGLISH -> { "" }
            TextFormat.KEY_YEAR -> { "" }
            TextFormat.KEY_YEAR_FULL -> { "" }
            TextFormat.KEY_WEEK -> { "" }
            TextFormat.KEY_WEEK_JAPAN -> { "" }
            TextFormat.KEY_WEEK_US -> { "" }
            TextFormat.KEY_WEEK_NUM -> { "" }
            TextFormat.KEY_WEEK_CHINA -> { "" }
            TextFormat.KEY_HOUR -> { "" }
            TextFormat.KEY_HOUR_FULL -> { "" }
            TextFormat.KEY_MINUTE -> { "" }
            TextFormat.KEY_MINUTE_FULL -> { "" }
            TextFormat.KEY_TIME_CHINA -> { "" }
            TextFormat.KEY_TIME_ENGLISH -> { "" }
            else -> {
                ""
            }
        }
        cacheMap[key] = value
        return value
    }

}
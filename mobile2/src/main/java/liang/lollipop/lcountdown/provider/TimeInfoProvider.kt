package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 9/27/20 21:23
 */
interface TimeInfoProvider {

    enum class CycleType() {
        No,
        Day,
        Week,
        Month,
        Year;
    }

    var targetTime: Long

    var limitTime: Long

    var isCountdown: Boolean

    var cycleType: CycleType

}
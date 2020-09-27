package liang.lollipop.lcountdown.info

import liang.lollipop.lcountdown.provider.TimeInfoProvider

/**
 * @author lollipop
 * @date 2020/5/26 23:34
 * 小部件的描述信息
 */
class WidgetInfo: JsonInfo(), TimeInfoProvider {

    /**
     * 小部件的ID
     */
    var widgetId: Int by IntDelegate(this, -1)

    /**
     * 序号
     */
    var id: Int by IntDelegate(this, Int.MAX_VALUE)

    /**
     * 宽度
     */
    var width: Int by IntDelegate(this, -1)

    /**
     * 宽度
     */
    var height: Int by IntDelegate(this, -1)


    override var targetTime: Long by LongDelegate(this)

    override var limitTime: Long by LongDelegate(this)

    /**
     * 是否是倒计时的形式
     */
    override var isCountdown: Boolean by BooleanDelegate(this)

    /**
     * 循环模式
     */
    override var cycleType: TimeInfoProvider.CycleType by EnumDelegate(this,
            TimeInfoProvider.CycleType.No) { TimeInfoProvider.CycleType.valueOf(it) }


}
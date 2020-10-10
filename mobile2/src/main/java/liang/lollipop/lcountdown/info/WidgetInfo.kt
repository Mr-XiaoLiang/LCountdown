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
    var width: Float
        get() {
            return backgroundInfo.width
        }
        set(value) {
            backgroundInfo.width = value
        }

    /**
     * 宽度
     */
    var height: Float
        get() {
            return backgroundInfo.height
        }
        set(value) {
            backgroundInfo.height = value
        }

    /**
     * 左边距
     */
    var marginLeft: Int by IntDelegate(this, 0)

    /**
     * 上边距
     */
    var marginTop: Int by IntDelegate(this, 0)

    /**
     * 右边距
     */
    var marginRight: Int by IntDelegate(this, 0)

    /**
     * 下边距
     */
    var marginBottom: Int by IntDelegate(this, 0)

    /**
     * 目标时间
     */
    override var targetTime: Long by LongDelegate(this)

    /**
     * 约束时间
     */
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

    /**
     * 背景信息
     */
    val backgroundInfo: BackgroundInfo by JsonInfoDelegate(this) {
        it.convertTo()
    }

    /**
     * 文本信息
     */
    val textInfoArray: TextInfoArray by JsonArrayDelegate(this) {
        it.convertTo()
    }

}
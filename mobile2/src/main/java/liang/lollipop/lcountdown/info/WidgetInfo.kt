package liang.lollipop.lcountdown.info

/**
 * @author lollipop
 * @date 2020/5/26 23:34
 * 小部件的描述信息
 */
class WidgetInfo: JsonInfo() {

    /**
     * 小部件的ID
     */
    var widgetId: Int by IntDelegate(this)

    /**
     * 序号
     */
    var index: Int by IntDelegate(this, Int.MAX_VALUE)

    /**
     * 倒计时的时间点
     */
    var time: Int by IntDelegate(this)

    /**
     * 宽度
     */
    var width: Int by IntDelegate(this, -1)

    /**
     * 宽度
     */
    var height: Int by IntDelegate(this, -1)

    /**
     * 是否是倒计时的形式
     */
    var isCountdown: Boolean by BooleanDelegate(this)

}
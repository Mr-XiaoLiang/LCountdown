package liang.lollipop.lcountdown.info

/**
 * @author lollipop
 * @date 2020/5/26 23:34
 * 小部件的描述信息
 */
class WidgetInfo: JsonInfo() {

    /**
     * 序号
     */
    var index: Int by IntDelegate(this, Int.MAX_VALUE)

    /**
     * 小部件的ID
     */
    var widgetId: Int by IntDelegate(this)

    /**
     * 小部件名称
     */
    var countdownName: String by StringDelegate(this)

    /**
     * 倒计时结束时间
     */
    var endTime: Int by IntDelegate(this)

    /**
     * 签名内容
     */
    var signValue: String by StringDelegate(this)

    /**
     * 宽度
     */
    var width: Int by IntDelegate(this, -1)

    /**
     * 宽度
     */
    var height: Int by IntDelegate(this, -1)

}
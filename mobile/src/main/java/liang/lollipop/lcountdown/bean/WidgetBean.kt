package liang.lollipop.lcountdown.bean

import liang.lollipop.lbaselib.base.BaseBean
import liang.lollipop.lcountdown.utils.CountdownUtil

/**
 * 小部件的Bean
 * @author Lollipop
 */
class WidgetBean : BaseBean() {

    /**
     * 序号
     */
    var index = Integer.MAX_VALUE

    /**
     * 小部件的ID
     */
    var widgetId = 0
    /**
     * 小部件名称
     */
    var countdownName = ""

    /**
     * 倒计时结束时间
     */
    var endTime = 0L

    /**
     * 签名内容
     */
    var signValue = ""

    /**
     * 小部件样式
     */
    var widgetStyle = WidgetStyle.LIGHT

    /**
     * 是否不显示时间
     */
    var noTime = false

    /**
     * 前缀名
     */
    var prefixName = ""

    /**
     * 后缀名
     */
    var suffixName = ""

    /**
     * 天的单位
     */
    var dayUnit = ""

    /**
     * 小时的单位
     */
    var hourUnit = ""

    /**
     * 前缀名的字体大小
     */
    var prefixFontSize = 16

    /**
     * 倒计时名的字体大小
     */
    var nameFontSize = 24

    /**
     * 后缀名字体大小
     */
    var suffixFontSize = 16

    /**
     * 天的字体大小
     */
    var dayFontSize = 34

    /**
     * 天的单位的字体大小
     */
    var dayUnitFontSize = 16

    /**
     * 小时的字体大小
     */
    var hourFontSize = 32

    /**
     * 小时的单位的字体大小
     */
    var hourUnitFontSize = 12

    /**
     * 时间的字体大小
     */
    var timeFontSize = 18

    /**
     * 签名的字体大小
     */
    var signFontSize = 12

    /**
     * 不使用倒计时的形式
     */
    var noCountdown = false

    /**
     * 获取符合要求的时间结果
     */
    fun getTimerInfo(): CountdownBean {
        return if (noCountdown) {
            CountdownUtil.timer(endTime)
        } else {
            CountdownUtil.countdown(endTime)
        }
    }

    fun parseStyle(value: Int){
        widgetStyle = when(value){

            WidgetStyle.DARK.value -> WidgetStyle.DARK

            WidgetStyle.BLACK.value -> WidgetStyle.BLACK

            WidgetStyle.WHITE.value -> WidgetStyle.WHITE

            else -> WidgetStyle.LIGHT
        }
    }

    fun copy(new: WidgetBean){

        this.index = new.index
        this.widgetId = new.widgetId
        this.countdownName = new.countdownName
        this.endTime = new.endTime
        this.signValue = new.signValue
        this.widgetStyle = new.widgetStyle
        this.noTime = new.noTime
        this.prefixName = new.prefixName
        this.suffixName = new.suffixName
        this.dayUnit = new.dayUnit
        this.hourUnit = new.hourUnit
        this.prefixFontSize = new.prefixFontSize
        this.nameFontSize = new.nameFontSize
        this.suffixFontSize = new.suffixFontSize
        this.dayFontSize = new.dayFontSize
        this.dayUnitFontSize = new.dayUnitFontSize
        this.hourFontSize = new.hourFontSize
        this.hourUnitFontSize = new.hourUnitFontSize
        this.timeFontSize = new.timeFontSize
        this.signFontSize = new.signFontSize
        this.noCountdown = new.noCountdown
    }

}
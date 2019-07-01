package liang.lollipop.lcountdown.bean

import android.text.TextUtils
import android.view.Gravity
import liang.lollipop.lbaselib.base.BaseBean
import liang.lollipop.lcountdown.utils.CountdownUtil
import org.json.JSONObject

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
    @Deprecated("Not used")
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
    @Deprecated("Not used")
    var hourFontSize = 32

    /**
     * 小时的单位的字体大小
     */
    @Deprecated("Not used")
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
     * 按天倒计时
     * 如果为 true，那么表示倒计时只计算
     * 小时以下的倒计时，忽略日期
     */
    var inOneDay = false

    /**
     * 名称的位置
     */
    val nameLocation = Location()

    /**
     * 前缀的位置
     */
    val prefixLocation = Location()

    /**
     * 后缀的位置
     */
    val suffixLocation = Location()

    /**
     * 天数的位置
     */
    val daysLocation = Location()

    /**
     * 单位的位置
     */
    val unitLocation = Location()

    /**
     * 时间的位置
     */
    val timeLocation = Location()

    /**
     * 签名的位置
     */
    val inscriptionLocation = Location()

    /**
     * 获取符合要求的时间结果
     */
    fun getTimerInfo(): CountdownBean {
        return if (noCountdown) {
            CountdownUtil.timer(endTime, inOneDay)
        } else {
            CountdownUtil.countdown(endTime, inOneDay)
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
        this.inOneDay = new.inOneDay
        this.nameLocation.copy(new.nameLocation)
        this.prefixLocation.copy(new.prefixLocation)
        this.suffixLocation.copy(new.suffixLocation)
        this.daysLocation.copy(new.daysLocation)
        this.unitLocation.copy(new.unitLocation)
        this.timeLocation.copy(new.timeLocation)
        this.inscriptionLocation.copy(new.inscriptionLocation)
    }

    fun parseLocation(json: String?) {
        if (TextUtils.isEmpty(json)) {
            val empty = JSONObject()
            nameLocation.parse(empty)
            prefixLocation.parse(empty)
            suffixLocation.parse(empty)
            daysLocation.parse(empty)
            unitLocation.parse(empty)
            timeLocation.parse(empty)
            inscriptionLocation.parse(empty)
            return
        }
        val obj = JSONObject(json)
        obj.optJSONObject("nameLocation")?.let {
            nameLocation.parse(it)
        }
        obj.optJSONObject("prefixLocation")?.let {
            prefixLocation.parse(it)
        }
        obj.optJSONObject("suffixLocation")?.let {
            suffixLocation.parse(it)
        }
        obj.optJSONObject("daysLocation")?.let {
            daysLocation.parse(it)
        }
        obj.optJSONObject("unitLocation")?.let {
            unitLocation.parse(it)
        }
        obj.optJSONObject("timeLocation")?.let {
            timeLocation.parse(it)
        }
        obj.optJSONObject("inscriptionLocation")?.let {
            inscriptionLocation.parse(it)
        }
    }

    fun serializationLocation(): String {
        val obj = JSONObject()
        obj.put("nameLocation", nameLocation.serialization())
        obj.put("prefixLocation", prefixLocation.serialization())
        obj.put("suffixLocation", suffixLocation.serialization())
        obj.put("daysLocation", daysLocation.serialization())
        obj.put("unitLocation", unitLocation.serialization())
        obj.put("timeLocation", timeLocation.serialization())
        obj.put("inscriptionLocation", inscriptionLocation.serialization())
        return obj.toString()
    }

    class Location(var gravity: Int = Gravity.NO_GRAVITY,
                   var verticalMargin: Float = 0F,
                   var horizontalMargin: Float = 0F) {

        fun copy(new: Location) {
            this.gravity = new.gravity
            this.verticalMargin = new.verticalMargin
            this.horizontalMargin = new.horizontalMargin
        }

        override fun toString(): String {
            return serialization().toString()
        }

        fun parse(obj: JSONObject) {
            gravity = obj.optInt("gravity", Gravity.NO_GRAVITY)
            verticalMargin = obj.optDouble("verticalMargin", 0.0).toFloat()
            horizontalMargin = obj.optDouble("horizontalMargin", 0.0).toFloat()
        }

        fun serialization(): JSONObject {
            val obj = JSONObject()
            obj.put("gravity", gravity)
            obj.put("verticalMargin", verticalMargin)
            obj.put("horizontalMargin", horizontalMargin)
            return obj
        }
    }

}
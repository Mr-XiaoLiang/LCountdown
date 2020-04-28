package liang.lollipop.lcountdown.bean

import android.graphics.Color
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

    companion object {
        val EMPTY_LOCATION = Location()
    }

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
    var repeatType = RepeatType.None

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
     * 名称的颜色
     */
    var nameColor = Color.WHITE

    /**
     * 前缀的颜色
     */
    var prefixColor = Color.WHITE

    /**
     * 后缀的颜色
     */
    var suffixColor = Color.WHITE

    /**
     * 天数的颜色
     */
    var daysColor = Color.WHITE

    /**
     * 单位的颜色
     */
    var unitColor = Color.WHITE

    /**
     * 时间的颜色
     */
    var timeColor = Color.WHITE

    /**
     * 签名的颜色
     */
    var inscriptionColor = Color.WHITE

    /**
     * 是否单日计时
     */
    val inOneDay: Boolean
        get() {
            return repeatType == RepeatType.Day
        }

    /**
     * 位置信息的序列化
     */
    var locations: String
        set(value) {
            parseLocation(value)
        }
        get() {
            return serializationLocation()
        }

    /**
     * 颜色信息的序列化
     */
    var colors: String
        set(value) {
            parseColor(value)
        }
        get() {
            return serializationColor()
        }

    /**
     * 获取符合要求的时间结果
     */
    fun getTimerInfo(): CountdownBean {
        return if (noCountdown) {
            CountdownUtil.timer(endTime, repeatType)
        } else {
            CountdownUtil.countdown(endTime, repeatType)
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
        this.repeatType = new.repeatType
        this.nameLocation.copy(new.nameLocation)
        this.prefixLocation.copy(new.prefixLocation)
        this.suffixLocation.copy(new.suffixLocation)
        this.daysLocation.copy(new.daysLocation)
        this.unitLocation.copy(new.unitLocation)
        this.timeLocation.copy(new.timeLocation)
        this.inscriptionLocation.copy(new.inscriptionLocation)

        this.nameColor = new.nameColor
        this.prefixColor = new.prefixColor
        this.suffixColor = new.suffixColor
        this.daysColor = new.daysColor
        this.unitColor = new.unitColor
        this.timeColor = new.timeColor
        this.inscriptionColor = new.inscriptionColor
    }

    private fun parseLocation(json: String?) {
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
        val obj = optObj(json)
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

    private fun serializationLocation(): String {
        val obj = JSONObject()
        obj.put("nameLocation",        nameLocation.serialization())
        obj.put("prefixLocation",      prefixLocation.serialization())
        obj.put("suffixLocation",      suffixLocation.serialization())
        obj.put("daysLocation",        daysLocation.serialization())
        obj.put("unitLocation",        unitLocation.serialization())
        obj.put("timeLocation",        timeLocation.serialization())
        obj.put("inscriptionLocation", inscriptionLocation.serialization())
        return obj.toString()
    }

    private fun parseColor(json: String?) {
        val obj = optObj(json)
        val defColor = if (widgetStyle == WidgetStyle.BLACK || widgetStyle == WidgetStyle.LIGHT) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        this.nameColor        = obj.optInt("nameColor",        defColor)
        this.prefixColor      = obj.optInt("prefixColor",      defColor)
        this.suffixColor      = obj.optInt("suffixColor",      defColor)
        this.daysColor        = obj.optInt("daysColor",        defColor)
        this.unitColor        = obj.optInt("unitColor",        defColor)
        this.timeColor        = obj.optInt("timeColor",        defColor)
        this.inscriptionColor = obj.optInt("inscriptionColor", defColor)
    }

    private fun serializationColor(): String {
        val obj = JSONObject()
        obj.put("nameColor",        nameColor)
        obj.put("prefixColor",      prefixColor)
        obj.put("suffixColor",      suffixColor)
        obj.put("daysColor",        daysColor)
        obj.put("unitColor",        unitColor)
        obj.put("timeColor",        timeColor)
        obj.put("inscriptionColor", inscriptionColor)
        return obj.toString()
    }

    private fun optObj(json: String?): JSONObject {
        if (TextUtils.isEmpty(json)) {
            return JSONObject()
        }
        return try {
            JSONObject(json!!)
        } catch (e: Exception) {
            JSONObject()
        }
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
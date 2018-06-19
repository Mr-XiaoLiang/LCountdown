package liang.lollipop.lcountdown.bean

/**
 * 倒计时的Bean，当倒计时计算完成后的结果返回值
 * @author Lollipop
 */
class CountdownBean {

    var days: String = ""
    var hours: String = ""
    var minutes = ""
    var seconds = ""
    var time: String = ""

    var dayInt = 0
    var hourInt = 0
    var minuteInt = 0
    var secondInt = 0

    fun getTimerValue(): String{
        val value = StringBuilder()
        value.append(days)
        value.append(".")
        value.append(hours)
        value.append(".")
        value.append(minutes)
        value.append(".")
        value.append(seconds)
        return value.toString()
    }

}
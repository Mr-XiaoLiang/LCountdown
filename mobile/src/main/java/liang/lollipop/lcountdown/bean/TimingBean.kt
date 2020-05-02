package liang.lollipop.lcountdown.bean

import android.graphics.Color
import android.text.TextUtils
import liang.lollipop.lbaselib.base.BaseBean
import liang.lollipop.lcountdown.utils.StringToColorUtil

/**
 * 计时的bean
 * @author Lollipop
 */
class TimingBean constructor(var id: Int = 0) : BaseBean() {

    var name = ""

    var isCountdown = false

    var startTime = 0L

    var endTime = 0L

    private var lastName = name

    private var lastStartTime = startTime

    private var lastColor = getBeanColor()

    private fun getBeanColor(): Int {
        val value = if (TextUtils.isEmpty(name)) {
            "$startTime"
        } else {
            name
        }
        return StringToColorUtil.format(value)

    }

    val color: Int
        get() {

            if (name != lastName || lastStartTime != startTime) {
                lastColor = getBeanColor()
            }

            return lastColor
        }

    val invertedColor: Int
        get() {

            if (name != lastName || lastStartTime != startTime) {
                lastColor = getBeanColor()

            }

            val gray = getGray(lastColor).inverted()

            return Color.rgb(gray, gray, gray)

        }

    private fun Int.inverted(): Int {
        val color = 255 - this

        if (color in 65..127) {
            return color - 64
        }

        if (color in 128..191) {
            return color + 64
        }

        return color
    }

    private fun getGray(pixel: Int): Int {
        return (Color.red(pixel) * 30
                + Color.green(pixel) * 60
                + Color.blue(pixel) * 10) / 100
    }
}
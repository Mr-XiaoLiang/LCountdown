package liang.lollipop.lcountdown.bean

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import liang.lollipop.lbaselib.base.BaseBean
import liang.lollipop.lcountdown.utils.StringToColorUtil

/**
 * 计时的bean
 * @author Lollipop
 */
class TimingBean constructor(var id: Int = 0) : BaseBean() {

    companion object {
        private const val ID = "TIMING_BEAN_ID"
        private const val NAME = "TIMING_BEAN_NAME"
        private const val COUNTDOWN = "TIMING_BEAN_COUNTDOWN"
        private const val START = "TIMING_BEAN_START"
        private const val END = "TIMING_BEAN_END"
    }

    var name = ""

    var isCountdown = false

    var startTime = 0L

    var endTime = 0L

    fun bindToIntent(intent: Intent) {
        intent.putExtra(ID, id)
        intent.putExtra(NAME, name)
        intent.putExtra(COUNTDOWN, isCountdown)
        intent.putExtra(START, startTime)
        intent.putExtra(END, endTime)
    }

    fun getFromIntent(intent: Intent) {
        id = intent.getIntExtra(ID, 0)
        name = intent.getStringExtra(NAME)?:""
        isCountdown = intent.getBooleanExtra(COUNTDOWN, false)
        startTime = intent.getLongExtra(START, 0L)
        endTime = intent.getLongExtra(END, 0L)
    }

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
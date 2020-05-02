package liang.lollipop.lcountdown.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_quick_timing.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.utils.TimingUtil
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

/**
 * 快速计时Activity
 * @author Lollipop
 */
class QuickTimingActivity : BaseActivity() {

    private var startTime = System.currentTimeMillis()

    companion object {

        const val RESULT_TIMING_ID = "RESULT_TIMING_ID"

        const val QUIET_BTN_TRANSITION = "QUIET_BTN"

    }

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_timing)

        init()
    }

    private fun init() {

        calendar.timeInMillis = startTime

        yearInputView.setText(calendar.get(Calendar.YEAR).format(4))
        monthInputView.setText(calendar.get(Calendar.MONTH).format())
        dayInputView.setText(calendar.get(Calendar.DAY_OF_MONTH).format())
        hourInputView.setText(calendar.get(Calendar.HOUR_OF_DAY).format())
        minuteInputView.setText(calendar.get(Calendar.MINUTE).format())
        secondsInputView.setText(calendar.get(Calendar.SECOND).format())
        millisecondInputView.setText(calendar.get(Calendar.MILLISECOND).format(3))

        bindClick(rootGroup, timingBtn, countdownBtn, infoBody)
    }

    private fun Int.format(length: Int = 2): String {
        val builder = StringBuilder()
        builder.append(this)
        while (builder.length < length) {
            builder.insert(0, "0")
        }
        return builder.toString()
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v) {

            rootGroup -> onBackPressed()

            timingBtn -> {
                saveTiming(false)
                rootGroup.callOnClick()
            }

            countdownBtn -> {
                saveTiming(true)
                rootGroup.callOnClick()
            }

        }

    }

    private fun TextInputEditText.getInt(): Int {
        return try {
            text.toString().toInt()
        } catch (e: Exception) {
            0
        }
    }

    private fun saveTiming(isCountdown: Boolean) {
        calendar.set(Calendar.YEAR, yearInputView.getInt())
        calendar.set(Calendar.MONTH, monthInputView.getInt())
        calendar.set(Calendar.DAY_OF_MONTH, dayInputView.getInt())
        calendar.set(Calendar.HOUR_OF_DAY, hourInputView.getInt())
        calendar.set(Calendar.MINUTE, minuteInputView.getInt())
        calendar.set(Calendar.SECOND, secondsInputView.getInt())
        calendar.set(Calendar.MILLISECOND, millisecondInputView.getInt())
        startTime = calendar.timeInMillis

        val name = timingNameEdit.text.toString().trim()
        val bean = TimingBean()
        bean.name = name
        bean.startTime = startTime
        bean.isCountdown = isCountdown

        TimingUtil.startTiming(this, bean)
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESULT_TIMING_ID, bean.id)
        })
    }

}

package liang.lollipop.lcountdown.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_time_calculator.*
import kotlinx.android.synthetic.main.content_time_calculator.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lcountdown.R
import java.lang.Exception
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * 时间计算器
 * @author Lollipop
 */
class TimeCalculatorActivity : BaseActivity() {

    companion object {
        private const val RESULT_FORMAT = 0
        private const val RESULT_YEAR = 1
        private const val RESULT_MONTH = 2
        private const val RESULT_WEEK = 3
        private const val RESULT_DAY = 4
        private const val RESULT_HOUR = 5
        private const val RESULT_MINUTE = 6
        private const val RESULT_SECONDS = 7
        private const val RESULT_MILLISECOND = 8

        private const val FORMAT_COUNT = RESULT_MILLISECOND + 1

        private const val ONE_MILLISECOND = 1L
        private const val ONE_SECONDS = ONE_MILLISECOND * 1000
        private const val ONE_MINUTE = ONE_SECONDS * 60
        private const val ONE_HOUR = ONE_MINUTE * 60
        private const val ONE_DAY = ONE_HOUR * 24
        private const val ONE_WEEK = ONE_DAY * 7
        private const val ONE_MONTH = ONE_DAY * 30
        private const val ONE_YEAR = ONE_DAY * 365

        private const val BORDER_ANIMATOR_DURATION = 300L
    }

    // 开始时间
    private var startTime = System.currentTimeMillis()
    // 结束时间
    private var endTime = System.currentTimeMillis() + 10000000000
    // 选中的是开始时间
    private var selectedStart = true
    // 返回值格式化类型
    private var resultFormat = RESULT_FORMAT
    // 输入的view
    private val inputViewArray: Array<TextInputEditText> by lazy {
        arrayOf(yearInputView,
                monthInputView,
                dayInputView,
                hourInputView,
                minuteInputView,
                secondsInputView,
                millisecondInputView)
    }
    // 输入框锁
    private var inputLock = false
    // 选中框的偏移量
    private var selectedBorderProgress = 0F
    // 边框动画的对象
    private val borderAnimator = ValueAnimator().apply {
        addUpdateListener {
            selectedBorderProgress = it.animatedValue as Float
            updateBorderLocation()
        }
    }
    // 浮点数格式化工具
    private val decimalFormat = DecimalFormat("#,##0.00###")

    // 日历计算器
    private val calendar = Calendar.getInstance()

    // 时间格式化工具
    private val simpleDateFormat = SimpleDateFormat("YYYY-MM-dd\nHH:mm:ss.SSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_calculator)
        initView()
    }

    private fun initView() {
        bindClick(startTimeCopyBtn, endTimeCopyBtn,
                refreshResultBtn, restoreBtn, startTimeView, endTimeView)
        inputViewArray.forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    onInputChange()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
        restoreBtn.callOnClick()
        startTimeView.updateTime(startTime)
        endTimeView.updateTime(endTime)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            startTimeCopyBtn -> {
                copyTimes(startTime)
            }
            endTimeCopyBtn -> {
                copyTimes(endTime)
            }
            refreshResultBtn -> {
                resultFormat++
                resultFormat %= FORMAT_COUNT
                onResultValueChange()
            }
            restoreBtn -> {
                inputLock = true
                inputViewArray.forEach {
                    it.setText("0.00")
                    it.clearFocus()
                }
                inputLock = false
                onInputChange()
            }
            startTimeView -> {
                if (!selectedStart) {
                    selectedStart = true
                    onSelectChange()
                }
            }
            endTimeView -> {
                if (selectedStart) {
                    selectedStart = false
                    onSelectChange()
                }
            }
        }
    }

    private fun onInputChange() {
        if (inputLock) {
            return
        }
        val targetTime = if (selectedStart) {
            startTime
        } else  {
            endTime
        }
        calendar.timeInMillis = targetTime
        calendar.add(Calendar.YEAR, yearInputView.getInt())
        calendar.add(Calendar.MONTH, monthInputView.getInt())
        calendar.add(Calendar.DAY_OF_MONTH, dayInputView.getInt())
        calendar.add(Calendar.HOUR_OF_DAY, hourInputView.getInt())
        calendar.add(Calendar.MINUTE, minuteInputView.getInt())
        calendar.add(Calendar.SECOND, secondsInputView.getInt())
        calendar.add(Calendar.MILLISECOND, millisecondInputView.getInt())
        val resultTime = calendar.timeInMillis
        if (selectedStart) {
            endTime = resultTime
        } else  {
            startTime = resultTime
        }
        startTimeView.updateTime(startTime)
        endTimeView.updateTime(endTime)
        onResultValueChange()
    }

    private fun TextView.updateTime(time: Long) {
        this.text = simpleDateFormat.format(Date(time))
    }

    private fun Int.format(): String {
        return if (this < 10) {
            "0$this"
        } else {
            this.toString()
        }
    }

    private fun TextInputEditText.getInt() : Int {
        val value = this.text?.toString()?:""
        if (TextUtils.isEmpty(value)) {
            return 0
        }
        return try {
            value.toInt()
        } catch (e: Exception) {
            0
        }
    }

    private fun onResultValueChange() {
        val offset = abs(startTime - endTime)
        val value = when (resultFormat) {
            RESULT_YEAR -> {
                v(offset * 1F / ONE_YEAR) + getString(R.string.year)
            }
            RESULT_MONTH -> {
                v(offset * 1F / ONE_MONTH) + getString(R.string.month)
            }
            RESULT_WEEK -> {
                v(offset * 1F / ONE_WEEK) + getString(R.string.week)
            }
            RESULT_DAY -> {
                v(offset * 1F / ONE_DAY) + getString(R.string.day)
            }
            RESULT_HOUR -> {
                v(offset * 1F / ONE_HOUR) + getString(R.string.hour)
            }
            RESULT_MINUTE -> {
                v(offset * 1F / ONE_MINUTE) + getString(R.string.minute)
            }
            RESULT_SECONDS -> {
                v(offset * 1F / ONE_SECONDS) + getString(R.string.seconds)
            }
            RESULT_MILLISECOND -> {
               v(offset * 1F) + getString(R.string.millisecond)
            }
            else  -> {
                val builder = StringBuilder()
                builder.append(v(offset / ONE_YEAR))
                builder.append(getString(R.string.year))
                builder.append(" ")
                builder.append(v(offset % ONE_YEAR / ONE_MONTH))
                builder.append(getString(R.string.month))
                builder.append(" ")
                builder.append(v(offset % ONE_MONTH / ONE_DAY))
                builder.append(getString(R.string.day))
                builder.append(" ")
                builder.append(v(offset % ONE_DAY / ONE_HOUR))
                builder.append(getString(R.string.hour))
                builder.append(" ")
                builder.append(v(offset % ONE_HOUR / ONE_MINUTE))
                builder.append(getString(R.string.minute))
                builder.append(" ")
                builder.append(v(offset % ONE_MINUTE / ONE_SECONDS))
                builder.append(getString(R.string.seconds))
                builder.append(" ")
                builder.append(v(offset % ONE_SECONDS / ONE_MILLISECOND))
                builder.append(getString(R.string.millisecond))
                builder.toString()
            }
        }
        resultView.text = value
    }

    private fun copyTimes(time: Long) {
        // TODO
    }

    private fun onSelectChange() {
        borderAnimator.cancel()
        val targetProgress = if (selectedStart) { 0F } else { 1F }
        borderAnimator.setFloatValues(selectedBorderProgress, targetProgress)
        borderAnimator.duration = (BORDER_ANIMATOR_DURATION *
                abs(targetProgress - selectedBorderProgress)).toLong()
        borderAnimator.start()

        val offset = if (selectedStart) {
            endTime - startTime
        } else  {
            startTime - endTime
        }

        onInputChange()

        inputLock = true
        yearInputView.setText(v(offset / ONE_YEAR))
        monthInputView.setText(v(offset % ONE_YEAR / ONE_MONTH))
        dayInputView.setText(v(offset % ONE_MONTH / ONE_DAY))
        hourInputView.setText(v(offset % ONE_DAY / ONE_HOUR))
        minuteInputView.setText(v(offset % ONE_HOUR / ONE_MINUTE))
        secondsInputView.setText(v(offset % ONE_MINUTE / ONE_SECONDS))
        millisecondInputView.setText(v(offset % ONE_SECONDS / ONE_MILLISECOND))
        inputLock = false
    }

    private fun updateBorderLocation() {
        val height = endTimeView.top - startTimeView.top
        selectedBorder.translationY = height * selectedBorderProgress
    }

    override fun onDestroy() {
        super.onDestroy()
        borderAnimator.cancel()
    }

    private fun v(float: Float): String {
        return decimalFormat.format(float)
    }

    private fun v(float: Long): String {
        return decimalFormat.format(float)
    }

}

package liang.lollipop.lcountdown.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseActivity
import liang.lollipop.lcountdown.databinding.ActivityTimeCalculatorBinding
import liang.lollipop.lcountdown.utils.ClipboardHelper
import liang.lollipop.lcountdown.utils.lazyBind
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

    private val binding: ActivityTimeCalculatorBinding by lazyBind()

    // 输入的view
    private val inputViewArray: Array<TextInputEditText> by lazy {
        arrayOf(
            binding.content.yearInputView,
            binding.content.monthInputView,
            binding.content.dayInputView,
            binding.content.hourInputView,
            binding.content.minuteInputView,
            binding.content.secondsInputView,
            binding.content.millisecondInputView
        )
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
        setContentView(binding.root)
        setToolbar(binding.toolbar)
        initView()
    }

    private fun initView() {
        bindClick(
            binding.startTimeCopyBtn,
            binding.endTimeCopyBtn,
            binding.refreshResultBtn,
            binding.content.restoreBtn,
            binding.startTimeView,
            binding.endTimeView
        )
        inputViewArray.forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    onInputChange()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
        binding.content.restoreBtn.callOnClick()
        binding.startTimeView.updateTime(startTime)
        binding.endTimeView.updateTime(endTime)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calculator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menuHelp -> {
                alert().setMessage(R.string.help_calculator).show()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.startTimeCopyBtn -> {
                copyTimes(startTime)
            }

            binding.endTimeCopyBtn -> {
                copyTimes(endTime)
            }

            binding.refreshResultBtn -> {
                resultFormat++
                resultFormat %= FORMAT_COUNT
                onResultValueChange()
            }

            binding.content.restoreBtn -> {
                inputLock = true
                inputViewArray.forEach {
                    it.setText("0")
                    it.clearFocus()
                }
                inputLock = false
                onInputChange()
            }

            binding.startTimeView -> {
                if (!selectedStart) {
                    selectedStart = true
                    onSelectChange()
                }
            }

            binding.endTimeView -> {
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
        } else {
            endTime
        }
        calendar.timeInMillis = targetTime
        calendar.add(Calendar.YEAR, binding.content.yearInputView.getInt())
        calendar.add(Calendar.MONTH, binding.content.monthInputView.getInt())
        calendar.add(Calendar.DAY_OF_MONTH, binding.content.dayInputView.getInt())
        calendar.add(Calendar.HOUR_OF_DAY, binding.content.hourInputView.getInt())
        calendar.add(Calendar.MINUTE, binding.content.minuteInputView.getInt())
        calendar.add(Calendar.SECOND, binding.content.secondsInputView.getInt())
        calendar.add(Calendar.MILLISECOND, binding.content.millisecondInputView.getInt())
        val resultTime = calendar.timeInMillis
        if (selectedStart) {
            endTime = resultTime
        } else {
            startTime = resultTime
        }
        binding.startTimeView.updateTime(startTime)
        binding.endTimeView.updateTime(endTime)
        onResultValueChange()
    }

    private fun TextView.updateTime(time: Long) {
        this.text = simpleDateFormat.format(Date(time))
    }

    private fun TextInputEditText.getInt(): Int {
        val value = this.text?.toString() ?: ""
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
                v(offset * 1.0 / ONE_YEAR) + getString(R.string.year)
            }

            RESULT_MONTH -> {
                v(offset * 1.0 / ONE_MONTH) + getString(R.string.month)
            }

            RESULT_WEEK -> {
                v(offset * 1.0 / ONE_WEEK) + getString(R.string.week)
            }

            RESULT_DAY -> {
                v(offset * 1.0 / ONE_DAY) + getString(R.string.day)
            }

            RESULT_HOUR -> {
                v(offset * 1.0 / ONE_HOUR) + getString(R.string.hour)
            }

            RESULT_MINUTE -> {
                v(offset * 1.0 / ONE_MINUTE) + getString(R.string.minute)
            }

            RESULT_SECONDS -> {
                v(offset * 1.0 / ONE_SECONDS) + getString(R.string.seconds)
            }

            RESULT_MILLISECOND -> {
                v(offset * 1.0) + getString(R.string.millisecond)
            }

            else -> {
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
        binding.resultView.text = value
    }

    private fun copyTimes(time: Long) {
        if (ClipboardHelper.put(this, ClipboardHelper.encodeTimestamp(time))) {
            alert().setTitle(R.string.title_copy_times)
                .setMessage(R.string.msg_copy_times)
                .setPositiveButton(R.string.understood) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            alert().setMessage(R.string.copy_error)
                .setPositiveButton(R.string.understood) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun onSelectChange() {
        borderAnimator.cancel()
        val targetProgress = if (selectedStart) {
            0F
        } else {
            1F
        }
        borderAnimator.setFloatValues(selectedBorderProgress, targetProgress)
        borderAnimator.duration = (BORDER_ANIMATOR_DURATION *
                abs(targetProgress - selectedBorderProgress)).toLong()
        borderAnimator.start()

        val offset = if (selectedStart) {
            endTime - startTime
        } else {
            startTime - endTime
        }

        inputLock = true
        binding.content.dayInputView.setText((offset / ONE_DAY).toString())
        binding.content.hourInputView.setText((offset % ONE_DAY / ONE_HOUR).toString())
        binding.content.minuteInputView.setText((offset % ONE_HOUR / ONE_MINUTE).toString())
        binding.content.secondsInputView.setText((offset % ONE_MINUTE / ONE_SECONDS).toString())
        binding.content.millisecondInputView.setText((offset % ONE_SECONDS / ONE_MILLISECOND).toString())
        inputLock = false
    }

    private fun updateBorderLocation() {
        val height = binding.endTimeView.top - binding.startTimeView.top
        binding.selectedBorder.translationY = height * selectedBorderProgress
    }

    override fun onDestroy() {
        super.onDestroy()
        borderAnimator.cancel()
    }

    private fun v(float: Double): String {
        return decimalFormat.format(float)
    }

    private fun v(long: Long): String {
        return long.toString()
    }

}

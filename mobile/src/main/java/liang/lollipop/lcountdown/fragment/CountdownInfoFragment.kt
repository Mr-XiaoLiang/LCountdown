package liang.lollipop.lcountdown.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.fragment_countdown_info.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.RepeatType
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.drawable.StyleSelectedForeDrawable
import liang.lollipop.lcountdown.utils.CountdownUtil
import java.util.*
import kotlin.math.abs

/**
 * @date: 2018/6/21 19:48
 * @author: lollipop
 *
 * 倒计时信息的Fragment
 */
class CountdownInfoFragment : LTabFragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var callback: Callback

    private lateinit var style1BtnBG: StyleSelectedForeDrawable
    private lateinit var style2BtnBG: StyleSelectedForeDrawable
    private lateinit var style3BtnBG: StyleSelectedForeDrawable
    private lateinit var style4BtnBG: StyleSelectedForeDrawable

    private var widgetStyle = WidgetStyle.LIGHT

    private val calendar = Calendar.getInstance()

    private var isReady = false

    private var repeatType = RepeatType.None

    override fun getTitleId(): Int {
        return R.string.title_base_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_mode_edit_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.baseTabSelected
    }

    companion object {

        private const val ARG_NAME = "ARG_NAME"
        private const val ARG_SIGN = "ARG_SIGN"
        private const val ARG_TIME = "ARG_TIME"
        private const val ARG_NO_TIME = "ARG_NO_TIME"
        private const val ARG_STYLE = "ARG_STYLE"
        private const val ARG_NO_COUNTDOWN = "ARG_NO_COUNTDOWN"
        private const val ARG_REPEAT_TYPE = "ARG_REPEAT_TYPE"

    }

    fun reset(widgetBean: WidgetBean) {

        arguments = (arguments ?: Bundle()).apply {

            putString(ARG_NAME, widgetBean.countdownName)
            putString(ARG_SIGN, widgetBean.signValue)
            putLong(ARG_TIME, widgetBean.endTime)
            putBoolean(ARG_NO_TIME, widgetBean.noTime)
            putInt(ARG_STYLE, widgetBean.widgetStyle.value)
            putBoolean(ARG_NO_COUNTDOWN, widgetBean.noCountdown)
            putInt(ARG_REPEAT_TYPE, widgetBean.repeatType.ordinal)

        }

        initView()

    }

    private fun initView() {
        if (!isReady) {
            return
        }

        arguments?.let {

            nameInputView.setText(it.getString(ARG_NAME, ""))

            signInputView.setText(it.getString(ARG_SIGN, ""))

            calendar.timeInMillis = it.getLong(ARG_TIME, System.currentTimeMillis())
            onEndTimeChange()

            noTimeCheckBox.isChecked = it.getBoolean(ARG_NO_TIME, false)
            timingTypeCheckBox.isChecked = it.getBoolean(ARG_NO_COUNTDOWN, false)

            repeatType = RepeatType.values()[it.getInt(ARG_REPEAT_TYPE, RepeatType.None.ordinal)]
            when (repeatType) {
                RepeatType.None -> {
                    notRepeatBtn.isChecked = true
                }
                RepeatType.Day -> {
                    dayRepeatBtn.isChecked = true
                }
                RepeatType.Month -> {
                    monthRepeatBtn.isChecked = true
                }
                RepeatType.Week -> {
                    weekRepeatBtn.isChecked = true
                }
            }

            onStyleChange(it.getInt(ARG_STYLE, WidgetStyle.BLACK.value).let { style ->
                when (style) {
                    WidgetStyle.DARK.value -> WidgetStyle.DARK
                    WidgetStyle.LIGHT.value -> WidgetStyle.LIGHT
                    WidgetStyle.WHITE.value -> WidgetStyle.WHITE
                    else -> WidgetStyle.BLACK
                }
            })

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_info, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            callback = context
        } else {
            throw RuntimeException("Can't find CountdownInfoFragment.Callback")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameInputView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback.onNameInfoChange(s ?: "")
            }
        })

        signInputView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback.onSignInfoChange(s ?: "")
            }
        })

        dateSelectView.setOnClickListener(this)
        timeSelectView.setOnClickListener(this)

        style1BtnBG = StyleSelectedForeDrawable(context!!)
        style2BtnBG = StyleSelectedForeDrawable(context!!)
        style3BtnBG = StyleSelectedForeDrawable(context!!, R.drawable.bg_black)
        style4BtnBG = StyleSelectedForeDrawable(context!!, R.drawable.bg_white)

        style1Btn.setOnClickListener(this)
        style1Btn.background = style1BtnBG
        style2Btn.setOnClickListener(this)
        style2Btn.background = style2BtnBG
        style3Btn.setOnClickListener(this)
        style3Btn.background = style3BtnBG
        style4Btn.setOnClickListener(this)
        style4Btn.background = style4BtnBG

        noTimeCheckBox.setOnCheckedChangeListener(this)
        timingTypeCheckBox.setOnCheckedChangeListener(this)
        repeatGroup.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                R.id.dayRepeatBtn -> RepeatType.Day
                R.id.weekRepeatBtn -> RepeatType.Week
                R.id.monthRepeatBtn -> RepeatType.Month
                else -> RepeatType.None
            }
            repeatType = type
            dateSelectView.isEnabled = type != RepeatType.Day
            callback.onRepeatTypeChange(type)
            onEndTimeChange()
        }

        isReady = true

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        isReady = false
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView) {
            noTimeCheckBox -> {
                callback.onTimeTypeChange(isChecked)
            }
            timingTypeCheckBox -> {
                callback.onTimingTypeChange(isChecked)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            dateSelectView -> {
                val yearIn = calendar.get(Calendar.YEAR)
                val monthIn = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onEndTimeChange()
                }, yearIn, monthIn, day).show()
            }

            timeSelectView -> {
                val hourIn = calendar.get(Calendar.HOUR_OF_DAY)
                val minuteIn = calendar.get(Calendar.MINUTE)
                TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    onEndTimeChange()
                }, hourIn, minuteIn, false).show()
            }

            style1Btn -> {
                onStyleChange(WidgetStyle.LIGHT)
            }

            style2Btn -> {
                onStyleChange(WidgetStyle.DARK)
            }

            style3Btn -> {
                onStyleChange(WidgetStyle.BLACK)
            }

            style4Btn -> {
                onStyleChange(WidgetStyle.WHITE)
            }

        }

    }

    private fun onEndTimeChange() {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        dateSelectView.text = when (repeatType) {
            RepeatType.Week -> {
                resources.getStringArray(R.array.week_name)[CountdownUtil.timeToWeekDay(calendar.timeInMillis) - 1]
            }

            RepeatType.Month -> {
                "" + CountdownUtil.timeToMonthDay(calendar.timeInMillis) + getString(R.string.day_unit2)
            }

            else -> {
                ("${year.formatNumber()}-${month.formatNumber()}-${day.formatNumber()}")
            }
        }
        timeSelectView.text = ("${hour.formatNumber()} : ${minute.formatNumber()}")

        callback.onTimeInfoChange(calendar.timeInMillis)

    }

    private fun Int.formatNumber(): String {
        return when {
            this > 9 -> "" + this
            this < -9 -> "-" + abs(this)
            this < 0 -> "-0" + abs(this)
            else -> "0$this"
        }
    }

    private fun onStyleChange(style: WidgetStyle) {
        widgetStyle = style
        style1BtnBG.isShow(false)
        style2BtnBG.isShow(false)
        style3BtnBG.isShow(false)
        style4BtnBG.isShow(false)

        when (widgetStyle) {

            WidgetStyle.LIGHT -> {
                style1BtnBG.isShow(true)
            }

            WidgetStyle.DARK -> {
                style2BtnBG.isShow(true)
            }

            WidgetStyle.BLACK -> {
                style3BtnBG.isShow(true)
            }

            WidgetStyle.WHITE -> {
                style4BtnBG.isShow(true)
            }

        }

        callback.onStyleInfoChange(widgetStyle)
    }

    interface Callback {

        fun onNameInfoChange(name: CharSequence)

        fun onSignInfoChange(sign: CharSequence)

        fun onTimeTypeChange(noTime: Boolean)

        fun onTimeInfoChange(time: Long)

        fun onStyleInfoChange(style: WidgetStyle)

        fun onTimingTypeChange(noCountdown: Boolean)

        fun onRepeatTypeChange(repeatType: RepeatType)

    }

}
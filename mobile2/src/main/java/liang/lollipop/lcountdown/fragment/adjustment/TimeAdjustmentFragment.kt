package liang.lollipop.lcountdown.fragment.adjustment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_adjustment_time.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.WidgetPart
import liang.lollipop.lcountdown.provider.TimeInfoProvider
import java.util.*

/**
 * @author lollipop
 * @date 2020/6/14 22:09
 * 时间调整的碎片
 */
class TimeAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_time

    override val icon = R.drawable.ic_baseline_access_time_24

    override val adjustmentPart = WidgetPart.Time

    override val title = R.string.title_time

    override val colorId = R.color.focusTimeAdjust

    private val infoProvider = TimeInfoProviderWrapper()

    private var listenerLock = false

    private val calendar: Calendar by lazy {
        Calendar.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateBtn.setOnClickListener {
            val yearIn = calendar.get(Calendar.YEAR)
            val monthIn = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(it.context, { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onTimeChange()
            }, yearIn, monthIn, day).show()
        }

        timeBtn.setOnClickListener {
            val hourIn = calendar.get(Calendar.HOUR_OF_DAY)
            val minuteIn = calendar.get(Calendar.MINUTE)
            TimePickerDialog(it.context, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeChange()
            }, hourIn, minuteIn, false).show()
        }
        timingMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            if (!listenerLock) {
                infoProvider.isCountdown = (checkedId == R.id.countdownMethodBtn)
                callInfoChange()
            }
        }
        cycleModeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (!listenerLock) {
                infoProvider.cycleType = when(checkedId) {
                    R.id.dayCycleBtn -> {
                        TimeInfoProvider.CycleType.Day
                    }
                    R.id.monthCycleBtn -> {
                        TimeInfoProvider.CycleType.Month
                    }
                    R.id.yearCycleBtn -> {
                        TimeInfoProvider.CycleType.Year
                    }
                    else -> {
                        TimeInfoProvider.CycleType.No
                    }
                }
                callInfoChange()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        parse()
    }

    override fun onWidgetInfoChange() {
        super.onWidgetInfoChange()
        parse()
    }

    private fun parse() {
        listenerLock = true
        calendar.timeInMillis = infoProvider.targetTime
        onTimeChange(false)
        if (infoProvider.isCountdown) {
            countdownMethodBtn.isChecked = true
        } else {
            timingMethodBtn.isChecked = true
        }
        when(infoProvider.cycleType) {
            TimeInfoProvider.CycleType.Day -> {
                dayCycleBtn
            }
            TimeInfoProvider.CycleType.Month -> {
                monthCycleBtn
            }
            TimeInfoProvider.CycleType.Year -> {
                yearCycleBtn
            }
            else -> {
                noCycleBtn
            }
        }.isChecked = true
        listenerLock = false
    }

    @SuppressLint("SetTextI18n")
    private fun onTimeChange(fromUser: Boolean = true) {
        val hourIn = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteIn = calendar.get(Calendar.MINUTE)
        timeBtn.text = "${hourIn.formatNumber()}:${minuteIn.formatNumber()}"

        val yearIn = calendar.get(Calendar.YEAR)
        val monthIn = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        dateBtn.text = "${yearIn.formatNumber()}-${monthIn.formatNumber()}-${day.formatNumber()}"

        if (fromUser) {
            callInfoChange()
        }
    }

    private fun callInfoChange() {
        callChangeWidgetInfo()
    }

    private fun Int.formatNumber(): String {
        if (this < 10) {
            return "0$this"
        }
        return this.toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachCallback<Callback>(context) {
            infoProvider.provider = it.getTimeInfoProvider()
        }
    }

    override fun onDetach() {
        super.onDetach()
        infoProvider.provider = null
    }

    interface Callback {
        fun getTimeInfoProvider(): TimeInfoProvider
    }

    private class TimeInfoProviderWrapper(var provider: TimeInfoProvider? = null): TimeInfoProvider {
        override var targetTime: Long
            get() = provider?.targetTime?:0
            set(value) {
                provider?.targetTime = value
            }
        override var limitTime: Long
            get() = provider?.limitTime?:0
            set(value) {
                provider?.limitTime = value
            }
        override var isCountdown: Boolean
            get() = provider?.isCountdown?:true
            set(value) {
                provider?.isCountdown = value
            }
        override var cycleType: TimeInfoProvider.CycleType
            get() = provider?.cycleType?: TimeInfoProvider.CycleType.No
            set(value) {
                provider?.cycleType = value
            }
    }

}
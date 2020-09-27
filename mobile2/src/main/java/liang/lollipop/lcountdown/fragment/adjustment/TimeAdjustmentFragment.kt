package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_adjustment_time.*
import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 2020/6/14 22:09
 * 时间调整的碎片
 */
class TimeAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_time

    override val icon = R.drawable.ic_baseline_access_time_24

    override val title = R.string.title_time

    override val colorId = R.color.focusTimeAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeBtn.setOnClickListener {
//            val yearIn = calendar.get(Calendar.YEAR)
//            val monthIn = calendar.get(Calendar.MONTH)
//            val day = calendar.get(Calendar.DAY_OF_MONTH)
//            DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
//                calendar.set(Calendar.YEAR, year)
//                calendar.set(Calendar.MONTH, month)
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                onEndTimeChange()
//            }, yearIn, monthIn, day).show()
        }

//        timeSelectView -> {
//            val hourIn = calendar.get(Calendar.HOUR_OF_DAY)
//            val minuteIn = calendar.get(Calendar.MINUTE)
//            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
//                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                calendar.set(Calendar.MINUTE, minute)
//                onEndTimeChange()
//            }, hourIn, minuteIn, false).show()
//        }
    }



}
package liang.lollipop.lcountdown.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_quick_timing.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseActivity
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.utils.TimingUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * 快速计时Activity
 * @author Lollipop
 */
class QuickTimingActivity : BaseActivity() {

    private var srcStartTime = System.currentTimeMillis()
    private var startTime = srcStartTime

    companion object {

        const val RESULT_TIMING_ID = "RESULT_TIMING_ID"

        const val QUIET_BTN_TRANSITION = "QUIET_BTN"

        private const val SECOND = 1000L
        private const val MINUTE = SECOND * 60
        private const val HOUR = MINUTE * 60
        private const val DAY = HOUR * 24

    }

    private val calendar = Calendar.getInstance()

    private val autoChangeInfoList = ArrayList<AutoChangeInfo>()

    private val autoChangeAdapter = AutoChangeAdapter(autoChangeInfoList, this::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_timing)

        init()
    }

    private fun init() {
        onTimeChange()

        bindClick(rootGroup, timingBtn, countdownBtn, infoBody, autoChangeGroup)

        autoChangeList.adapter = autoChangeAdapter
        autoChangeList.layoutManager = FlexboxLayoutManager(this,
                FlexDirection.ROW, FlexWrap.WRAP).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        initListData()
        autoChangeAdapter.notifyDataSetChanged()
    }

    private fun initListData() {
        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.now), srcStartTime))

        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.minutes_later_10),
                srcStartTime + (MINUTE * 10)))

        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.minutes_later_15),
                srcStartTime + (MINUTE * 15)))

        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.after_half_an_hour),
                srcStartTime + (MINUTE * 30)))

        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.after_one_hour),
                srcStartTime + HOUR))

        calendar.timeInMillis = srcStartTime
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val endDay = calendar.timeInMillis
        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.end_today),
                endDay))

        autoChangeInfoList.add(AutoChangeInfo(
                getString(R.string.tomorrow_at_this_time),
                srcStartTime + DAY))

        calendar.timeInMillis = srcStartTime
        val thisHour = calendar.get(Calendar.HOUR_OF_DAY)
        for (i in 1 .. 23) {
            val nextHour = ((thisHour + i) % 24).format()
            val nextHourInMillis = (srcStartTime + (HOUR * i)) / HOUR * HOUR
            autoChangeInfoList.add(AutoChangeInfo("${nextHour}:00", nextHourInMillis))
        }
    }

    private fun onTimeChange() {
        calendar.timeInMillis = startTime

        yearInputView.setText(calendar.get(Calendar.YEAR).format(4))
        monthInputView.setText((calendar.get(Calendar.MONTH) + 1).format())
        dayInputView.setText(calendar.get(Calendar.DAY_OF_MONTH).format())
        hourInputView.setText(calendar.get(Calendar.HOUR_OF_DAY).format())
        minuteInputView.setText(calendar.get(Calendar.MINUTE).format())
        secondsInputView.setText(calendar.get(Calendar.SECOND).format())
        millisecondInputView.setText(calendar.get(Calendar.MILLISECOND).format(3))
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
        val month = monthInputView.getInt().let {
            if (it > 0) {
                it - 1
            } else {
                0
            }
        }
        calendar.set(Calendar.MONTH, month)
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

    private fun onItemClick(holder: AutoChangeHolder) {
        val info = autoChangeInfoList[holder.adapterPosition]
        startTime = info.time
        onTimeChange()
        timingNameEdit.setText(info.name)
    }

    private data class AutoChangeInfo(val name: String, val time: Long)

    private class AutoChangeHolder(view: View,
                                   private val onClick: (AutoChangeHolder) -> Unit)
        : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(group: ViewGroup, onClick: (AutoChangeHolder) -> Unit): AutoChangeHolder {
                return AutoChangeHolder(LayoutInflater.from(group.context)
                        .inflate(R.layout.item_auto_timer, group, false), onClick)
            }
        }

        private val nameView: TextView = itemView.findViewById(R.id.nameView)

        init {
            nameView.setOnClickListener {
                onClick(this)
            }
        }

        fun bind(info: AutoChangeInfo) {
            nameView.text = info.name
        }

    }

    private class AutoChangeAdapter(private val data: ArrayList<AutoChangeInfo>,
                                    private val onItemClick: (AutoChangeHolder) -> Unit)
        : RecyclerView.Adapter<AutoChangeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoChangeHolder {
            return AutoChangeHolder.create(parent, onItemClick)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: AutoChangeHolder, position: Int) {
            holder.bind(data[position])
        }

    }

}

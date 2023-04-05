package liang.lollipop.lcountdown.holder

import android.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseHolder
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.databinding.ItemTimingBinding
import liang.lollipop.lcountdown.drawable.LinearGradientDrawable
import liang.lollipop.lcountdown.utils.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 计时的显示Holder
 * @author Lollipop
 */
class TimingHolder(private val binding: ItemTimingBinding) : BaseHolder<TimingBean>(binding.root) {


    // 开启悬浮窗的按钮
    val floatingBtn: ImageView
        get() {
            return binding.floatingBtn
        }

    val stopBtn: Button
        get() {
            return binding.stopBtn
        }


    //头部的颜色
    private val headColorDrawable = LinearGradientDrawable()

    private var lastBean: TimingBean? = null

    //计时显示的Handler
    private val timerTask = task {
        if (itemView.isAttachedToWindow) {
            onTimeChange()
        }
    }

    //时间格式化
    private val simpleDateFormat =
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }

    private val countdownBean = CountdownBean()

    init {
        //头部的颜色
        binding.headColorView.setImageDrawable(headColorDrawable)

        itemView.setOnClickListener(this)
        binding.stopBtn.setOnClickListener(this)
        floatingBtn.setOnClickListener(this)

        canSwipe = true

        itemView.setOnLongClickListener { view ->
            lastBean?.let { bean ->
                ClipboardHelper.put(view.context, ClipboardHelper.encodeTimestamp(bean.startTime))
                AlertDialog.Builder(view.context).setTitle(R.string.title_copy_times)
                    .setMessage(R.string.msg_copy_times)
                    .setPositiveButton(R.string.understood) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            true
        }
    }

    companion object {

        fun new(parent: ViewGroup): TimingHolder {
            return TimingHolder(parent.bind(false))
        }

    }

    override fun onBind(bean: TimingBean) {

        lastBean = bean

        headColorDrawable.onColorChange(bean.color, 0x00FFFFFF)
        headColorDrawable.alpha = 128
        headColorDrawable.callUpdate()

        binding.titleIconView.text = bean.name.let {
            if (TextUtils.isEmpty(it)) {
                ""
            } else {
                it.substring(0, 1).uppercase(Locale.getDefault())
            }
        }

        binding.timingTitleView.text = bean.name
        binding.timingTitleView.visibility = if (TextUtils.isEmpty(bean.name)) {
            View.GONE
        } else {
            View.VISIBLE
        }

        binding.startTimeView.text = simpleDateFormat.format(Date(bean.startTime))

        binding.endTimeView.text = bean.endTime.let {
            if (it == 0L) {
                context.getString(R.string.now)
            } else {
                simpleDateFormat.format(Date(bean.endTime))
            }
        }

        onTimeChange()

        val btnVisibility = if (bean.endTime == 0L) {
            View.VISIBLE
        } else {
            View.GONE
        }

        stopBtn.visibility = btnVisibility
        floatingBtn.isEnabled = btnVisibility == View.VISIBLE

        floatingBtn.rotation = if (bean.isCountdown) {
            0F
        } else {
            -90F
        }

        FileUtil.loadTimerImage(binding.backImageView, bean.id)
    }

    private fun onTimeChange() {

        val bean = lastBean ?: return

        var startTime = bean.startTime
        var endTime = bean.endTime
        if (endTime == 0L) {
            endTime = System.currentTimeMillis()
        }

        if (bean.isCountdown) {
            val temp = startTime
            startTime = endTime
            endTime = temp
        }

        binding.timeLengthView.text = CountdownUtil.timer(
            countdownBean,
            startTime,
            endTime
        ).getTimerValue()

        timerTask.cancel()
        if (lastBean?.endTime == 0L) {
            timerTask.delay(1000)
        }

    }

    fun stopTiming() {
        timerTask.cancel()
    }

}
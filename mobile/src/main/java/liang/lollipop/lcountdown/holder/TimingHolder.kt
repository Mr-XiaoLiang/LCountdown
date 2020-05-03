package liang.lollipop.lcountdown.holder

import android.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseHolder
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.drawable.LinearGradientDrawable
import liang.lollipop.lcountdown.utils.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 计时的显示Holder
 * @author Lollipop
 */
class TimingHolder(itemView: View) : BaseHolder<TimingBean>(itemView) {

    //标题文字
    private val titleIconView: TextView = find(R.id.titleIconView)

    //标题View
    private val timingTitleView: TextView = find(R.id.timingTitleView)

    //开始时间
    private val startTimeView: TextView = find(R.id.startTimeView)

    //时长
    private val timeLengthView: TextView = find(R.id.timeLengthView)

    //结束时间
    private val endTimeView: TextView = find(R.id.endTimeView)

    // 开启悬浮窗的按钮
    val floatingBtn: ImageView = find(R.id.floatingBtn)

    // 背景图
    private val backImageView: ImageView = find(R.id.backImageView)

    //头部的颜色
    private val headColorDrawable = LinearGradientDrawable()

    //停止按钮
    val stopBtn: Button = find(R.id.stopBtn)

    private var lastBean: TimingBean? = null

    //计时显示的Handler
    private val timerTask = createTask {
        if (itemView.isAttachedToWindow) {
            onTimeChange()
        }
    }

    //时间格式化
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.ENGLISH).apply {
        timeZone = TimeZone.getDefault()
    }

    private val countdownBean = CountdownBean()

    init {
        //头部的颜色
        val headColorView: ImageView = find(R.id.headColorView)
        headColorView.setImageDrawable(headColorDrawable)

        itemView.setOnClickListener(this)
        stopBtn.setOnClickListener(this)
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

        fun new(layoutInflater: LayoutInflater, parent: ViewGroup): TimingHolder {
            return TimingHolder(layoutInflater.inflate(R.layout.item_timing, parent, false))
        }

    }

    override fun onBind(bean: TimingBean) {

        lastBean = bean

        headColorDrawable.onColorChange(bean.color, 0x00FFFFFF)
        headColorDrawable.alpha = 128
        headColorDrawable.callUpdate()

        titleIconView.text = bean.name.let {
            if (TextUtils.isEmpty(it)) {
                ""
            } else {
                it.substring(0, 1).toUpperCase(Locale.getDefault())
            }
        }

        timingTitleView.text = bean.name
        timingTitleView.visibility = if (TextUtils.isEmpty(bean.name)) {
            View.GONE
        } else {
            View.VISIBLE
        }

        startTimeView.text = simpleDateFormat.format(Date(bean.startTime))

        endTimeView.text = bean.endTime.let {
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

        FileUtil.loadTimerImage(backImageView, bean.id)
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

        timeLengthView.text = CountdownUtil.timer(
                countdownBean,
                startTime,
                endTime
        ).getTimerValue()

        removeTask(timerTask)
        if (lastBean?.endTime == 0L) {
            onUIDelay(1000, timerTask)
        }

    }

    fun stopTiming() {
        removeTask(timerTask)
    }

}
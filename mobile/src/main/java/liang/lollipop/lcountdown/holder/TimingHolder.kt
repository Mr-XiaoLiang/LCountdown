package liang.lollipop.lcountdown.holder

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import liang.lollipop.lbaselib.base.BaseHolder
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.drawable.LinearGradientDrawable
import liang.lollipop.lcountdown.utils.CountdownUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * 计时的显示Holder
 * @author Lollipop
 */
class TimingHolder(itemView: View): BaseHolder<TimingBean>(itemView) {

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
    //头部的颜色
    private val headColorDrawable = LinearGradientDrawable()

    //停止按钮
    private val stopBtn: Button = find(R.id.stopBtn)

    private var lastBean: TimingBean? = null

    //计时显示的Handler
    private val timerHandler = Handler(Looper.getMainLooper(), Handler.Callback { msg ->
        when(msg?.what){

            WHAT_TIMER_NEXT -> {

                onTimeChange()

                true
            }

            else -> false

        }
    })

    //时间格式化
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.ENGLISH).apply {
        timeZone = TimeZone.getDefault()
    }

    private val countdownBean = CountdownBean()

    init {
        //头部的颜色
        val headColorView: ImageView = find(R.id.headColorView)
        headColorView.setImageDrawable(headColorDrawable)

        stopBtn.setOnClickListener(this)

        canSwipe = true
    }

    companion object {

        fun new(layoutInflater: LayoutInflater,parent: ViewGroup): TimingHolder{
            return TimingHolder(layoutInflater.inflate(R.layout.item_timing,parent,false))
        }

        private const val WHAT_TIMER_NEXT = 345

    }

    override fun onBind(bean: TimingBean) {

        lastBean = bean

        headColorDrawable.onColorChange(bean.color,0x00FFFFFF)
        headColorDrawable.callUpdate()

        titleIconView.text = bean.name.let {
            if(TextUtils.isEmpty(it)){
                ""
            }else{
                it.substring(0,1)
            }
        }
        timingTitleView.text = bean.name
        timingTitleView.visibility = if(TextUtils.isEmpty(bean.name)){View.GONE}else{View.VISIBLE}

        startTimeView.text = simpleDateFormat.format(Date(bean.startTime))

        endTimeView.text = bean.endTime.let { if(it == 0L){
            context.getString(R.string.now)
        }else{
            simpleDateFormat.format(Date(bean.endTime))
        } }

        onTimeChange()

        stopBtn.visibility = if(bean.endTime == 0L){ View.VISIBLE }else{ View.GONE }

    }

    private fun onTimeChange(){

        if(lastBean != null){

            val startTime = lastBean?.startTime?:0L
            val endTime = lastBean?.endTime?:0L

            timeLengthView.text = CountdownUtil.timer(
                    countdownBean,
                    startTime,
                    if(endTime == 0L){
                        System.currentTimeMillis()
                    }else{
                        endTime
                    }
            ).getTimerValue()

            timerHandler.removeMessages(WHAT_TIMER_NEXT)
            if(lastBean?.endTime == 0L){
                timerHandler.sendEmptyMessageDelayed(WHAT_TIMER_NEXT,1000)
            }

        }

    }

    fun stopTiming(){
        timerHandler.removeMessages(WHAT_TIMER_NEXT)
    }

}
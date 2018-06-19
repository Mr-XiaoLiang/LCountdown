package liang.lollipop.lcountdown.service

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.os.Message
import android.service.dreams.DreamService
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import liang.lollipop.lbaselib.base.SimpleHandler
import liang.lollipop.lbaselib.util.TaskUtils
import liang.lollipop.lbaselib.util.TintUtil
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.IconBean
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.holder.IconHolder
import liang.lollipop.lcountdown.utils.CountdownUtil
import liang.lollipop.lcountdown.utils.WidgetDBUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * 倒计时的睡眠服务
 * @author Lollipop
 */
class CountdownDreamService: DreamService(),ValueAnimator.AnimatorUpdateListener,Animator.AnimatorListener,SimpleHandler.HandlerCallback {

    private val calendar = Calendar.getInstance()

    private lateinit var dreamBody: View

    private lateinit var dreamRoot: View

    private lateinit var timeView: TextView

    private lateinit var countdownView: TextView

    private lateinit var batteryView: TextView

    private lateinit var nameView: TextView

    private lateinit var iconGroup: FlexboxLayout

    private lateinit var inflater: LayoutInflater

    private val random = Random()

    private val hiddenAnimator = ValueAnimator()
    private val showAnimator = ValueAnimator()

    private val widgetBean = WidgetBean()

    private val iconBeanList = java.util.ArrayList<IconBean>()
    private val shownHolders = java.util.ArrayList<IconHolder>()
    private val waitHolders = java.util.ArrayList<IconHolder>()

    private val dreamBroadcastReceiver = DreamBroadcastReceiver()

    private val handler = SimpleHandler(this)

    private var isTimer = false

    companion object {

        private const val LOCATION_UPDATE_INTERVAL = 5

        private const val HIDDEN_DURATION = 1500L
        private const val SHOW_DURATION = 1500L

        private const val COUNTDOWN_DURATION = 1000L
        private const val WHAT_COUNTDOWN = 0xC0D0

        private const val ICON_COLOR = Color.WHITE

    }

    override fun onHandler(message: Message) {

        when(message.what){

            WHAT_COUNTDOWN -> {
                updateCountdown()
                handler.sendEmptyMessageDelayed(WHAT_COUNTDOWN, COUNTDOWN_DURATION)
            }

        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Exit dream upon user touch
        isInteractive = false
        // Hide system UI
        isFullscreen = true
        // Set the dream layout
        setContentView(R.layout.dream_root)
        initView()
        initAnimator()
        initData()
    }

    private fun initView(){

        inflater = LayoutInflater.from(this)

        timeView = findViewById(R.id.timeView)
        dreamBody = findViewById(R.id.dreamBody)
        dreamRoot = findViewById(R.id.dreamRoot)
        batteryView = findViewById(R.id.powerView)
        countdownView = findViewById(R.id.countdownView)
        nameView = findViewById(R.id.nameView)
        iconGroup = findViewById(R.id.iconsGroup)

    }

    private fun initAnimator(){
        hiddenAnimator.interpolator = DecelerateInterpolator()
        hiddenAnimator.duration = HIDDEN_DURATION
        hiddenAnimator.addUpdateListener(this)
        hiddenAnimator.addListener(this)
        hiddenAnimator.setFloatValues(1F, 0F)

        showAnimator.interpolator = DecelerateInterpolator()
        showAnimator.duration = SHOW_DURATION
        showAnimator.addUpdateListener(this)
        showAnimator.addListener(this)
        showAnimator.setFloatValues(0F, 1F)
    }

    private fun initData(){
        val widgetList = ArrayList<WidgetBean>()
        WidgetDBUtil.read(this).getAll(widgetList).close()
        if(widgetList.isNotEmpty()){
            isTimer = false
            widgetBean.copy(widgetList[0])
        }else{
            isTimer = true
            widgetBean.endTime = System.currentTimeMillis()
            widgetBean.countdownName = ""
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation === hiddenAnimator || animation === showAnimator) {
            val value = animation.animatedValue as Float
            dreamBody.alpha = value
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        if (animation == hiddenAnimator) {//如果是隐藏的动画
            showAnimator.start()//启动显示的动画
        }
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
        if (animation == showAnimator) {
            layoutAgain()
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        initData()
        registerReceiver()
        updateTime()
        updateCountdown()
        showAnimator.start()
        handler.sendEmptyMessage(WHAT_COUNTDOWN)
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        unregisterReceiver()
        cancelAnimator()
        handler.removeMessages(WHAT_COUNTDOWN)
    }

    private fun cancelAnimator() {
        hiddenAnimator.cancel()
        showAnimator.cancel()
    }

    //重新调整位置
    private fun layoutAgain() {
        val x = Math.abs(random.nextInt(dreamRoot.width - dreamBody.width))
        val y = Math.abs(random.nextInt(dreamRoot.height - dreamBody.height))
        dreamBody.translationX = x.toFloat()
        dreamBody.translationY = y.toFloat()
    }

    private inner class DreamBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                //时间变化
                Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIME_TICK -> {
                    updateTime()
                    updateCountdown()
                }
                //电量变化
                Intent.ACTION_BATTERY_CHANGED -> updateBattery(intent)
                 //充电完毕
                Intent.ACTION_BATTERY_OKAY -> {
                    batteryView.text = "100%"
                }
                //添加通知
                NotificationService.ACTION_LOLLIPOP_NOTIFICATION_POSTED -> addIcon(intent.extras)
            //删除通知
                NotificationService.ACTION_LOLLIPOP_NOTIFICATION_REMOVED -> removeIcon(intent.extras)
            }
        }
    }

    private fun addIcon(bundle: Bundle){

        TaskUtils.addUITask(object :TaskUtils.UICallback<IconBean,Bundle>{
            override fun onSuccess(result: IconBean) {

                iconBeanList.add(result)
                val holder: IconHolder = if (waitHolders.size > 0) {
                    waitHolders.removeAt(0)
                } else {
                    IconHolder.getInstance(inflater, iconGroup)
                }
                holder.addTo(iconGroup)
                holder.onBind(result)
                shownHolders.add(holder)

            }

            override fun onError(e: Exception, code: Int, msg: String) {
                e.printStackTrace()
            }

            override fun onBackground(args: Bundle?): IconBean {

                var isShown = false
                val pkgName = args?.getString(NotificationService.ARG_PKG, "")?:""
                for (bean in iconBeanList) {
                    if (bean.pkgName == pkgName) {
                        isShown = true
                        break
                    }
                }

                if(!isShown){
                    val iconId = args?.getInt(NotificationService.ARG_ICON, 0)?:0
                    if(iconId == 0){
                        throw RuntimeException("iconId == 0")
                    }
                    val pkgContext = createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY)
                    val icon = TintUtil.tintDrawable(pkgContext,iconId).setColor(ICON_COLOR).mutate().tint()
                    return IconBean(pkgName,icon)
                }else{
                    throw RuntimeException("is Shown")
                }
            }

        },bundle)

    }

    private fun removeIcon(bundle: Bundle){

        TaskUtils.addUITask(object : TaskUtils.UICallback<ArrayList<IconHolder>,Bundle>{
            override fun onSuccess(result: ArrayList<IconHolder>) {

                for (holder in result) {
                    holder.remove()
                }

            }

            override fun onError(e: Exception, code: Int, msg: String) {
                e.printStackTrace()
            }

            override fun onBackground(args: Bundle?): ArrayList<IconHolder> {

                val pkgName = args?.getString(NotificationService.ARG_PKG, "")?:""
                val holderIterator = shownHolders.iterator()
                val holders = ArrayList<IconHolder>()
                while (holderIterator.hasNext()) {
                    val holder = holderIterator.next()
                    if (holder.bean != null && pkgName == holder.bean?.pkgName) {
                        waitHolders.add(holder)
                        holders.add(holder)
                        holderIterator.remove()
                    }
                }
                return holders
            }
        }, bundle)

    }

    private fun updateTime() {
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar.get(Calendar.HOUR_OF_DAY).formatNumber()
        val minutesInt = calendar.get(Calendar.MINUTE)
        val minutes = calendar.get(Calendar.MINUTE).formatNumber()

        timeView.text = "$hour:$minutes"

        if (minutesInt % LOCATION_UPDATE_INTERVAL == 0) {
            hiddenAnimator.start()
        }
    }

    private fun updateCountdown(){

        val countdownBean = if(isTimer){CountdownUtil.timer(widgetBean.endTime)}else{CountdownUtil.countdown(widgetBean.endTime)}

        countdownView.text = countdownBean.getTimerValue()
//        countdownView.text = "${countdownBean.days}.${countdownBean.hours}.${countdownBean.minutes}.${countdownBean.seconds}"
        nameView.text = widgetBean.countdownName

    }

    private fun updateBattery(intent: Intent){
        //当前剩余电量
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        //电量最大值
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1)
        //电量百分比
        val batteryPct = (level / scale.toFloat() * 100).toInt()
        batteryView.text = "$batteryPct%"
    }

    private fun Int.formatNumber(): String = if(this < 10){ "0"+this }else{ ""+this }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationService.ACTION_LOLLIPOP_NOTIFICATION_POSTED)
        intentFilter.addAction(NotificationService.ACTION_LOLLIPOP_NOTIFICATION_REMOVED)
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY)
        registerReceiver(dreamBroadcastReceiver, intentFilter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(dreamBroadcastReceiver)
    }

}
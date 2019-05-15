package liang.lollipop.lcountdown.service

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.BatteryManager
import android.os.Bundle
import android.os.Message
import android.service.dreams.DreamService
import android.util.Log
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

    private val hiddenAnimator = ValueAnimator().apply {
        interpolator = DecelerateInterpolator()
        duration = HIDDEN_DURATION
        addUpdateListener(this@CountdownDreamService)
        addListener(this@CountdownDreamService)
        setFloatValues(1F, 0F)
    }
    private val showAnimator = ValueAnimator().apply {
        interpolator = DecelerateInterpolator()
        duration = SHOW_DURATION
        addUpdateListener(this@CountdownDreamService)
        addListener(this@CountdownDreamService)
        setFloatValues(0F, 1F)
    }

    private val widgetBean = WidgetBean()

    private val iconBeanList = ArrayList<IconBean>()
    private val shownHolders = ArrayList<IconHolder>()
    private val waitHolders = ArrayList<IconHolder>()

    private val dreamBroadcastReceiver = DreamBroadcastReceiver()

    private val handler = SimpleHandler(this)

    private var isTimer = false

    private var batteryHelper: BatteryHelper? = null

    private var lastAnimationTime = -1

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
                updateInfo()
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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // Set the dream layout
        setContentView(R.layout.dream_root)
        initView()
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

        countdownView.typeface = Typeface.createFromAsset(assets, "fonts/time_font.otf")
        timeView.typeface = Typeface.createFromAsset(assets, "fonts/ttf_liquid_crystal.ttf")
        batteryView.typeface = Typeface.createFromAsset(assets, "fonts/ttf_liquid_crystal.ttf")

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
        for (holder in shownHolders) {
            holder.remove()
        }
        waitHolders.addAll(shownHolders)
        shownHolders.clear()
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
        updateInfo()
        showAnimator.start()
        handler.sendEmptyMessage(WHAT_COUNTDOWN)
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        unregisterReceiver()
        cancelAnimator()
        handler.removeMessages(WHAT_COUNTDOWN)
    }

    private fun updateInfo() {
        updateTime()
        updateCountdown()
        updateBattery()
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

    private fun getBattery(): Int {
        if (batteryHelper == null) {
            batteryHelper = BatteryHelper(this)
        }
        return batteryHelper?.capacity?:-1
    }

    private class BatteryHelper(context: Context) {
        private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        /**
         * 电量的百分比
         * 0~100
         */
        val capacity: Int
            get() {
                // 当前电量百分比
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            }

    }

    private inner class DreamBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // 添加通知
                NotificationService.ACTION_LOLLIPOP_NOTIFICATION_POSTED -> addIcon(intent.extras)
                // 删除通知
                NotificationService.ACTION_LOLLIPOP_NOTIFICATION_REMOVED -> removeIcon(intent.extras)
            }
        }
    }

    private fun addIcon(bundle: Bundle?){
        if (bundle == null) {
            return
        }
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

    private fun removeIcon(bundle: Bundle?){
        if (bundle == null) {
            return
        }
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

    @SuppressLint("SetTextI18n")
    private fun updateTime() {
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar.get(Calendar.HOUR_OF_DAY).formatNumber()
        val minutesInt = calendar.get(Calendar.MINUTE)
        val minutes = calendar.get(Calendar.MINUTE).formatNumber()

        timeView.text = "$hour:$minutes"

        if (minutesInt % LOCATION_UPDATE_INTERVAL == 0 && minutesInt != lastAnimationTime) {
            lastAnimationTime = minutesInt
            hiddenAnimator.start()
        }
    }

    private fun updateCountdown(){

        val countdownBean = if(isTimer){
            CountdownUtil.timer(widgetBean.endTime)
        }else{
            widgetBean.getTimerInfo()
        }

        countdownView.text = countdownBean.getTimerValue()
        nameView.text = widgetBean.countdownName

    }

    private fun updateBattery(){
        val value = getBattery()
        batteryView.text = if (value < 0) {
            ""
        } else {
            "$value%"
        }
    }

    private fun Int.formatNumber(): String = if(this < 10){ "0"+this }else{ ""+this }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationService.ACTION_LOLLIPOP_NOTIFICATION_POSTED)
        intentFilter.addAction(NotificationService.ACTION_LOLLIPOP_NOTIFICATION_REMOVED)
        registerReceiver(dreamBroadcastReceiver, intentFilter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(dreamBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryHelper = null
    }

}
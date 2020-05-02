package liang.lollipop.lcountdown.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.app.NotificationCompat
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.activity.TimingListActivity
import liang.lollipop.lcountdown.bean.CountdownBean
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.utils.*
import org.jetbrains.anko.windowManager

/**
 * @date: 2018/6/25 10:30
 * @author: lollipop
 *
 * 悬浮窗口的服务
 */
@SuppressLint("Registered")
class FloatingService : Service() {

    private var isReady = false

    private lateinit var notificationManager: NotificationManager

    private val floatingHolderList = ArrayList<ViewHolder>()
    private val floatingViewHelper: FloatingViewHelper by lazy {
        FloatingViewHelper.create(windowManager)
    }

    companion object {

        private const val FOLLOWERS_CHANNEL_ID = "liang.lollipop.lcountdown.Floating"

        //消息通知的ID
        private const val NOTIFICATION_ID = 548

        // 请求权限的pendingIntent
        private const val PENDING_REQUEST_PERMISSION = 23333

        // 关闭悬浮窗
        private const val ACTION_CLOSE_FLOATING = "lcountdown.CLOSE_FLOATING"

        private const val ARG_STOP = "ARG_CLOSE_FLOATING"

        fun start(context: Context, info: TimingBean, isStop: Boolean) {
            val intent = Intent(context, FloatingService::class.java)
            info.bindToIntent(intent)
            intent.putExtra(ARG_STOP, isStop)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

    }

    private val closeBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CLOSE_FLOATING) {
                closeAllFloating()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createChannels()

        registerReceiver(closeBroadcast, IntentFilter(ACTION_CLOSE_FLOATING))
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val androidChannel = NotificationChannel(FOLLOWERS_CHANNEL_ID,
                    getString(R.string.followers_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)
            androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            androidChannel.importance = NotificationManager.IMPORTANCE_NONE
            notificationManager.createNotificationChannel(androidChannel)
        }
    }

    private fun createNotification(): Notification {
        val closeIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                Intent(ACTION_CLOSE_FLOATING), PendingIntent.FLAG_UPDATE_CURRENT)
        val openIntent = PendingIntent.getActivity(this, 233,
                Intent(this, TimingListActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, FOLLOWERS_CHANNEL_ID)
        builder.setAutoCancel(false)//可以点击通知栏的删除按钮删除
                .setPriority(NotificationCompat.PRIORITY_MIN)//最高等级的通知优先级
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)//设置锁屏的显示模式，此处为保护模式
                .setWhen(System.currentTimeMillis())//设置消息时间
                .setSmallIcon(R.drawable.ic_small_logo)//设置小图标
                .setOngoing(true)
                .setShowWhen(false)
                .setContentTitle(getString(R.string.floating_notif_title))//设置标题
                .setContentText(getString(R.string.floating_notif_msg))//设置内容
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .addAction(0, getString(R.string.close_floating), closeIntent)
                .setContentIntent(openIntent)

        return builder.build()
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !android.provider.Settings.canDrawOverlays(this)) {
            notificationToOpenPermission()
            stopSelf()
            return false
        }
        return true
    }

    private fun findHolderById(id: Int): ViewHolder? {
        for (holder in floatingHolderList) {
            if (holder.timingInfo?.id == id) {
                return holder
            }
        }
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!checkPermission() || intent == null) {
            return START_REDELIVER_INTENT
        }
        isReady = true

        val info = TimingBean()
        info.getFromIntent(intent)

        val isStop = intent.getBooleanExtra(ARG_STOP, false)

        val holder = findHolderById(info.id)
        if (holder != null) {
            closeFloating(holder)
            return START_REDELIVER_INTENT
        }

        if (isStop) {
            return START_REDELIVER_INTENT
        }

        createHolder(info)
        startForeground(NOTIFICATION_ID, createNotification())

        return START_REDELIVER_INTENT
    }

    private fun closeFloating(holder: ViewHolder) {
        holder.onStop()
        removeView(holder.view)
        floatingHolderList.remove(holder)
        if (floatingHolderList.isEmpty()) {
            notificationManager.cancel(NOTIFICATION_ID)
            stopSelf()
        }
        System.gc()
    }

    private fun closeAllFloating() {
        for (holder in floatingHolderList) {
            holder.onStop()
            removeView(holder.view)
        }
        floatingHolderList.clear()
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
        System.gc()
    }

    private fun createHolder(info: TimingBean): ViewHolder {
        for (holder in floatingHolderList) {
            if (holder.timingInfo?.id == info.id) {
                return holder
            }
        }
        val holder = ViewHolder(this)
        floatingHolderList.add(holder)
        addView(holder.view)
        holder.onStart(info)
        return holder
    }

    override fun onDestroy() {
        super.onDestroy()
        isReady = false
        unregisterReceiver(closeBroadcast)
    }

    private fun notificationToOpenPermission() {
        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        } else {
            android.provider.Settings.ACTION_APPLICATION_SETTINGS
        }
        val notification = NotificationCompat.Builder(this, FOLLOWERS_CHANNEL_ID)
                .setContentTitle(getString(R.string.notifi_title_no_alert))
                .setContentText(getString(R.string.notifi_msg_no_alert))
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_small_logo)
                .setFullScreenIntent(PendingIntent.getActivity(this,
                        PENDING_REQUEST_PERMISSION,
                        Intent(action, Uri.parse("package:$packageName")),
                        PendingIntent.FLAG_UPDATE_CURRENT), true)
                .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun addView(view: View) {
        if (!isReady) {
            return
        }
        try {
            floatingViewHelper.addView(view, FloatingViewHelper.createParams())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun removeView(view: View) {
        try {
            floatingViewHelper.removeView(view)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private class ViewHolder(context: Context) {

        var timingInfo: TimingBean? = null
            private set

        private val countdownBean = CountdownBean()
        private val backgroundDrawable = CountdownFloatingBackground()

        @SuppressLint("InflateParams")
        val view: View = LayoutInflater.from(context).inflate(
                R.layout.item_floating, null)

        private val nameView: TextView = view.findViewById(R.id.countdownName)
        private val valueView: TextView = view.findViewById(R.id.countdownValue)

        private val updateTask = createTask {
            onTimeChange()
        }

        init {
            view.background = backgroundDrawable
            backgroundDrawable.corners = context.resources.getDimensionPixelSize(
                    R.dimen.floating_corners).toFloat()
        }

        fun onStart(info: TimingBean) {
            timingInfo = info
            backgroundDrawable.color = info.color
            nameView.text = info.name
            onTimeChange()
        }

        fun onStop() {
            timingInfo = null
            removeTask(updateTask)
        }

        private fun onTimeChange() {
            val bean = timingInfo ?: return
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
            valueView.text = CountdownUtil.timer(
                    countdownBean, startTime, endTime).getTimerValue()
            removeTask(updateTask)
            onUIDelay(300, updateTask)
        }

    }

    private class CountdownFloatingBackground : Drawable() {

        companion object {
            private const val ALPHA_MAX = 255
        }

        var color = Color.WHITE
        var corners = 0F
        private var rootAlpha = ALPHA_MAX

        private val boundsF = RectF()

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        override fun draw(canvas: Canvas) {
            paint.color = Color.BLACK.changeAlpha(rootAlpha, 120)
            canvas.drawRoundRect(boundsF, corners, corners, paint)
            paint.color = color.changeAlpha(rootAlpha, 60)
            canvas.drawRoundRect(boundsF, corners, corners, paint)
        }

        private fun Int.changeAlpha(root: Int, a: Int): Int {
            val alpha = (a * 1F / ALPHA_MAX * (root * 1F / ALPHA_MAX) * ALPHA_MAX).toInt().let {
                when {
                    it > ALPHA_MAX -> {
                        ALPHA_MAX
                    }
                    it < 0 -> {
                        0
                    }
                    else -> {
                        it
                    }
                }
            }

            return this and 0xFFFFFF or (alpha shl 24)
        }

        override fun onBoundsChange(b: Rect?) {
            super.onBoundsChange(b)
            boundsF.set(bounds)
            invalidateSelf()
        }

        override fun setAlpha(alpha: Int) {
            rootAlpha = alpha
            invalidateSelf()
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}

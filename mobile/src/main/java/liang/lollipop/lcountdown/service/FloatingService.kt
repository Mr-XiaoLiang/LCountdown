package liang.lollipop.lcountdown.service

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.utils.FloatingViewHelper
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.windowManager

/**
 * @date: 2018/6/25 10:30
 * @author: lollipop
 *
 * 悬浮窗口的服务
 */
@SuppressLint("Registered")
class FloatingService: Service(), ValueAnimator.AnimatorUpdateListener {

    private var isReady = false

    private lateinit var notificationManager: NotificationManager

    companion object {

        private const val FOLLOWERS_CHANNEL_ID = "liang.lollipop.lcountdown"
        const val FOLLOWERS_CHANNEL_NAME = "LCountdown"

        //消息通知的ID
        private const val NOTIFICATION_ID = 548542

        // 请求权限的pendingIntent
        private const val PENDING_REQUEST_PERMISSION = 23333

        fun start(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, FloatingService::class.java))
            } else {
                context.startService(Intent(context, FloatingService::class.java))
            }
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannels() {
        // create android channel
        val androidChannel = NotificationChannel(FOLLOWERS_CHANNEL_ID,
                FOLLOWERS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        // Sets whether notifications posted to this channel should display notification lights
        //        androidChannel.enableLights(false);
        // Sets whether notification posted to this channel should vibrate.
        //        androidChannel.enableVibration(false);
        // Sets the notification light color for notifications posted to this channel
        //        androidChannel.setLightColor(ContextCompat.getColor(this,R.color.colorAccent));
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        androidChannel.importance = NotificationManager.IMPORTANCE_NONE
        notificationManager.createNotificationChannel(androidChannel)
    }

    private fun createNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationHigh()
        } else {
            createNotificationLow()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationHigh(): Notification {
        val builder = Notification.Builder(this, FOLLOWERS_CHANNEL_ID)
        builder.setAutoCancel(false)//可以点击通知栏的删除按钮删除
                .setVisibility(Notification.VISIBILITY_PRIVATE)//设置锁屏的显示模式，此处为保护模式
                .setWhen(System.currentTimeMillis())//设置消息时间
                .setSmallIcon(R.drawable.ic_small_logo)//设置小图标
                .setColorized(true)
                .setOngoing(true)
                .setContentTitle(getString(R.string.floating_notif_title))//设置标题
                .setContentText(getString(R.string.floating_notif_msg))//设置内容
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
        return builder.build()
    }

    private fun createNotificationLow(): Notification {
        val builder = NotificationCompat.Builder(this, FOLLOWERS_CHANNEL_ID)
        builder.setAutoCancel(false)//可以点击通知栏的删除按钮删除
                .setPriority(NotificationCompat.PRIORITY_MAX)//最高等级的通知优先级
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)//设置锁屏的显示模式，此处为保护模式
                .setWhen(System.currentTimeMillis())//设置消息时间
                .setSmallIcon(R.drawable.ic_small_logo)//设置小图标
                .setOngoing(true)
                .setContentTitle(getString(R.string.floating_notif_title))//设置标题
                .setContentText(getString(R.string.floating_notif_msg))//设置内容
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(this)) {
            startActivity(Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            stopSelf()
            isReady = false
            return START_REDELIVER_INTENT
        }

        isReady = true

        startForeground(NOTIFICATION_ID,createNotification())

        val textView = TextView(this)
        textView.text = "HelloWorld"
        textView.backgroundColor = Color.RED
        addView(textView)

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        isReady = false
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

    private fun addView(view: View){
        if(!isReady){
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(this)) {
            notificationToOpenPermission()
            stopSelf()
            return
        }

        FloatingViewHelper.create(windowManager).addView(view, FloatingViewHelper.createParams())

    }



}

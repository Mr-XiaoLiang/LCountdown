package liang.lollipop.lcountdown.service

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * 消息服务
 * @author Lollipop
 */
class NotificationService: NotificationListenerService() {

    companion object {

        //广播，用于监听通知内容
        const val ACTION_LOLLIPOP_NOTIFICATION_POSTED = "ACTION_LOLLIPOP_NOTIFICATION_POSTED"
        const val ACTION_LOLLIPOP_NOTIFICATION_REMOVED = "ACTION_LOLLIPOP_NOTIFICATION_REMOVED"
        //应用图标
        const val ARG_ICON = Notification.EXTRA_SMALL_ICON
        //应用包名
        const val ARG_PKG = "ARG_PKG"

    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val notification = sbn.notification?:return
        if (notification.flags and Notification.FLAG_ONGOING_EVENT == Notification.FLAG_ONGOING_EVENT){
            return
        }
        val notifyInfo = notification.extras?:return
        val intent = Intent(ACTION_LOLLIPOP_NOTIFICATION_POSTED)
        intent.putExtra(ARG_ICON, notifyInfo.getInt(ARG_ICON))
        intent.putExtra(ARG_PKG, sbn.packageName)
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        val notification = sbn.notification
        val bundle = notification.extras
        bundle.putString(ARG_PKG, sbn.packageName)
        val intent = Intent(ACTION_LOLLIPOP_NOTIFICATION_REMOVED)
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

}
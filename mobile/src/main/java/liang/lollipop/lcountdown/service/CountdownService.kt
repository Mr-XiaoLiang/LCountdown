package liang.lollipop.lcountdown.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Message
import liang.lollipop.lbaselib.base.SimpleHandler
import liang.lollipop.lcountdown.utils.WidgetUtil

/**
 * 倒计时的服务
 * @author Lollipop
 */
class CountdownService: Service(), SimpleHandler.HandlerCallback {

    private val handler = SimpleHandler(this)

    companion object {

        private const val WHAT_UPDATE = 0x666

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        registerTimer()
        next()

        return START_STICKY_COMPATIBILITY
    }

    override fun onHandler(message: Message) {

        when(message.what){

            WHAT_UPDATE -> {

                WidgetUtil.callUpdate(this)

                next()

            }

        }

    }

    private fun next(){
        handler.sendEmptyMessageDelayed(WHAT_UPDATE,WidgetUtil.UPDATE_TIME)
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterTimer()
        handler.removeMessages(WHAT_UPDATE)
    }

}
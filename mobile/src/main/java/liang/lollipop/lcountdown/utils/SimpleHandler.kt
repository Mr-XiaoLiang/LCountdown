package liang.lollipop.lcountdown.utils

import android.os.Handler
import android.os.Message

/**
 * @author lollipop
 * @date 2020/5/3 12:44
 */
class SimpleHandler(private val callback: (Message) -> Unit) : Handler() {

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        callback.invoke(msg)
    }

}
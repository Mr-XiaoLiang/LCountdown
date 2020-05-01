package liang.lollipop.lcountdown.utils

import android.util.Log

/**
 * @author lollipop
 * @date 2020/5/1 21:46
 */
inline fun <reified T: Any> T.log(value: String) {
    Log.d("Lollipop", "${this.javaClass.simpleName} -> $value")
}
package liang.lollipop.lcountdown.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import java.lang.RuntimeException


/**
 * @author lollipop
 * @date 2020/4/27 22:39
 * 剪切板辅助类
 */
object ClipboardHelper {

    private const val LABEL = "LCountdown"
    private const val KEY = "##"

    fun put(context: Context, value: String): Boolean {
        return try {
            //获取剪贴板管理器：
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建普通字符型ClipData
            val clipData = ClipData.newPlainText(LABEL, value)
            // 将ClipData内容放到系统剪贴板里。
            clipboardManager.setPrimaryClip(clipData)
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    fun get(context: Context): String {
        try {
            //获取剪贴板管理器：
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (!clipboardManager.hasPrimaryClip()) {
                return ""
            }
            val primaryClip = clipboardManager.primaryClip?:return ""
            if (LABEL != primaryClip.description.label) {
                return ""
            }
            if (primaryClip.itemCount > 0) {
                return primaryClip.getItemAt(0)?.text?.toString()?:""
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return ""
    }

    fun clear(context: Context) {
        try {
            //获取剪贴板管理器：
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建普通字符型ClipData
            val clipData = ClipData.newPlainText("", "")
            // 将ClipData内容放到系统剪贴板里。
            clipboardManager.setPrimaryClip(clipData)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun encodeTimestamp(time: Long): String {
        return KEY + time.toString(Character.MAX_RADIX) + KEY
    }

    fun decodeTimestamp(value: String): Long {
        if (value.indexOf(KEY) != 0 || value.lastIndexOf(KEY) != (value.length - KEY.length)) {
            throw RuntimeException("value illegal")
        }
        return value.substring(KEY.length, value.length - KEY.length).toLong(Character.MAX_RADIX)
    }

}
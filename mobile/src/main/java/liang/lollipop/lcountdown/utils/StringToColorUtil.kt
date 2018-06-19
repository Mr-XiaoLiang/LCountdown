package liang.lollipop.lcountdown.utils

import android.graphics.Color
import android.util.Log
import java.security.MessageDigest

/**
 * 一个字符串转颜色的工具类
 * @author Lollipop
 */
object StringToColorUtil {

    fun format(text: String): Int{
        return format(text,true)
    }

    fun format(text: String,alphaLimit: Boolean): Int{

        return try {
            val md5 = md5(text)
            val red = (java.lang.Long.parseLong(md5.substring(0, 8), 16) % 256).toInt()
            val green = (java.lang.Long.parseLong(md5.substring(8, 16), 16) % 256).toInt()
            val blue = (java.lang.Long.parseLong(md5.substring(16, 24), 16) % 256).toInt()
            var alpha = (java.lang.Long.parseLong(md5.substring(24, 31), 16) % 256).toInt()
            if (alphaLimit) {
                alpha = alpha % 128 + 128
            }
            Color.argb(alpha, red, green, blue)
        } catch (e: Exception) {
            Color.WHITE
        }
    }

    private fun md5(text: String): String {
        return bytesToMD5(text.toByteArray())
    }

    //把字节数组转换成md5
    private fun bytesToMD5(input: ByteArray): String {
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            val md = MessageDigest.getInstance("MD5")
            //计算后获得字节数组
            val buff = md.digest(input)
            //把数组每一字节换成16进制连成md5字符串
            return bytesToHex(buff)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    //把字节数组转成16进位制数
    private fun bytesToHex(bytes: ByteArray): String {
        val md5str = StringBuffer()
        //把数组每一字节换成16进制连成md5字符串
        var digital: Int
        for (i in bytes.indices) {
            digital = bytes[i].toInt()
            if (digital < 0) {
                digital += 256
            }
            if (digital < 16) {
                md5str.append("0")
            }
            md5str.append(Integer.toHexString(digital))
        }
        return md5str.toString()
    }

}
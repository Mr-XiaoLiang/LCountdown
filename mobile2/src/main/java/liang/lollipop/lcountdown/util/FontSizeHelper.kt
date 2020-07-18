package liang.lollipop.lcountdown.util

import android.content.Context
import kotlin.math.min

/**
 * @author lollipop
 * @date 7/18/20 16:31
 * 字体大小辅助类
 */
object FontSizeHelper {

    fun maxFontSize(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return min(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    fun getFontSize(context: Context, weight: Float): Int {
        return getFontSize(maxFontSize(context), weight)
    }

    fun getFontSize(maxSize: Int, weight: Float): Int {
        return (maxSize * weight * 0.01F).toInt()
    }

}
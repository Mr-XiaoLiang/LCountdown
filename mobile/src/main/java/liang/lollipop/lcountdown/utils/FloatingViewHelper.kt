package liang.lollipop.lcountdown.utils

import android.graphics.PixelFormat
import android.os.Build
import android.view.*

/**
 * @author lollipop
 * @date 2020/5/1 14:23
 * 悬浮的View的辅助类
 */
class FloatingViewHelper private constructor(private val windowManager: WindowManager): ViewManager {

    companion object {
        fun create(windowManager: WindowManager): FloatingViewHelper {
            return FloatingViewHelper(windowManager)
        }
    }

    override fun addView(view: View, params: ViewGroup.LayoutParams) {
        view.parent?.let { parent ->
            if (parent is ViewManager) {
                parent.removeView(view)
            }
        }
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS//透明状态栏
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN//覆盖整个屏幕
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS//绘制状态栏背景
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不拦截焦点，否则所有界面将不可用
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, //允许窗口延伸到屏幕外部
                PixelFormat.TRANSPARENT
        )
        view.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        windowManager.addView(view, layoutParams)
    }

    override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
        TODO("Not yet implemented")
    }

    override fun removeView(view: View) {
        TODO("Not yet implemented")
    }


}
package liang.lollipop.lcountdown.utils

import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager
import liang.lollipop.lcountdown.service.ViewDragHelper

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
        fun createParams(): WindowManager.LayoutParams {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            return WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    type,
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                            or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不拦截焦点，否则所有界面将不可用
                            or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT
            )
        }
    }

    override fun addView(view: View, params: ViewGroup.LayoutParams) {
        view.parent?.let { parent ->
            if (parent is ViewManager) {
                parent.removeView(view)
            }
        }
        windowManager.addView(view, params)
        ViewDragHelper.bind(view).onLocationUpdate { v, offsetX, offsetY ->
            val layoutParams =  v.layoutParams as WindowManager.LayoutParams
            layoutParams.x += offsetX
            layoutParams.y += offsetY
            updateViewLayout(view, layoutParams)
        }
    }

    override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
        windowManager.updateViewLayout(view, params)
    }

    override fun removeView(view: View) {
        windowManager.removeView(view)
    }

}
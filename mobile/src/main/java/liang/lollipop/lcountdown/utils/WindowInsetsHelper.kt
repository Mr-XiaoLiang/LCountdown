package liang.lollipop.lcountdown.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

/**
 * @author lollipop
 * @date 2020/5/13 23:05
 * 窗口的Insets变化时的辅助处理器
 */
class WindowInsetsHelper (private val self: View) {

    private var rootParent: View? = null

    private val tempLocalArray = IntArray(2)
    private val tempBounds = Rect()
    private val windowBounds = Rect()
    private val tempOutSize = Rect()

    private val srcMargin = Rect()

    init {
        if (self.isAttachedToWindow) {
            rootParent = findRootParent(self)
        }
        self.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                rootParent = null
            }

            override fun onViewAttachedToWindow(v: View?) {
                rootParent = findRootParent(self)
            }
        })
    }

    fun updateByPadding(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (checkParent(view)) {
            val insets = getViewInsets(left, top, right, bottom)
            self.setPadding(insets.left, insets.top, insets.right, insets.bottom)
        }
    }

    fun updateByMargin(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (checkParent(view)) {
            val layoutParams = self.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                val insets = getViewInsets(left, top, right, bottom)
                layoutParams.setMargins(
                        srcMargin.left + insets.left,
                        srcMargin.top + insets.top,
                        srcMargin.right + insets.right,
                        srcMargin.bottom + insets.bottom)
                self.layoutParams = layoutParams
            }
        }
    }

    private fun getViewInsets(left: Int, top: Int, right: Int, bottom: Int): Rect {
        self.getLocationInWindow(tempLocalArray)
        tempBounds.set(0, 0, self.width, self.height)
        tempBounds.offset(tempLocalArray[0], tempLocalArray[1])
        self.getWindowVisibleDisplayFrame(windowBounds)
        tempOutSize.left = (left - tempBounds.left).limit()
        tempOutSize.top = (top - tempBounds.top).limit()
        tempOutSize.right = (right - windowBounds.width() + tempBounds.right).limit()
        tempOutSize.bottom = (bottom - windowBounds.height() +  tempBounds.bottom).limit()
        return tempOutSize
    }

    private fun Int.limit(): Int {
        if (this < 0) {
            return 0
        }
        return this
    }

    /**
     * 检查是否时同一个宿主
     */
    private fun checkParent(view: View): Boolean {
        rootParent?:return false
        val parent = findRootParent(view)
        return parent == rootParent
    }

    fun baseMargin(left: Int, top: Int, right: Int, bottom: Int) {
        srcMargin.set(left, top, right, bottom)
    }

    fun baseMarginFromNow() {
        val layoutParams = self.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            baseMargin(layoutParams.leftMargin, layoutParams.topMargin,
                    layoutParams.rightMargin, layoutParams.bottomMargin)
        }
    }

    private fun findRootParent(view: View): View {
        var target = view
        while (target.parent != null) {
            val parent = target.parent
            if (parent is View) {
                target = parent
            } else {
                break
            }
        }
        return target
    }

}
package liang.lollipop.lcountdown.dialog

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.OnWindowInsetsListener
import java.lang.RuntimeException
import java.util.*

/**
 * @author lollipop
 * @date 11/23/20 21:50
 */
class ToastDialog: OnWindowInsetsListener {

    companion object {
        private const val LAYOUT_ID = R.layout.dialog_toast
        private const val TEXT_ID = R.id.toastText
        private const val ACTION_ID = R.id.actionBtn

        private fun findGroup(rootGroup: View?): ViewGroup? {
            rootGroup?:return null
            if (rootGroup is CoordinatorLayout) {
                return rootGroup
            }
            if (rootGroup is FrameLayout) {
                return rootGroup
            }
            if (rootGroup is ViewGroup) {
                val views = LinkedList<View>()
                views.add(rootGroup)
                // 按层次遍历
                while (views.isNotEmpty()) {
                    val view = views.removeFirst()
                    if (view is CoordinatorLayout) {
                        return view
                    }
                    if (view is FrameLayout) {
                        return view
                    }
                    if (view is ViewGroup) {
                        for (i in 0 until view.childCount) {
                            views.addLast(view.getChildAt(i))
                        }
                    }
                }
            }
            return null
        }
    }

    private var toastView: View? = null

    private var thisToast: ToastTask? = null

    private var pendingToast: ToastTask? = null

    private var progress = 0F

    fun show(activity: Activity,
             text: Int, action: Int, callback: (DismissEvent) -> Unit) {
        val rootView = toastView?:createView(activity)
        val shownToast = thisToast
        if (shownToast != null) {
            pendingToast = ToastTask(text, action, callback)
            dismiss()
            return
        }
        // TODO
    }

    fun dismiss() {
        // TODO
    }

    private fun createView(activity: Activity): View {
        val rootGroup = findGroup(activity.window.decorView)?:throw RuntimeException(
                "Root group not found")
        val rootView = activity.layoutInflater.inflate(
                LAYOUT_ID, null, false)
        toastView = rootView
        rootView.visibility = View.INVISIBLE
        rootGroup.addView(rootView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return rootView
    }

    fun destroy() {

    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        TODO("Not yet implemented")
    }

    private data class ToastTask(
            val text: Int,
            val action: Int,
            val callback: (DismissEvent) -> Unit) {

    }

    enum class DismissEvent {
        TimeOut, Action, Swipe, Replace
    }

}
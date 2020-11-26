package liang.lollipop.lcountdown.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.OnWindowInsetsListener
import liang.lollipop.lcountdown.listener.OnWindowInsetsProvider
import liang.lollipop.lcountdown.listener.WindowInsetsHelper
import liang.lollipop.lcountdown.listener.WindowInsetsProviderHelper
import liang.lollipop.lcountdown.util.onUI
import liang.lollipop.lcountdown.util.task
import java.util.*
import kotlin.math.abs

/**
 * @author lollipop
 * @date 11/23/20 21:50
 */
class ToastDialog(private var insetsProvider: OnWindowInsetsProvider?):
        OnWindowInsetsListener,
        ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener {

    companion object {
        private const val LAYOUT_ID = R.layout.dialog_toast
        private const val TEXT_ID = R.id.toastText
        private const val ACTION_ID = R.id.actionBtn
        private const val CONTENT_ID = R.id.toastCard

        private const val CONTENT_NONE = 0

        private const val ANIMATION_DURATION = 300L

        /**
         * 进度精确到1%，小于1%认为已经是0了
         */
        private const val PROGRESS_HIDE = 0.01F

        /**
         * 进度精确到1%，大于99%认为已经是1了
         */
        private const val PROGRESS_SHOWN = 0.99F

        /**
         * 进度的阈值
         */
        private const val PROGRESS_THRESHOLD = 0.5F

        /**
         * 短时间显示
         */
        const val DELAY_SHORT = 2500L

        /**
         * 长时间显示
         */
        const val DELAY_LONE = 3500L

        private const val TIMEOUT_OFF = -1L

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

    private var touchable = false

    private val selfInsetsProviderHelper = WindowInsetsProviderHelper()

    private val animator = ValueAnimator().apply {
        duration = ANIMATION_DURATION
        addUpdateListener(this@ToastDialog)
        addListener(this@ToastDialog)
    }

    private val timeoutTask = task {
        dismiss(DismissEvent.TimeOut)
    }

    init {
        insetsProvider?.addOnWindowInsetsListener(this)
    }

    fun show(activity: Activity, text: Int) {
        show(activity, text, DELAY_SHORT, CONTENT_NONE) {}
    }

    fun show(activity: Activity, text: Int, action: Int, callback: (DismissEvent) -> Unit) {
        show(activity, text, TIMEOUT_OFF, action, callback)
    }

    fun show(activity: Activity, text: Int, delay: Long) {
        show(activity, text, delay, CONTENT_NONE) {}
    }

    fun preload(activity: Activity) {
        toastView?:createView(activity)
    }

    private fun show(activity: Activity,
             text: Int, delay: Long, action: Int, callback: (DismissEvent) -> Unit) {
        toastView?:createView(activity)
        showToast(ToastTask(text, delay, action, callback))
    }

    private fun showToast(task: ToastTask) {
        toastView?:return
        val shownToast = thisToast
        if (shownToast != null) {
            pendingToast = task
            dismiss(DismissEvent.Replace)
            return
        }
        thisToast = task
        find<TextView>(TEXT_ID) {
            setText(task.text)
        }
        find<TextView>(ACTION_ID) {
            if (task.action == CONTENT_NONE) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                setText(task.action)
            }
        }
        doAnimation(true, task.delay)
    }

    private fun dismiss(event: DismissEvent) {
        val callback = thisToast?.callback
        onUI {
            callback?.invoke(event)
        }
        thisToast = null
        doAnimation(false)
    }

    private fun doAnimation(isOpen: Boolean, timeoutOffset: Long = 0) {
        // 动画开始，锁定手指触摸，防止手势打断操作
        touchable = false
        animator.cancel()
        val endValue = if (isOpen) { 1F } else { 0F }
        val duration = (abs(endValue - progress) * ANIMATION_DURATION).toLong()
        timeoutTask.cancel()
        // 如果是打开操作并且激活超时，那么设置超时
        // 可能存在手势操作中断，使用展开动画归位时，使得toast延时，属于正常现象
        if (isOpen && timeoutOffset >= 0) {
            timeoutTask.delay(duration + timeoutOffset)
        }
        animator.setFloatValues(progress, endValue)
        animator.duration = duration
        animator.start()
    }

    private fun createView(activity: Activity): View {
        val rootGroup = findGroup(activity.window.decorView)?:throw RuntimeException(
                "Root group not found")
        val self = activity.layoutInflater.inflate(
                LAYOUT_ID, null, false)
        toastView = self
        find<View>(ACTION_ID) {
            setOnClickListener {
                dismiss(DismissEvent.Action)
            }
        }
        self.visibility = View.INVISIBLE
        self.post {
            progress = 0F
            updateCard()
        }
        rootGroup.addView(self,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        selfInsetsProviderHelper.addOnWindowInsetsListener(
                WindowInsetsHelper.SimpleInsetsCallback(self, false))
        return self
    }

    private fun onAnimationEnd(isOpen: Boolean) {
        if (!isOpen) {
            val pending = pendingToast
            if (pending != null) {
                pendingToast = null
                thisToast = null
                showToast(pending)
            } else {
                toastView?.visibility = View.INVISIBLE
            }
        }
    }

    private fun <T: View> find(id: Int, run: T.() -> Unit) {
        toastView?.findViewById<T>(id)?.let(run)
    }

    fun destroy() {
        animator.cancel()
        timeoutTask.cancel()
        insetsProvider?.removeOnWindowInsetsListener(this)
        insetsProvider = null
        selfInsetsProviderHelper.destroy()
        // TODO
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        selfInsetsProviderHelper.onInsetsChange(root, left, top, right, bottom)
    }

    private fun updateCard() {
        find<View>(CONTENT_ID) {
            translationY = (top + height) * (progress - 1)
        }
    }

    private data class ToastTask(
            val text: Int,
            val delay: Long,
            val action: Int,
            val callback: (DismissEvent) -> Unit)

    enum class DismissEvent {
        TimeOut, Action, Swipe, Replace
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation == animator) {
            progress = animation.animatedValue as Float
            updateCard()
        }
    }

    override fun onAnimationStart(animation: Animator?) {
        if (animation == animator) {
            toastView?.visibility = View.VISIBLE
        }
    }

    override fun onAnimationEnd(animation: Animator?) {
        if (animation == animator) {
            when {
                progress < PROGRESS_HIDE -> {
                    onAnimationEnd(false)
                }
                progress > PROGRESS_SHOWN -> {
                    onAnimationEnd(true)
                }
                else -> {
                    doAnimation(progress > PROGRESS_THRESHOLD)
                }
            }
        }
    }

    override fun onAnimationCancel(animation: Animator?) {
        // 取消了动画，那么不做任何处理
    }

    override fun onAnimationRepeat(animation: Animator?) {
        // 动画重复播放，不做处理
    }

}
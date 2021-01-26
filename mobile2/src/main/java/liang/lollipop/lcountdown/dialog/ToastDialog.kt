package liang.lollipop.lcountdown.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.OnWindowInsetsListener
import liang.lollipop.lcountdown.listener.OnWindowInsetsProvider
import liang.lollipop.lcountdown.listener.WindowInsetsHelper
import liang.lollipop.lcountdown.listener.WindowInsetsProviderHelper
import liang.lollipop.lcountdown.util.SingleTouchHelper
import liang.lollipop.lcountdown.util.onUI
import liang.lollipop.lcountdown.util.task
import java.util.*
import kotlin.math.abs
import kotlin.math.min

/**
 * @author lollipop
 * @date 11/23/20 21:50
 * 一个用于Toast显示的dialog
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

        fun once(activity: Activity, text: Int) {
            showOnce(activity, text, DELAY_SHORT, CONTENT_NONE) {}
        }

        fun once(activity: Activity, text: Int, action: Int, callback: (DismissEvent) -> Unit) {
            showOnce(activity, text, TIMEOUT_OFF, action, callback)
        }

        fun once(activity: Activity, text: Int, delay: Long) {
            showOnce(activity, text, delay, CONTENT_NONE) {}
        }

        private fun showOnce(activity: Activity,
                     text: Int, delay: Long, action: Int, callback: (DismissEvent) -> Unit) {
            val insetsProvider: OnWindowInsetsProvider? = if (activity is OnWindowInsetsProvider) {
                activity
            } else {
                null
            }
            val toastDialog = ToastDialog(insetsProvider)
            toastDialog.removeWhenDismiss = true
            toastDialog.show(activity, text, delay, action, callback)
        }
    }

    private var toastView: View? = null

    private var thisToast: ToastTask? = null

    private var pendingToast: ToastTask? = null

    private var progress = 0F

    private var touchable = false

    private val selfInsetsProviderHelper = WindowInsetsProviderHelper()

    private var removeWhenDismiss = false

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
        DragHelper.bind(self, CONTENT_ID,
                { touchable },
                { find<View>(CONTENT_ID) {
                        val allHeight = height + top
                        if (allHeight * -0.3 > translationY) {
                            // 关闭
                            progress = translationY / allHeight + 1
                            dismiss(DismissEvent.Swipe)
                        } else {
                            // 打开
                            doAnimation(true, thisToast?.delay?:0)
                        }
                    }
                })
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
                if (removeWhenDismiss) {
                    remove()
                }
            }
        } else {
            touchable = thisToast?.delay != TIMEOUT_OFF
        }
    }

    private fun <T: View> find(id: Int, run: T.() -> Unit) {
        toastView?.findViewById<T>(id)?.let(run)
    }

    private fun remove() {
        toastView?.let {
            val parent = it.parent
            if (parent != null && parent is ViewManager) {
                parent.removeView(it)
            }
        }
    }

    fun destroy() {
        animator.cancel()
        timeoutTask.cancel()
        insetsProvider?.removeOnWindowInsetsListener(this)
        insetsProvider = null
        selfInsetsProviderHelper.destroy()
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

    private class DragHelper private constructor(
            private val targetId: Int,
            private val dragEnable: () -> Boolean,
            private val onUp: () -> Unit): View.OnTouchListener {

        companion object {
            fun bind(viewGroup: View,
                     targetId: Int,
                     dragEnable: () -> Boolean,
                     onUp: () -> Unit) {
                viewGroup.setOnTouchListener(DragHelper(targetId, dragEnable, onUp))
            }
        }

        private val touchDown = PointF()
        private val firstLocation = PointF()

        private val singleTouchHelper = SingleTouchHelper(this::onSingleTouch)

        private fun onSingleTouch(
                view: View, type: SingleTouchHelper.Event, x: Float, y: Float): Boolean {
            if (view is ViewGroup) {
                return onGroupTouch(view, type, x, y)
            }
            return false
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            event?:return false
            v?:return false
            if (!dragEnable.invoke()) {
                return false
            }
            return singleTouchHelper.onTouch(v, event)
        }

        private fun onGroupTouch(
                group: ViewGroup, type: SingleTouchHelper.Event, x: Float, y: Float): Boolean {
            when (type) {
                SingleTouchHelper.Event.Down -> {
                    val isTouched = touchOnTarget(group, x, y)
                    if (!isTouched) {
                        return false
                    }
                    touchDown.set(x, y)
                    updateTargetLocation(group)
                }
                SingleTouchHelper.Event.Move -> {
                    // 只移动Y轴
                    val offsetY = y - touchDown.y
                    target(group)?.let {
                        val translationY = offsetY + firstLocation.y
                        it.translationY = min(translationY, 0F)
                    }
                }
                SingleTouchHelper.Event.Up -> {
                    onUp.invoke()
                }
                SingleTouchHelper.Event.Change -> {
                    if (!touchOnTarget(group, x, y)) {
                        onUp.invoke()
                        return false
                    }
                }
                SingleTouchHelper.Event.Cancel -> {
                    onUp.invoke()
                }
            }
            return true
        }

        private fun touchOnTarget(group: ViewGroup, x: Float, y: Float): Boolean {
            if (x > group.width || y > group.height) {
                return false
            }
            val child = target(group)?:return false
            val left = child.left + child.translationX
            val top = child.top + child.translationY
            val right = child.right + child.translationX
            val bottom = child.bottom + child.translationY
            return left < x && top < y && right > x && bottom > y
        }

        private fun updateTargetLocation(group: ViewGroup) {
            val child = target(group)
            if (child == null) {
                firstLocation.set(0F, 0F)
                return
            }
            val left = child.translationX
            val top = child.translationY
            firstLocation.set(left, top)
        }

        private fun target(group: ViewGroup): View? {
            return group.findViewById(targetId)
        }

    }

}
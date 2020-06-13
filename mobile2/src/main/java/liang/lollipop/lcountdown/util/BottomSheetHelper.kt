package liang.lollipop.lcountdown.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.abs

/**
 * @author lollipop
 * @date 2020/6/9 23:13
 * 底部抽屉的辅助器
 */
class BottomSheetHelper(
        private val sheetBody: View,
        private val sheetGrip: View,
        private val linkageView: View,
        private val sheetContent: View):
        Animator.AnimatorListener,
        ValueAnimator.AnimatorUpdateListener {

    companion object {
        private const val PROGRESS_MAX = 1F
        private const val PROGRESS_MIN = 0F
        private const val ANIMATION_DURATION = 300L
    }

    private var progress: Float = 0F
        set(value) {
            field = value
            onProgressChange()
        }

    private val valueAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            addListener(this@BottomSheetHelper)
            addUpdateListener(this@BottomSheetHelper)
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    var paddingBottom = 0

    private var pendingTask: PendingTask? = null

    private var isInit: Boolean = false

    val isHide: Boolean
        get() {
            return (progress * 100).toInt() < 2
        }

    val isShown: Boolean
        get() {
            return (progress * 100).toInt() > 98
        }

    private var statusChangeListener: ((Boolean) -> Unit)? = null

    init {
        sheetGrip.post {
            isInit = true
            checkPending()
        }
    }

    fun destroy() {
        isInit = false
        valueAnimator.cancel()
        pendingTask = null
    }

    fun stop() {
        if (!isInit) {
            return
        }
        if (valueAnimator.isStarted && valueAnimator.isRunning) {
            valueAnimator.end()
        }
    }

    fun onStatusChange(listener: (Boolean) -> Unit) {
        statusChangeListener = listener
    }

    fun show(animation: Boolean = true) {
        post(animation, PROGRESS_MAX)
    }

    fun hide(animation: Boolean = true) {
        post(animation, PROGRESS_MIN)
    }

    fun changeStatus() {
        if (progress > 0.5F) {
            hide()
        } else {
            show()
        }
    }

    private fun checkPending() {
        if (!isInit) {
            return
        }
        pendingTask?.let {
            if (it.animation) {
                startAnimation(it.target)
            } else {
                progress = it.target
                onAnimationEnd(valueAnimator)
            }
        }
        pendingTask = null
    }

    private fun post(animation: Boolean, target: Float) {
        pendingTask = PendingTask(animation, target)
        checkPending()
    }

    private fun startAnimation(endProgress: Float) {
        cancel()
        val now = progress
        val duration = (abs(now - endProgress)
                / abs(PROGRESS_MAX - PROGRESS_MIN)
                * ANIMATION_DURATION).toLong()
        valueAnimator.setFloatValues(now, endProgress)
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    private fun cancel() {
        valueAnimator.cancel()
    }

    private fun onProgressChange() {
        val now = progress
        val height = sheetContent.height - paddingBottom
        val offset = (PROGRESS_MAX - now) * height
        sheetBody.translationY = offset
        sheetGrip.rotation = now * 180
        linkageView.translationY = height * now * -0.5F
    }

    override fun onAnimationRepeat(animation: Animator?) { }

    override fun onAnimationEnd(animation: Animator?) {
        if (isHide) {
            sheetContent.visibility = View.INVISIBLE
        }
        statusChangeListener?.invoke(isShown)
    }

    override fun onAnimationCancel(animation: Animator?) { }

    override fun onAnimationStart(animation: Animator?) {
        if (sheetBody.visibility != View.VISIBLE) {
            sheetBody.visibility = View.VISIBLE
        }
        if (sheetContent.visibility != View.VISIBLE) {
            sheetContent.visibility = View.VISIBLE
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation == valueAnimator) {
            progress = animation.animatedValue as Float
        }
    }

    private data class PendingTask(val animation: Boolean, val target: Float)

}
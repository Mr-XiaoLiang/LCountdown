package liang.lollipop.lcountdown.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Outline
import android.graphics.Path
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.OnWindowInsetsListener
import liang.lollipop.lcountdown.base.OnWindowInsetsProvider
import liang.lollipop.lcountdown.utils.WindowInsetsHelper
import java.util.*

/**
 * @author lollipop
 * @date 2020/5/12 23:03
 * 帘子一样垂下的Dialog
 * 它会在保持页面不变的情况下尽可能的提供更多的内容空间
 */
class CurtainDialog private constructor(
        private val rootGroup: ViewGroup,
        private val onWindowInsetsProvider: OnWindowInsetsProvider?):
        View.OnClickListener,
        ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener,
        OnWindowInsetsListener {

    companion object {
        private const val DURATION = 300L

        fun with(activity: Activity): CurtainDialog {
            val provider = if (activity is OnWindowInsetsProvider) { activity } else { null }
            return CurtainDialog(findGroup(activity.window.decorView)
                    ?:throw InflateException("Not fount root group"), provider)
        }

        fun with(fragment: Fragment): CurtainDialog {
            val provider = if (fragment is OnWindowInsetsProvider) { fragment } else { null }
            return CurtainDialog(findGroup(fragment.view)
                    ?:throw InflateException("Not fount root group"), provider)
        }

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

    private val dialogView = LayoutInflater.from(rootGroup.context)
            .inflate(R.layout.dialog_curtain, rootGroup, false)

    private val backgroundView: View = dialogView.findViewById(R.id.backgroundView)
    private val contentGroup: FrameLayout = dialogView.findViewById(R.id.contentGroup)
    private val closeBtn: View = dialogView.findViewById(R.id.closeBtn)

    private var progress = 0F
    private var pullCurtain = false
    private val valueAnimator = ValueAnimator()
    private var once = false

    private val windowInsetsHelper: WindowInsetsHelper by lazy {
        WindowInsetsHelper(contentGroup)
    }

    init {
        valueAnimator.addUpdateListener(this)
        valueAnimator.addListener(this)
        dialogView.setOnClickListener(this)
        closeBtn.setOnClickListener(this)
        contentGroup.setOnClickListener{}
        dialogView.visibility = View.INVISIBLE
    }

    fun dismiss() {
        if (!rootGroup.isAttachedToWindow || rootGroup.parent == null || !rootGroup.isShown) {
            return
        }
        doAnimation(false)
    }

    fun showOnce() {
        once = true
        show()
    }

    fun show() {
        if (dialogView.parent == null || dialogView.parent != rootGroup) {
            dialogView.parent?.let { parent ->
                if (parent is ViewManager) {
                    parent.removeView(dialogView)
                }
            }
            rootGroup.addView(dialogView)
            windowInsetsHelper.baseMarginFromNow()
            onWindowInsetsProvider?.addOnWindowInsetsProvider(this)
        }
        dialogView.post {
            doAnimation(true)
        }
    }

    private fun removeView() {
        if (dialogView.isAttachedToWindow) {
            dialogView.parent?.let { parent ->
                if (parent is ViewManager) {
                    parent.removeView(dialogView)
                }
            }
        }
        onWindowInsetsProvider?.removeOnWindowInsetsProvider(this)
    }

    private fun doAnimation(open: Boolean) {
        pullCurtain = open
        valueAnimator.cancel()
        val endValue = if (open) { 1F } else { 0F }
        valueAnimator.duration = if (open) {
            (endValue - progress) * DURATION
        } else {
            (progress - endValue) * DURATION
        }.toLong()
        valueAnimator.setFloatValues(progress, endValue)
        valueAnimator.start()
    }

    private fun onProgressChange() {
        if (progress > 1F) {
            progress = 1F
        } else if (progress < 0F) {
            progress = 0F
        }
        backgroundView.alpha = progress
        contentGroup.translationY = (contentGroup.top + contentGroup.height) * (progress - 1)
    }

    private fun hideCloseBtn() {
        closeBtn.animate().let { anim ->
            anim.cancel()
            closeBtn.scaleX = 1F
            closeBtn.scaleY = 1F
            anim.scaleX(0F)
            anim.scaleY(0F)
            anim.start()
        }
    }

    private fun showCloseBtn() {
        closeBtn.animate().let { anim ->
            anim.cancel()
            closeBtn.scaleX = 0F
            closeBtn.scaleY = 0F
            anim.scaleX(1F)
            anim.scaleY(1F)
            anim.start()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            dialogView, closeBtn -> {
                dismiss()
            }
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation == valueAnimator) {
            progress = animation.animatedValue as Float
            onProgressChange()
        }
    }

    override fun onAnimationRepeat(animation: Animator?) { }

    override fun onAnimationEnd(animation: Animator?) {
        if (!pullCurtain) {
            dialogView.visibility = View.INVISIBLE
            if (once) {
                removeView()
            }
        } else {
            showCloseBtn()
        }
    }

    override fun onAnimationCancel(animation: Animator?) {  }

    override fun onAnimationStart(animation: Animator?) {
        if (pullCurtain) {
            dialogView.visibility = View.VISIBLE
            closeBtn.scaleX = 0F
            closeBtn.scaleY = 0F
        } else {
            hideCloseBtn()
        }
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        windowInsetsHelper.updateByMargin(root, left, top, right, bottom)
    }

}
package liang.lollipop.lcountdown.utils

import android.util.Log
import android.view.ViewGroup
import liang.lollipop.lcountdown.view.CheckImageView

/**
 * @date: 2019-06-29 17:35
 * @author: lollipop
 * 复选框 View 的单选辅助类
 */
class CheckedButtonHelper private constructor(private val parent: ViewGroup): CheckImageView.OnCheckedChangeListener {

    companion object {
        fun bind(group: ViewGroup): CheckedButtonHelper {
            return CheckedButtonHelper(group)
        }
    }

    init {
        onViewChange()
    }

    private var checkChangeListener: ((CheckImageView?, Boolean) -> Unit)? = null

    fun onViewChange() {
        val count = parent.childCount
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            if (view is CheckImageView) {
                view.onChecked(this)
                Log.d("Lollipop", "onViewChange: ${view.id}")
            }
        }
    }

    override fun onCheckedChange(view: CheckImageView, isChecked: Boolean) {
        setChecked(view, isChecked, true)
    }

    fun setChecked(view: CheckImageView?, isChecked: Boolean, cellListener: Boolean) {
        val count = parent.childCount
        for (i in 0 until count) {
            val it = parent.getChildAt(i)
            if (it is CheckImageView && it != view) {
                it.setChecked(checked = false, callListener = false)
            }
        }
        view?.setChecked(checked = isChecked, callListener = false)
        if (cellListener) {
            if (!isChecked) {
                checkChangeListener?.invoke(null, false)
            } else {
                checkChangeListener?.invoke(view, true)
            }
        }
    }

    fun onChecked(listener: ((CheckImageView?, Boolean) -> Unit)?) {
        checkChangeListener = listener
    }

}
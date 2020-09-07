package liang.lollipop.lcountdown.util

import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.view.CheckImageView

/**
 * @author lollipop
 * @date 9/8/20 01:24
 */
class GravityViewHelper(group: ViewGroup): View.OnClickListener {

    private val viewList = ArrayList<CheckImageView>()

    private var selectedIndex = -1

    private var callback: ((view: View) -> Unit)? = null

    init {
        val childCount = group.childCount
        for (index in 0 until childCount) {
            val child = group.getChildAt(index)
            if (child is CheckImageView) {
                child.setOnClickListener(this)
                viewList.add(child)
            }
        }
        if (viewList.isNotEmpty()) {
            selectedIndex = 0
        }
        updateCheckStatus()
    }

    fun checked(id: Int) {
        for (index in viewList.indices) {
            if (viewList[index].id == id) {
                selectedIndex == index
                break
            }
        }
        updateCheckStatus()
    }

    private fun updateCheckStatus() {
        for (index in viewList.indices) {
            viewList[index].isChecked = (index == selectedIndex)
        }
    }

    override fun onClick(v: View?) {
        v?:return
        val index = viewList.indexOf(v)
        if (selectedIndex >= 0 && index < 0) {
            return
        }
        selectedIndex = index
        updateCheckStatus()
        callback?.invoke(v)
    }

    fun onCheckedChange(callback: (view: View) -> Unit) {
        this.callback = callback
    }

}
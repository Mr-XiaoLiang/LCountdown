package liang.lollipop.lcountdown.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.listener.BackPressedListener

/**
 * @author lollipop
 * @date 2020/5/14 22:58
 * 内部Dialog的呈现器
 */
open class InnerDialogProvider: BackPressedListener {

    private var callback: Callback? = null

    open val layoutId = 0

    private var view: View? = null

    fun bindCallback(callback: Callback?) {
        this.callback = callback
    }

    protected fun dismiss() {
        callback?.callDismiss()
    }

    open fun onCreate() {

    }

    open fun onStart() {

    }

    open fun onStop() {

    }

    open fun onDestroy() {

    }

    open fun createContentView(group: ViewGroup): View {
        return createViewById(group)
    }

    open fun onViewCreated(view: View) {

    }

    private fun createViewById(group: ViewGroup): View {
        if (layoutId == 0) {
            throw NullPointerException("layoutId == 0")
        }
        val thisView = LayoutInflater.from(group.context).inflate(layoutId, group, false)
        view = thisView
        return thisView
    }

    interface Callback {
        fun callDismiss()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    fun <T: View> find(id: Int): T? {
        return view?.findViewById<T>(id)
    }

}
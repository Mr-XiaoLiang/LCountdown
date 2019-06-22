package liang.lollipop.lbaselib.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import liang.lollipop.lbaselib.util.LItemTouchCallback
import liang.lollipop.lbaselib.util.LItemTouchHelper
import androidx.core.util.Pair

/**
 * Created by lollipop on 2018/1/2.
 * @author Fragment的基础类
 */
open class BaseFragment: Fragment(),
        SimpleHandler.HandlerCallback,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener,
        LItemTouchCallback.OnItemTouchCallbackListener,
        LItemTouchCallback.OnItemTouchStateChangedListener{



    protected var handler: Handler = SimpleHandler(this)
    protected var rootView: View? = null

    companion object {

        val SCROLL_STATE_IDLE = RecyclerView.SCROLL_STATE_IDLE
        val SCROLL_STATE_SETTLING = RecyclerView.SCROLL_STATE_SETTLING
        val SCROLL_STATE_DRAGGING = RecyclerView.SCROLL_STATE_DRAGGING

    }

    private fun findRootView(view: View) {
        //获取根节点View，用于弹出SnackBar
        rootView = view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findRootView(view)
    }

    override fun onClick(v: View?) {

    }

    override fun onHandler(message: Message) {
    }

    override fun onRefresh() {
    }

    open fun getTitle(): String {
        return ""
    }

    open fun getTitleId(): Int {
        return 0
    }

    protected fun getTouchHelper(recyclerView: RecyclerView): LItemTouchHelper {
        val helper = LItemTouchHelper.newInstance(recyclerView, this)
        helper.setStateChangedListener(this)
        return helper
    }

    protected fun startActivity(intent: Intent, vararg pair: Pair<View, String>) {
        if (pair.isEmpty()) {
            super.startActivity(intent)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent)
        } else {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, *pair)
            startActivity(intent, optionsCompat.toBundle())
        }
    }

    override fun onSwiped(adapterPosition: Int) {
    }

    override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
        return false
    }

    override fun onItemViewClick(holder: RecyclerView.ViewHolder?, v: View) {
    }

    override fun onItemTouchStateChanged(viewHolder: RecyclerView.ViewHolder?, status: Int) {
    }

}
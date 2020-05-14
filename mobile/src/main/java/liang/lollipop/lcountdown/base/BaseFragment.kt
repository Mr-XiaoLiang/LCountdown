package liang.lollipop.lcountdown.base

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import liang.lollipop.lcountdown.utils.BackPressedProviderHelper
import liang.lollipop.lcountdown.utils.LItemTouchCallback
import liang.lollipop.lcountdown.utils.SimpleHandler
import liang.lollipop.lcountdown.utils.WindowInsetsProviderHelper

/**
 * Created by lollipop on 2018/1/2.
 * @author Fragment的基础类
 */
open class BaseFragment: Fragment(),
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener,
        LItemTouchCallback.OnItemTouchCallbackListener,
        LItemTouchCallback.OnItemTouchStateChangedListener,
        OnWindowInsetsProvider,
        OnWindowInsetsListener,
        BackPressedProvider,
        BackPressedListener {

    protected val handler: Handler by lazy {
        SimpleHandler {
            onHandler(it)
        }
    }

    private val windowInsetsProviderHelper: WindowInsetsProviderHelper by lazy {
        WindowInsetsProviderHelper()
    }

    private val backPressedProviderHelper: BackPressedProviderHelper by lazy {
        BackPressedProviderHelper()
    }

    override fun onClick(v: View?) {

    }

    open fun onHandler(message: Message) {
    }

    override fun onRefresh() {
    }

    open fun getTitle(): String {
        return ""
    }

    open fun getTitleId(): Int {
        return 0
    }

    protected fun getTouchHelper(recyclerView: RecyclerView): liang.lollipop.lcountdown.utils.LItemTouchHelper {
        val helper = liang.lollipop.lcountdown.utils.LItemTouchHelper.newInstance(recyclerView, this)
        helper.setStateChangedListener(this)
        return helper
    }

    protected fun startActivity(intent: Intent, vararg pair: Pair<View, String>) {
        if (pair.isEmpty()) {
            super.startActivity(intent)
            return
        }
        startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, *pair).toBundle())
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

    protected fun bindClick(vararg views: View) {
        for (v in views) {
            v.setOnClickListener(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnWindowInsetsProvider) {
            context.addOnWindowInsetsProvider(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.let {
            if (it is OnWindowInsetsProvider) {
                it.removeOnWindowInsetsProvider(this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        context?.let {
            if (it is BackPressedProvider) {
                it.addBackPressedListener(this)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        context?.let {
            if (it is BackPressedProvider) {
                it.removeBackPressedListener(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedProviderHelper.clear()
    }

    override fun addOnWindowInsetsProvider(listener: OnWindowInsetsListener) {
        windowInsetsProviderHelper.addOnWindowInsetsProvider(listener)
    }

    override fun removeOnWindowInsetsProvider(listener: OnWindowInsetsListener) {
        windowInsetsProviderHelper.removeOnWindowInsetsProvider(listener)
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        windowInsetsProviderHelper.onInsetsChange(root, left, top, right, bottom)
    }

    override fun addBackPressedListener(listener: BackPressedListener) {
        backPressedProviderHelper.addBackPressedListener(listener)
    }

    override fun removeBackPressedListener(listener: BackPressedListener) {
        backPressedProviderHelper.removeBackPressedListener(listener)
    }

    override fun onBackPressed(): Boolean {
        return backPressedProviderHelper.onBackPressed()
    }

}
package liang.lollipop.lcountdown.base

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import liang.lollipop.lcountdown.utils.LItemTouchCallback
import liang.lollipop.lcountdown.utils.LItemTouchHelper
import liang.lollipop.lcountdown.utils.SimpleHandler

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 基础的Activity
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(),
        SwipeRefreshLayout.OnRefreshListener,
        LItemTouchCallback.OnItemTouchStateChangedListener,
        LItemTouchCallback.OnItemTouchCallbackListener,
        View.OnClickListener {

    /**是否显示返回按钮*/
    protected var isShowBack = true

    /**Handler*/
    protected val handler: Handler by lazy {
        SimpleHandler {
            onHandler(it)
        }
    }

    companion object {
        /**用来做关联动画的别名*/
        protected const val TRANSITION_NAME = "ROOT_VIEW"
    }

    override fun onStart() {
        super.onStart()
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(isShowBack)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        findRootView()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        findRootView()
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
        findRootView()
    }

    private fun initRootGroup(group: View) {
        val attributes = window.attributes
        attributes.systemUiVisibility = (
                attributes.systemUiVisibility
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { _, insets ->
            onInsetsChange(group, insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
        }
    }

    protected open fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        root.setPadding(left, top, right, bottom)
    }

    open fun onHandler(message: Message) {}

    private fun findRootView() {
        //获取根节点View，用于弹出SnackBar
        val contentParent = findViewById<ViewGroup>(android.R.id.content)
        val rootView = if (contentParent.childCount > 0) contentParent.getChildAt(0) else contentParent
        rootView.transitionName = TRANSITION_NAME
        initRootGroup(rootView)
    }

    protected open fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    protected fun getTouchHelper(recyclerView: RecyclerView): LItemTouchHelper {
        val helper = LItemTouchHelper.newInstance(recyclerView, this)
        helper.setStateChangedListener(this)
        return helper
    }

    protected fun startActivityForResult(intent: Intent, requestCode: Int, vararg pair: Pair<View, String>) {
        if (pair.isEmpty()) {
            super.startActivityForResult(intent, requestCode)
            return
        }
        startActivityForResult(intent, requestCode,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pair).toBundle())
    }

    override fun onRefresh() {
    }

    override fun onClick(v: View?) {
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

    protected fun alert(): AlertDialog.Builder {
        return AlertDialog.Builder(this)
    }

    protected fun bindClick(vararg views: View) {
        for (v in views) {
            v.setOnClickListener(this)
        }
    }

}
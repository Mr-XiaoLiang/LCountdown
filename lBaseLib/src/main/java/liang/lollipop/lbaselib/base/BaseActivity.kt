package liang.lollipop.lbaselib.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import liang.lollipop.lbaselib.util.LItemTouchCallback
import liang.lollipop.lbaselib.util.LItemTouchHelper

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 基础的Activity
 */
open class BaseActivity : AppCompatActivity(),
        SimpleHandler.HandlerCallback,
        SwipeRefreshLayout.OnRefreshListener,
        LItemTouchCallback.OnItemTouchStateChangedListener,
        LItemTouchCallback.OnItemTouchCallbackListener,
        View.OnClickListener {

    /**是否显示返回按钮*/
    protected var isShowBack = true

    /**Handler*/
    protected val handler: Handler by lazy {
        SimpleHandler(this)
    }

    /**根View*/
    protected var rootView: View? = null

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

    override fun onHandler(message: Message) {
    }

    private fun findRootView() {
        //获取根节点View，用于弹出SnackBar
        val contentParent = findViewById<ViewGroup>(android.R.id.content)
        rootView = if (contentParent.childCount > 0) contentParent.getChildAt(0) else contentParent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView!!.transitionName = TRANSITION_NAME
        }
    }

    protected open fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    protected fun getTouchHelper(recyclerView: RecyclerView): LItemTouchHelper {
        val helper = LItemTouchHelper.newInstance(recyclerView, this)
        helper.setStateChangedListener(this)
        return helper
    }

    @SuppressLint("RestrictedApi")
    protected fun startActivityForResult(intent: Intent, requestCode: Int, vararg pair: Pair<View, String>) {
        if (pair.isEmpty()) {
            super.startActivity(intent)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(intent, requestCode)
        } else {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pair)
            startActivityForResult(intent, requestCode, optionsCompat.toBundle())
        }
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
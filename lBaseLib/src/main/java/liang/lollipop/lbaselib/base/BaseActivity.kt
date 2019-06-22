package liang.lollipop.lbaselib.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import liang.lollipop.lbaselib.util.AppBarStateChangeListener
import liang.lollipop.lbaselib.util.LItemTouchCallback
import liang.lollipop.lbaselib.util.LItemTouchHelper
import liang.lollipop.lbaselib.util.PermissionsUtil
import java.util.*
import androidx.core.util.Pair

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 基础的Activity
 */
open class BaseActivity: AppCompatActivity(),
        SimpleHandler.HandlerCallback,
        SwipeRefreshLayout.OnRefreshListener,
        LItemTouchCallback.OnItemTouchStateChangedListener,
        LItemTouchCallback.OnItemTouchCallbackListener,
        View.OnClickListener,
        AppBarStateChangeListener.OnAppBarStateChangeListener{

    /**是否显示返回按钮*/
    protected var isShowBack = true
    /**Handler*/
    protected var handler: Handler = SimpleHandler(this)
    /**用来做关联动画的别名*/
    protected val TRANSITION_NAME = "ROOT_VIEW"
    /**根View*/
    protected var rootView: View? = null
    /**自动隐藏系统UI*/
    protected var autoHintSystemUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {

        val SCROLL_STATE_IDLE = RecyclerView.SCROLL_STATE_IDLE
        val SCROLL_STATE_SETTLING = RecyclerView.SCROLL_STATE_SETTLING
        val SCROLL_STATE_DRAGGING = RecyclerView.SCROLL_STATE_DRAGGING

        val PLEASE_REFRESH = 9999

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

    protected open fun setToolbar(id: Int) {
        setToolbar(findViewById<Toolbar>(id))
    }

    protected open fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    protected fun getContext(): Context {
        return this
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
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pair)
            startActivity(intent, optionsCompat.toBundle())
        }
    }

    @SuppressLint("RestrictedApi")
    protected fun startActivityForResult(intent: Intent, requestCode: Int, vararg pair: Pair<View, String>) {
        if (pair.isEmpty()) {
            super.startActivity(intent)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(intent,requestCode)
        } else {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pair)
            startActivityForResult(intent, requestCode, optionsCompat.toBundle())
        }
    }

    protected fun withAppBarStates(appBarLayout: AppBarLayout){
        appBarLayout.addOnOffsetChangedListener(AppBarStateChangeListener(this))
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

    override fun onAppBarStateChangeed(appBarLayout: AppBarLayout, state: Int) {
        if(autoHintSystemUI){
            when(state){
                AppBarStateChangeListener.IDLE -> {
                    hideSystemUI()
                }

                AppBarStateChangeListener.EXPANDED -> {
                    showSystemUI()
                }

                AppBarStateChangeListener.COLLAPSED -> {
                    hideSystemUI()
                }
            }
        }
    }

    protected fun hideSystemUI(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }

    protected fun showSystemUI(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    protected fun checkPermissions(requestCode:Int,pp:PermissionsUtil.OnPermissionsPass?,vararg permissions: String){
        PermissionsUtil.checkPermissions(this,requestCode,pp,*permissions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionsUtil.verifyPermissions(grantResults)) {
            onPermissionsGrant(requestCode)
        } else {
            onPermissionsUnAllow(requestCode, PermissionsUtil.verifyPermissions(permissions, grantResults))
        }
    }

    /**
     * 权限申请完成的回调
     *
     * @param requestCode
     */
    protected open fun onPermissionsGrant(requestCode: Int) {

    }

    /**
     * 权限申请未完成的回调
     *
     * @param requestCode 申请号码
     * @param permissions 未授权的权限
     */
    protected open fun onPermissionsUnAllow(requestCode: Int, permissions: ArrayList<String>) {

    }

    protected open fun popPermissionsDialog(msg:String,title:String,yesBtn:String,noBtn:String){
        PermissionsUtil.popPermissionsDialog(this,msg,title,yesBtn,noBtn)
    }

    protected fun alert(title:String,msg:String){
        AlertDialog.Builder(this).setTitle(title).setMessage(msg).show()
    }

    protected fun alert(title:Int,msg:Int){
        AlertDialog.Builder(this).setTitle(title).setMessage(msg).show()
    }

    protected fun alert(title:String,msg:Int){
        AlertDialog.Builder(this).setTitle(title).setMessage(msg).show()
    }

    protected fun alert(title:Int,msg:String){
        AlertDialog.Builder(this).setTitle(title).setMessage(msg).show()
    }

    protected fun alert():AlertDialog.Builder{
        return AlertDialog.Builder(this)
    }

}
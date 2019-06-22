package liang.lollipop.lcountdown.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_widget_list.*
import kotlinx.android.synthetic.main.content_widget_list.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lbaselib.util.SimpleRecyclerViewHelper
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.adapter.WidgetListAdapter
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.utils.WidgetDBUtil
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.widget.CountdownWidget
import java.util.*

/**
 * 小部件的集合列表
 * @author Lollipop
 */
class WidgetListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {

        private const val WHAT_UPDATE = 99

    }

    private lateinit var sqlDB: WidgetDBUtil.SqlDB

    private val widgetList = ArrayList<WidgetBean>()

    private lateinit var adapter: WidgetListAdapter

    private var isLoading = false

    private var isChangeManual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_list)
        setToolbar(toolbar)
        sqlDB = WidgetDBUtil.write(this)
        initView()
        WidgetUtil.alarmUpdate(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dream,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.menuDream -> {

                alert().setMessage(R.string.msg_alert_dream)
                        .setPositiveButton(R.string.goto_open){ dialog, _ ->
                            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                            dialog.dismiss()
                        }
                        .show()

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView(){

        refreshLayout.setOnRefreshListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val helper = SimpleRecyclerViewHelper.create<WidgetBean>().apply {
            recyclerView = this@WidgetListActivity.recyclerView
            dataList = this@WidgetListActivity.widgetList
            undoValue = getString(R.string.undo)
            deleteValue = getString(R.string.deleted)
            callback = object :SimpleRecyclerViewHelper.SimpleCallback<WidgetBean>(){
                override fun onMove(src: WidgetBean, target: WidgetBean, srcPosition: Int, targetPosition: Int): Boolean {
                    isChangeManual = true
                    return true
                }
            }
        }.buildToTouchHelper().apply {
            setStateChangedListener(this@WidgetListActivity)
            setCanDrag(true)
            setCanSwipe(false)
        }
        adapter = WidgetListAdapter(widgetList,layoutInflater,helper)
        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

    }

    override fun onItemTouchStateChanged(viewHolder: RecyclerView.ViewHolder?, status: Int) {
        super.onItemTouchStateChanged(viewHolder, status)
        if (!isChangeManual) {
            //如果数据没有被修改，那么就放弃本次上传操作
            return
        }
        if (status == ItemTouchHelper.ACTION_STATE_IDLE){
            for((index, bean) in widgetList.withIndex()){
                bean.index = index
            }
            sqlDB.updateAll(widgetList)
            onRefresh()
        }
    }

    override fun onStart() {
        super.onStart()
        val delayed = ( System.currentTimeMillis() / WidgetUtil.UPDATE_TIME + 1 ) * WidgetUtil.UPDATE_TIME
        handler.sendEmptyMessageDelayed(WHAT_UPDATE,delayed)

        if(isLoading){
            return
        }
        onRefresh()
    }

    override fun onRefresh() {
        super.onRefresh()
        isLoading = true
        sqlDB.getAll(widgetList)

        adapter.notifyDataSetChanged()
        isLoading = false
        refreshLayout.isRefreshing = false
    }

    override fun onItemViewClick(holder: RecyclerView.ViewHolder?, v: View) {
        super.onItemViewClick(holder, v)
        if(holder == null){
            return
        }

        val index = holder.adapterPosition
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetList[index].widgetId)
        intent.putExtra(CountdownWidget.WIDGET_SHOW,1)
        startActivity(intent)
    }

    override fun onHandler(message: Message) {

        when(message.what){

            WHAT_UPDATE -> {

                adapter.notifyDataSetChanged()
                handler.sendEmptyMessageDelayed(WHAT_UPDATE, WidgetUtil.UPDATE_TIME)

            }

        }

    }

    override fun onStop() {
        super.onStop()
        handler.removeMessages(WHAT_UPDATE)
    }

}

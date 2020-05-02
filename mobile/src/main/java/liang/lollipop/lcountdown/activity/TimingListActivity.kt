package liang.lollipop.lcountdown.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_timing_list.*
import kotlinx.android.synthetic.main.content_timing_list.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lcountdown.LApplication
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.adapter.TimingListAdapter
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.holder.TimingHolder
import liang.lollipop.lcountdown.service.FloatingService
import liang.lollipop.lcountdown.utils.LogHelper
import liang.lollipop.lcountdown.utils.TimingUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * 计时列表的Activity
 * @author Lollipop
 */
class TimingListActivity : BaseActivity() {

    private lateinit var timingUtil: TimingUtil

    private val dataList = ArrayList<TimingBean>()

    private lateinit var adapter: TimingListAdapter

    companion object {

        private const val REQUEST_NEW_TIMING = 123

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timing_list)
        setToolbar(toolbar)

        timingUtil = TimingUtil.write(this)

        initView()
    }

    private fun initView() {

        quickTimingBtn.setOnClickListener(this)

        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val helper = getTouchHelper(recyclerView)
        helper.setCanSwipe(true)
        adapter = TimingListAdapter(dataList, layoutInflater, helper)
        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuList -> {
                startActivity(Intent(this, WidgetListActivity::class.java))
                return true
            }
            R.id.menuCalculator -> {
                startActivity(Intent(this, TimeCalculatorActivity::class.java))
                return true
            }
            R.id.clearLog -> {
                val file = (application as LApplication).logDir
                val size = file.let {
                    if (it.exists() && it.isDirectory && it.canRead()) {
                        it.listFiles()?.size?:0
                    } else {
                        0
                    }
                }
                AlertDialog.Builder(this)
                        .setMessage(String.format(
                                getString(R.string.clear_log_message), size))
                        .setNegativeButton(R.string.clear_log_enter) { dialog, _ ->
                            LogHelper.clearLog((application as LApplication).logDir)
                            Snackbar.make(recyclerView, R.string.clear_log_end, Snackbar.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        getData()
    }

    override fun onSwiped(adapterPosition: Int) {
        super.onSwiped(adapterPosition)
        val bean = dataList.removeAt(adapterPosition)
        adapter.notifyItemRemoved(adapterPosition)

        Snackbar.make(recyclerView, R.string.deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    dataList.add(adapterPosition, bean)
                    adapter.notifyItemInserted(adapterPosition)
                }
                .addCallback(OnSwipedHelper(timingUtil, bean)).show()
    }

    private class OnSwipedHelper(
            private val timingUtil: TimingUtil,
            private val bean: TimingBean) : BaseTransientBottomBar.BaseCallback<Snackbar>() {

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)

            if (event != DISMISS_EVENT_ACTION) {

                timingUtil.deleteTiming(bean.id)

            }

        }

    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v) {
            quickTimingBtn -> {
                startActivityForResult(
                        Intent(this, QuickTimingActivity::class.java),
                        REQUEST_NEW_TIMING,
                        androidx.core.util.Pair.create(v, QuickTimingActivity.QUIET_BTN_TRANSITION))
            }

        }

    }

    private fun getData() {

        doAsync {

            dataList.clear()
            timingUtil.selectAll(dataList)

            uiThread {

                adapter.notifyDataSetChanged()
                refreshLayout.isRefreshing = false

                if (dataList.isEmpty()) {

                    Snackbar.make(recyclerView, "你现在还没有计时项目", Snackbar.LENGTH_LONG).show()

                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            REQUEST_NEW_TIMING -> if (resultCode == Activity.RESULT_OK && data != null) {

                val id = data.getIntExtra(QuickTimingActivity.RESULT_TIMING_ID, 0)
                if (id > 0) {
                    val bean = TimingBean(id)
                    timingUtil.selectOne(bean)
                    dataList.add(0, bean)
                    adapter.notifyItemInserted(0)
                }

            }

        }

    }

    override fun onResume() {
        super.onResume()

        onRefresh()

    }

    override fun onItemViewClick(holder: RecyclerView.ViewHolder?, v: View) {
        super.onItemViewClick(holder, v)

        if (holder == null) {
            return
        }

        if (holder is TimingHolder) {
            when (v) {
                holder.stopBtn -> {
                    val bean = dataList[holder.adapterPosition]
                    timingUtil.stopTiming(bean.id).selectOne(bean)
                    adapter.notifyItemChanged(holder.adapterPosition)
                    FloatingService.start(this, bean, true)
                }

                holder.floatingBtn -> {
                    val bean = dataList[holder.adapterPosition]
                    FloatingService.start(this, bean, false)
                }
            }
        }

    }

}

package liang.lollipop.lcountdown.activity

import android.animation.Animator
import android.animation.ValueAnimator
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
import liang.lollipop.lcountdown.LApplication
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.adapter.TimingListAdapter
import liang.lollipop.lcountdown.base.BaseActivity
import liang.lollipop.lcountdown.bean.PhotoInfo
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.fragment.CountdownImageFragment
import liang.lollipop.lcountdown.holder.TimingHolder
import liang.lollipop.lcountdown.service.FloatingService
import liang.lollipop.lcountdown.utils.*


/**
 * 计时列表的Activity
 * @author Lollipop
 */
class TimingListActivity : BaseActivity(),
        CountdownImageFragment.Callback{

    private lateinit var timingUtil: TimingUtil

    private val dataList = ArrayList<TimingBean>()

    private lateinit var adapter: TimingListAdapter

    private var selectedInfo: Int = 0

    private lateinit var sheetHelper: BottomSheetHelper

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

        sheetHelper = BottomSheetHelper(imagePanel)
        sheetHelper.onStateChange {
            if (it == BottomSheetHelper.State.COLLAPSED) {
                selectedInfo = 0
            }
            updateButton(it)
        }
        sheetBtn.setOnClickListener(this)
        sheetHelper.close(false)
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

    private fun selectedItem(position: Int) {
        selectedInfo = dataList[position].id
        sheetHelper.expand(true)
    }

    private fun updateButton(newState: BottomSheetHelper.State) {
        val rotation = if (BottomSheetHelper.State.EXPANDED == newState) {
            180F
        } else {
            0F
        }
        sheetBtn.animate().let {
            it.cancel()
            it.rotation(rotation).start()
        }
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

            sheetBtn -> {
                sheetHelper.reverse()
            }

        }

    }

    private fun getData() {

        doAsync {
            dataList.clear()
            timingUtil.selectAll(dataList)
            onUI {
                adapter.notifyDataSetChanged()
                refreshLayout.isRefreshing = false
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
                holder.itemView -> {
                    selectedItem(holder.adapterPosition)
                }
            }
        }

    }

    private class BottomSheetHelper(private val sheetView: View) :
            ValueAnimator.AnimatorUpdateListener,
            Animator.AnimatorListener {

        private val valueAnimator = ValueAnimator().apply {
            addUpdateListener(this@BottomSheetHelper)
            addListener(this@BottomSheetHelper)
        }

        companion object {
            private const val MIN = 0F
            private const val MAX = 1F
            private const val DURATION = 300L
        }

        enum class State {
            NOTHING,
            EXPANDED,
            COLLAPSED,
            SCROLLING
        }

        private var progress = 0F
        var state = State.NOTHING
            private set
        private var isOpen = false

        private var stateListener: ((State) -> Unit)? = null

        fun onStateChange(listener: (State) -> Unit) {
            this.stateListener = listener
        }

        private fun changeState(s: State) {
            if (s != state) {
                state = s
                stateListener?.invoke(s)
            }
        }

        fun close(isAnimator: Boolean = true) {
            isOpen = false
            post {
                if (!isAnimator) {
                    changeState(State.COLLAPSED)
                    onProgressChange(MIN)
                } else {
                    changeState(State.SCROLLING)
                    valueAnimator.cancel()
                    valueAnimator.setFloatValues(progress, MIN)
                    valueAnimator.duration = ((progress - MIN) / (MAX - MIN) * DURATION).toLong()
                    valueAnimator.start()
                }
            }
        }

        fun expand(isAnimator: Boolean = true) {
            isOpen = true
            post {
                if (!isAnimator) {
                    changeState(State.EXPANDED)
                    onProgressChange(MAX)
                } else {
                    changeState(State.SCROLLING)
                    valueAnimator.cancel()
                    valueAnimator.setFloatValues(progress, MAX)
                    valueAnimator.duration = ((MAX - progress) / (MAX - MIN) * DURATION).toLong()
                    valueAnimator.start()
                }
            }
        }

        fun reverse(isAnimator: Boolean = true) {
            if (isOpen) {
                close(isAnimator)
            } else {
                expand(isAnimator)
            }
        }

        fun end() {
            if (valueAnimator.isRunning) {
                valueAnimator.end()
            }
        }

        private fun post(run: () -> Unit) {
            sheetView.post(run)
        }

        private fun onProgressChange(value: Float) {
            progress = value
            val offsetMax = sheetView.height
            sheetView.translationY = offsetMax * (1 - value)
        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            if (animation == valueAnimator) {
                onProgressChange(animation.animatedValue as Float)
            }
        }

        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            if (animation == valueAnimator) {
                changeState(if (isOpen) {
                    State.EXPANDED
                } else {
                    State.COLLAPSED
                })
            }
        }

        override fun onAnimationCancel(animation: Animator?) {  }

        override fun onAnimationStart(animation: Animator?) {
            if (animation == valueAnimator) {
                changeState(State.SCROLLING)
            }
        }
    }

    override fun onImageSelected(info: PhotoInfo) {
        if (selectedInfo == 0) {
            sheetHelper.close(true)
            return
        }
        doAsync {
            val selected = selectedInfo
            if (info == PhotoInfo.Empty) {
                FileUtil.removeTimerImage(this, selected)
            } else {
                FileUtil.copyTimer(this, info.path, selected)
            }
            var position = -1
            for (index in dataList.indices) {
                if (dataList[index].id == selected) {
                    position = index
                }
            }
            if (position >= 0) {
                onUI {
                    adapter.notifyItemChanged(position)
                }
            }
        }
        sheetHelper.close(true)
    }

}

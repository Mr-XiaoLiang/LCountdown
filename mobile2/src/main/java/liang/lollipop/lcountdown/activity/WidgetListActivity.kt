package liang.lollipop.lcountdown.activity

import android.appwidget.AppWidgetManager
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_widget_list.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.engine.WidgetEngine
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.util.list.DirectionInfo
import liang.lollipop.lcountdown.util.list.ListTouchHelper
import liang.lollipop.lcountdown.util.list.SwipeableHolder

/**
 * 小部件列表
 * 包括已展示的小部件、未展示的小部件
 */
class WidgetListActivity : AppBarActivity() {

    companion object {
        private const val TYPE_ALL = 0
        private const val TYPE_SHOWN = 1
        private const val TYPE_HIDE = 2
    }

    override val layoutId: Int
        get() = R.layout.activity_widget_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetPageAdapter = WidgetPageAdapter(supportFragmentManager, resources)
        viewPager.adapter = widgetPageAdapter
        viewPager.offscreenPageLimit = widgetPageAdapter.count
        tabLayout.setupWithViewPager(viewPager, true)
    }

    private class WidgetPageAdapter(fragmentManager: FragmentManager,
                                    private val res: Resources)
        : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentArray = arrayOf(
                WidgetListFragment.create(TYPE_ALL),
                WidgetListFragment.create(TYPE_SHOWN),
                WidgetListFragment.create(TYPE_HIDE),
        )

        override fun getCount(): Int {
            return fragmentArray.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentArray[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return res.getString(fragmentArray[position].title)
        }

    }

    private class WidgetListFragment: Fragment() {

        companion object {
            private const val ARG_TYPE = "TYPE"

            fun create(type: Int): WidgetListFragment {
                val fragment = WidgetListFragment()
                val arguments = fragment.arguments?:Bundle()
                arguments.putInt(ARG_TYPE, type)
                fragment.arguments = arguments
                return fragment
            }
        }

        private val widgetData = ArrayList<WidgetInfo>()
        private val adapter = WidgetAdapter(widgetData, ::onItemClick)

        private var type = TYPE_ALL

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            type = arguments?.getInt(ARG_TYPE)?:TYPE_ALL
        }

        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?): View? {
            container?:return null
            return RecyclerView(container.context)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            if (view is RecyclerView) {
                view.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
                view.adapter = adapter
                ListTouchHelper.with(view)
                        .moveOrientation(DirectionInfo.NONE)
                        .swipeOrientation(DirectionInfo.HORIZONTAL)
                        .onSwiped(::onItemSwipe)
                adapter.notifyDataSetChanged()
            }
        }

        override fun onStart() {
            super.onStart()
            loadData()
        }

        val title: Int
            get() {
                return when (type) {
                    TYPE_SHOWN -> { R.string.widget_list_shown }
                    TYPE_HIDE -> { R.string.widget_list_hide }
                    else -> { R.string.widget_list_all }
                }
            }

        private fun loadData() {
            // TODO
        }

        private fun onItemClick(index: Int) {
            // TODO
        }

        private fun onItemSwipe(
                viewHolder: RecyclerView.ViewHolder,
                direction: ListTouchHelper.Direction) {
            // TODO
        }

    }

    private class WidgetAdapter(
            private val data: ArrayList<WidgetInfo>,
            private val onClick: (Int) -> Unit): RecyclerView.Adapter<WidgetHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetHolder {
            return WidgetHolder.create(parent, ::onHolderClick)
        }

        override fun onBindViewHolder(holder: WidgetHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        private fun onHolderClick(holder: WidgetHolder) {
            onClick(holder.adapterPosition)
        }

    }

    private class WidgetHolder
        private constructor(view: View,
                            private val onClick: (WidgetHolder) -> Unit): RecyclerView.ViewHolder(view), SwipeableHolder {

            companion object {
                fun create(
                        group: ViewGroup,
                        onClick: (WidgetHolder) -> Unit): WidgetHolder {
                    return WidgetHolder(
                            LayoutInflater.from(group.context)
                                    .inflate(R.layout.item_widget_list,
                                            group, false),
                            onClick)
                }
            }

        private val widgetRoot = itemView.findViewById<FrameLayout>(R.id.widgetRoot)

        private val widgetEngine = WidgetEngine(widgetRoot)

        private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

        init {
            widgetRoot.setOnClickListener {
                onClick(this)
            }
        }

        fun bind(info: WidgetInfo) {
            widgetEngine.updateAll(info)
        }

        override fun canSwipe(): DirectionInfo {
            return if (widgetId == -1 ||
                    widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                DirectionInfo.NONE
            } else {
                DirectionInfo.HORIZONTAL
            }
        }

    }

}
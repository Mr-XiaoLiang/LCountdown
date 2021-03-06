package liang.lollipop.lcountdown.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * @date: 2018/6/26 18:21
 * @author: lollipop
 *
 * 简单的RV帮助工具
 */
class SimpleRecyclerViewHelper<E>(option: HelperBuilder<E>): LItemTouchCallback.OnItemTouchCallbackListener {

    private lateinit var adapter: RecyclerView.Adapter<*>

    private val deleteValue = option.deleteValue

    private val undoValue = option.undoValue

    private val recyclerView = option.recyclerView

    private val dataList: List<E> = option.dataList

    private val callback = option.callback

    companion object {

        fun <E> create(): HelperBuilder<E> {
            return HelperBuilder()
        }

    }

    class HelperBuilder<E> {

        lateinit var recyclerView: RecyclerView

        lateinit var dataList: List<E>

        lateinit var callback: OnRecyclerChangeCallback<E>

        var deleteValue = "deleted"

        var undoValue = "undo"

        fun buildToTouchHelper(): LItemTouchHelper {
            return LItemTouchHelper.newInstance(recyclerView, buildToHelper())
        }

        fun buildToHelper(): SimpleRecyclerViewHelper<E> {

            if(!this::recyclerView.isInitialized){
                throw RuntimeException("recyclerView.isInitialized = false")
            }

            if(!this::dataList.isInitialized){
                throw RuntimeException("dataList.isInitialized = false")
            }

            if(!this::callback.isInitialized){
                throw RuntimeException("callback.isInitialized = false")
            }

            return SimpleRecyclerViewHelper(this)
        }

    }

    override fun onSwiped(adapterPosition: Int) {

        if(!this::adapter.isInitialized){
            adapter = recyclerView.adapter as RecyclerView.Adapter<*>
        }

        val bean = when (dataList) {
            is ArrayList -> dataList.removeAt(adapterPosition)
            is LinkedList -> dataList.removeAt(adapterPosition)
            else -> throw RuntimeException("Unknow dataList")
        }
        adapter.notifyItemRemoved(adapterPosition)

        Snackbar.make(recyclerView,deleteValue,Snackbar.LENGTH_LONG)
                .setAction(undoValue) {
                    when (dataList) {
                        is ArrayList -> dataList.add(adapterPosition,bean)
                        is LinkedList -> dataList.add(adapterPosition,bean)
                    }
                    adapter.notifyItemInserted(adapterPosition)
                }
                .addCallback(OnSwipedHelper(bean, callback)).show()

    }

    override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {

        if(!this::adapter.isInitialized){
            adapter = recyclerView.adapter as RecyclerView.Adapter<*>
        }

        val srcBean =dataList[srcPosition]

        val targetBean = dataList[targetPosition]

        return if(callback.onMove(srcBean,targetBean,srcPosition,targetPosition)){
            Collections.swap(dataList, srcPosition, targetPosition)
            adapter.notifyItemMoved(srcPosition, targetPosition)
            true
        }else{
            false
        }

    }

    private class OnSwipedHelper<E>(private val bean: E,
                                    private val callback: OnRecyclerChangeCallback<E>)
        : BaseTransientBottomBar.BaseCallback<Snackbar>() {

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)

            if(event != DISMISS_EVENT_ACTION){

                callback.onSwiped(bean)

            }

        }

    }

    interface OnRecyclerChangeCallback<E>{

        fun onSwiped(data: E)

        fun onMove(src: E,target: E,srcPosition: Int, targetPosition: Int): Boolean

        fun onItemViewClick(holder: RecyclerView.ViewHolder, v: View)

    }

    open class SimpleCallback<E>: OnRecyclerChangeCallback<E> {
        override fun onSwiped(data: E) {
        }

        override fun onMove(src: E, target: E, srcPosition: Int, targetPosition: Int): Boolean {
            return false
        }

        override fun onItemViewClick(holder: RecyclerView.ViewHolder, v: View) {
        }

    }

    override fun onItemViewClick(holder: RecyclerView.ViewHolder?, v: View) {

        if(holder == null){
            return
        }

        callback.onItemViewClick(holder,v)

    }


}
package liang.lollipop.lbaselib.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.lbaselib.util.LItemTouchHelper

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 基础的Item Holder
 */
abstract class BaseHolder<in T:BaseBean>(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener {

    protected var touch: LItemTouchHelper? = null
    protected var canSwipe = false
    protected var canMove = false
    protected val context:Context = itemView.context

    init {
        itemView.setOnClickListener(this)
    }

    abstract fun onBind(bean:T)

    fun canSwipe():Boolean{
        return canSwipe
    }

    fun canMove():Boolean{
        return canMove
    }

    override fun onClick(v: View?) {
        if(v!=null && touch!=null){
            touch!!.onItemViewClick(this,v)
        }
    }

    fun setTouchHelper(helper:LItemTouchHelper){
        touch = helper
    }

    protected fun <T: View> find(id: Int): T{
        return itemView.findViewById(id)
    }

}
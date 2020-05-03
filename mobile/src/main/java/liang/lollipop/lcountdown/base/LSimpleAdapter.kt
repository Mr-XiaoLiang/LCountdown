package liang.lollipop.lcountdown.base

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 简化后的适配器组件
 */
abstract class LSimpleAdapter<H: BaseHolder<B>, B: BaseBean>(private val data:List<B>): RecyclerView.Adapter<H>() {

    override fun onBindViewHolder(holder: H, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].beanType
    }

}
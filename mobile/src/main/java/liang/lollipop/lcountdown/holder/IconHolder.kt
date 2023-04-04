package liang.lollipop.lcountdown.holder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import liang.lollipop.lcountdown.bean.IconBean
import liang.lollipop.lcountdown.databinding.DreamIconBinding
import liang.lollipop.lcountdown.utils.bind

/**
 * Icon显示的Holder
 * @author Lollipop
 */
class IconHolder(private val binding: DreamIconBinding) {


    private val iconView: ImageView
        get() {
            return binding.iconImage
        }

    private val body: View
        get() {
            return binding.root
        }

    var bean: IconBean? = null

    companion object {

        fun getInstance(viewGroup: ViewGroup): IconHolder {
            return IconHolder(viewGroup.bind(false))
        }

    }

    fun onBind(bean: IconBean) {
        this.bean = bean
        iconView.setImageDrawable(bean.icon)
    }

    fun addTo(viewGroup: ViewGroup?) {
        remove()
        viewGroup?.addView(body)
    }

    fun remove() {
        try {
            if (body.parent != null) {
                val group = body.parent as ViewGroup
                group.removeView(body)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
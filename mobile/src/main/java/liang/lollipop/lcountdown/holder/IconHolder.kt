package liang.lollipop.lcountdown.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.IconBean

/**
 * Icon显示的Holder
 * @author Lollipop
 */
class IconHolder(private val body: View) {


    private val iconView: ImageView = body.findViewById(R.id.iconImage)

    var bean: IconBean? = null

    companion object {

        private const val LAYOUT_ID = R.layout.dream_icon

        fun getInstance(inflater: LayoutInflater, viewGroup: ViewGroup): IconHolder {
            return IconHolder(inflater.inflate(LAYOUT_ID, viewGroup, false))
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
        }

    }

}
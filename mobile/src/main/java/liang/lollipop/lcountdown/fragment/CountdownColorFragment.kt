package liang.lollipop.lcountdown.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean

/**
 * @author lollipop
 * @date 2019-11-17 21:44
 * 设置颜色的Fragment
 */
class CountdownColorFragment: LTabFragment() {

    override fun getTitleId(): Int {
        return R.string.title_color_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_color_lens_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.colorTabSelected
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_color, container, false)
    }

    fun reset(bean: WidgetBean) {
        // TODO
    }

}
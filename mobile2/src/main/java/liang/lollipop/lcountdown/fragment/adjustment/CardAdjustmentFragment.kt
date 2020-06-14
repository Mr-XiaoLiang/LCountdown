package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.fragment.BaseFragment

/**
 * @author lollipop
 * @date 2020/6/14 22:01
 */
abstract class CardAdjustmentFragment: BaseFragment() {

    abstract val layoutId: Int

    abstract val icon: Int

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_adjustment_base, container, false)
        if (layoutId != 0) {
            inflater.inflate(layoutId, rootView.findViewById(R.id.panelBodyGroup))
        }
        return rootView
    }

}
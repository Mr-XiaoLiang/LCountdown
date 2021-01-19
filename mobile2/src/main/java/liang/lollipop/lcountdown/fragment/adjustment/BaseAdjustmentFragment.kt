package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.fragment.BaseFragment
import liang.lollipop.lcountdown.listener.WidgetInfoStatusListener
import liang.lollipop.lcountdown.provider.WidgetInfoStatusProvider

/**
 * @author lollipop
 * @date 2020/6/14 22:01
 */
abstract class BaseAdjustmentFragment: BaseFragment(), WidgetInfoStatusListener {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WidgetInfoStatusProvider) {
            context.registerWidgetInfoStatusListener(this)
        } else {
            parentFragment?.let { it ->
                if (it is WidgetInfoStatusProvider) {
                    it.registerWidgetInfoStatusListener(this)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        context?.let {
            if (it is WidgetInfoStatusProvider) {
                it.unregisterWidgetInfoStatusListener(this)
            }
        }
        parentFragment?.let { it ->
            if (it is WidgetInfoStatusProvider) {
                it.unregisterWidgetInfoStatusListener(this)
            }
        }
    }

    override fun onWidgetInfoChange() { }


}
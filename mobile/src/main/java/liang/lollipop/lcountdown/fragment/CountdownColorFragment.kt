package liang.lollipop.lcountdown.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_countdown_color.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.utils.WidgetUtil
import liang.lollipop.lcountdown.view.ExpandButton

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

    private var selectedTarget = WidgetUtil.Target.Nothing

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        expandBtnGroup.onSelectedBtnChange {
            onTargetChange(it)
//            onLocationChange()
        }
    }

    private fun onTargetChange(view: ExpandButton) {
        selectedTarget = when (view) {
            titleModeBtn -> WidgetUtil.Target.Name
            prefixModeBtn -> WidgetUtil.Target.Prefix
            suffixModeBtn -> WidgetUtil.Target.Suffix
            daysModeBtn -> WidgetUtil.Target.Days
            unitModeBtn -> WidgetUtil.Target.Unit
            timeModeBtn -> WidgetUtil.Target.Time
            signModeBtn -> WidgetUtil.Target.Inscription
            else -> WidgetUtil.Target.Nothing
        }
//        val info = locationInfoProvider?.getLocationInfo(selectedTarget)?:emptyInfo
    }

    fun reset(bean: WidgetBean) {
        // TODO
    }

}
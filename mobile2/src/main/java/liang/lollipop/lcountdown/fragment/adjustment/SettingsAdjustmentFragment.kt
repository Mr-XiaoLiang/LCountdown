package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_adjustment_settings.*
import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 2020/6/14 23:09
 * 偏好设置的碎片
 */
class SettingsAdjustmentFragment : CardAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_settings

    override val icon = R.drawable.ic_baseline_settings_24

    override val title = R.string.title_settings

    override val colorId = R.color.focusSettingsAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testBtn.setOnClickListener {
            testProgressLayout.progress = 0.5F
            testProgressLayout.startLoad()
        }
    }
}
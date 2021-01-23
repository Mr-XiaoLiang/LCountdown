package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_adjustment_settings.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.WidgetPart

/**
 * @author lollipop
 * @date 2020/6/14 23:09
 * 偏好设置的碎片
 */
class SettingsAdjustmentFragment : BaseAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_settings

    override val icon = R.drawable.ic_baseline_settings_24
    override val adjustmentPart: WidgetPart
        get() = WidgetPart.NONE

    override val title = R.string.title_settings

    override val colorId = R.color.focusSettingsAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testProgressLayout.strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3F, resources.displayMetrics)
        testProgressLayout.selected = ContextCompat.getColor(view.context, colorId)
        var i = 0
        testBtn.setOnClickListener {
            when(i) {
                0 -> {
                    testProgressLayout.startLoad()
                    testBtn.text = "正在加载"
                    i = 1
                }
                1 -> {
                    testProgressLayout.loadSuccess()
                    testBtn.text = "加载成功"
                    i = 2
                }
                2 -> {
                    testProgressLayout.loadFailure()
                    testBtn.text = "加载失败"
                    i = 0
                }
            }
        }
        loadBtn.setOnClickListener {
            testProgressLayout.startLoad()
        }
    }
}
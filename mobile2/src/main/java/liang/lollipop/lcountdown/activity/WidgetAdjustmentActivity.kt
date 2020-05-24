package liang.lollipop.lcountdown.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_widget_adjustment.*
import liang.lollipop.lcountdown.R

/**
 * 小部件的调整页面
 */
class WidgetAdjustmentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_adjustment)
        initRootGroup(rootGroup)
    }
}

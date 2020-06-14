package liang.lollipop.lcountdown.activity

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_widget_adjustment.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.util.BottomSheetHelper

/**
 * 小部件的调整页面
 */
class WidgetAdjustmentActivity : BaseActivity() {

    private var bottomSheetHelper: BottomSheetHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_adjustment)
        initRootGroup(rootGroup)
        bottomSheetHelper = BottomSheetHelper(sheetPanel, panelGrip, widgetFrame, sheetContent)
        bottomSheetHelper?.show(false)
        panelGrip.setOnClickListener {
            bottomSheetHelper?.changeStatus()
        }
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        tabGroup.setPadding(left, 0, right, bottom)
        bottomSheetHelper?.paddingBottom = bottom
    }

    override fun onStop() {
        super.onStop()
        bottomSheetHelper?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetHelper?.destroy()
    }

}

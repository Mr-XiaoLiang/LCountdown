package liang.lollipop.lcountdown.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_bottom_bar.*
import liang.lollipop.lcountdown.R

/**
 * 底部AppBar的Activity
 */
abstract class BottomBarActivity : BaseActivity() {

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_bar)
        initRootGroup(rootGroup)
    }
}

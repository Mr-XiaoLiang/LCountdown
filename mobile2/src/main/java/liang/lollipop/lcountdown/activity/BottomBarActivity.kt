package liang.lollipop.lcountdown.activity

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_bottom_bar.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.WindowInsetsHelper

/**
 * 底部AppBar的Activity
 */
abstract class BottomBarActivity : BaseActivity() {

    abstract val layoutId: Int

    private val contentGroupInsetsHelper: WindowInsetsHelper by lazy {
        WindowInsetsHelper(contentGroup)
    }
    private val appBarInsetsHelper: WindowInsetsHelper by lazy {
        WindowInsetsHelper(appBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_bar)
        layoutInflater.inflate(layoutId, contentGroup)
        initRootGroup(rootGroup)
        setSupportActionBar(appBar)

        appBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        contentGroupInsetsHelper.updateByPadding(root, left, top, right, bottom)
        appBarInsetsHelper.updateByPadding(root, left, top, right, bottom)
    }

}

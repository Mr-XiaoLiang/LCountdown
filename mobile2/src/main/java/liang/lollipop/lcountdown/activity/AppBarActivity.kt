package liang.lollipop.lcountdown.activity

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_app_bar.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.WindowInsetsHelper

/**
 * 底部AppBar的Activity
 */
abstract class AppBarActivity : BaseActivity() {

    abstract val layoutId: Int

    private val contentGroupInsetsHelper: WindowInsetsHelper by lazy {
        WindowInsetsHelper(contentGroup)
    }

    private val headGroupInsetsHelper: WindowInsetsHelper by lazy {
        WindowInsetsHelper(appBarLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_bar)
        layoutInflater.inflate(layoutId, contentGroup)
        setSupportActionBar(toolbar)
        updateHeadHeight()
        initRootGroup(rootGroup)
        title = getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateHeadHeight() {
        rootGroup.post {
            val rootHeight = rootGroup.height
            val collapsingLayoutParams = collapsingToolbarLayout.layoutParams
            collapsingLayoutParams.height = rootHeight / 3
            collapsingToolbarLayout.layoutParams = collapsingLayoutParams
        }
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        headGroupInsetsHelper.updateByPadding(root, left, top, right, bottom)
        contentGroupInsetsHelper.setInsetsByPadding(left, 0, right, 0)
        contentGroupInsetsHelper.setInsetsByMargin(0, 0, 0, bottom)
    }

}
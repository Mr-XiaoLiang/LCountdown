package liang.lollipop.lcountdown.activity

import android.os.Bundle
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseActivity
import liang.lollipop.lcountdown.databinding.ActivityGradientBinding
import liang.lollipop.lcountdown.utils.lazyBind

/**
 * 渐变色生成的Activity
 */
class GradientActivity : BaseActivity() {

    private val binding: ActivityGradientBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}

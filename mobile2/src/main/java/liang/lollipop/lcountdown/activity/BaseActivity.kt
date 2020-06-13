package liang.lollipop.lcountdown.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import liang.lollipop.lcountdown.listener.*

/**
 * @author lollipop
 * @date 2020/5/22 00:46
 */
@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity(),
    BackPressedProvider,
    OnWindowInsetsProvider{

    private val windowInsetsProviderHelper: WindowInsetsProviderHelper by lazy {
        WindowInsetsProviderHelper()
    }

    private val backPressedProviderHelper: BackPressedProviderHelper by lazy {
        BackPressedProviderHelper()
    }

    protected fun initRootGroup(group: View) {
//        val attributes = window.attributes
//        attributes.systemUiVisibility = (
//                attributes.systemUiVisibility
//                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT

        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { _, insets ->
            onInsetsChange(group, insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            windowInsetsProviderHelper.onInsetsChange(group,
                    insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
        }
    }

    protected open fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        root.setPadding(left, top, right, bottom)
    }

    override fun addBackPressedListener(listener: BackPressedListener) {
        backPressedProviderHelper.addBackPressedListener(listener)
    }

    override fun removeBackPressedListener(listener: BackPressedListener) {
        backPressedProviderHelper.removeBackPressedListener(listener)
    }

    override fun addOnWindowInsetsProvider(listener: OnWindowInsetsListener) {
        windowInsetsProviderHelper.addOnWindowInsetsProvider(listener)
    }

    override fun removeOnWindowInsetsProvider(listener: OnWindowInsetsListener) {
        windowInsetsProviderHelper.removeOnWindowInsetsProvider(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowInsetsProviderHelper.destroy()
        backPressedProviderHelper.destroy()
    }

    override fun onBackPressed() {
        if (backPressedProviderHelper.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

}
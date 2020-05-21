package liang.lollipop.lcountdown.activity

import android.annotation.SuppressLint
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
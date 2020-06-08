package liang.lollipop.lcountdown.fragment

import androidx.fragment.app.Fragment
import liang.lollipop.lcountdown.listener.*

/**
 * @author lollipop
 * @date 2020/6/3 00:28
 */
abstract class BaseFragment: Fragment(),
        BackPressedListener,
        BackPressedProvider,
        OnWindowInsetsProvider {

    abstract val title: Int

    abstract val colorId: Int

    private val windowInsetsProviderHelper: WindowInsetsProviderHelper by lazy {
        WindowInsetsProviderHelper()
    }

    private val backPressedProviderHelper: BackPressedProviderHelper by lazy {
        BackPressedProviderHelper()
    }

    override fun onBackPressed(): Boolean {
        return backPressedProviderHelper.onBackPressed()
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
}
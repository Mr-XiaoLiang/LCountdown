package liang.lollipop.lcountdown.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
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

    private var lifecycleHelper: FragmentLifecycleHelper = FragmentLifecycleHelper()

    abstract val title: Int

    abstract val colorId: Int

    protected fun supportLifecycle(fragment: Fragment) {
        lifecycleHelper.bindFragment(fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportLifecycle(this)
        super.onCreate(savedInstanceState)
        lifecycleHelper.onCreate(this, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleHelper.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        lifecycleHelper.onStart()
    }

    override fun onResume() {
        super.onResume()
        lifecycleHelper.onResume()
    }

    override fun onPause() {
        super.onPause()
        lifecycleHelper.onPause()
    }

    override fun onStop() {
        super.onStop()
        lifecycleHelper.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleHelper.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleHelper.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycleHelper.onDetach()
    }

    fun addLifecycleListener(listener: FragmentLifecycleListener) {
        lifecycleHelper.addLifecycleListener(listener)
    }

    fun removeLifecycleListener(listener: FragmentLifecycleListener) {
        lifecycleHelper.removeLifecycleListener(listener)
    }

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
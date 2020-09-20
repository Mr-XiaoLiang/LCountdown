package liang.lollipop.lcountdown.listener

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * @author lollipop
 * @date 9/21/20 01:33
 */
interface FragmentLifecycleListener<T: Fragment>: LifecycleListener<T> {

    fun onAttach(target: T, context: Context)

    fun onDetach(target: T)

}